package org.comtel.mt.msc.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.comtel.mt.msc.gui.AspectRatioTool.AR;
import org.comtel.mt.msc.gui.communication.VdmaChannelWriterThread;
import org.comtel.mt.msc.gui.communication.VdmaMessageProcessor;
import org.comtel.mt.msc.gui.communication.VdmaXmlChannelReaderThread;
import org.comtel.mt.msc.gui.model.MscGuiContext;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.DrawFrame;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.DrawRectArea;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.PriStrBsp;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.SndPICtoGUI;
import org.slf4j.LoggerFactory;

public class SwingTest extends JFrame implements VdmaMessageProcessor {

	private static final long serialVersionUID = 1L;

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SwingTest.class);

	private final MscGuiContext loader = new MscGuiContext();

	private final SimpleStringProperty ipAddress = new SimpleStringProperty("192.168.100.217");

	private final SimpleStringProperty status = new SimpleStringProperty();

	private final SimpleObjectProperty<BufferedImage> ovlImage = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);

	private VdmaXmlChannelReaderThread readerThread;
	private VdmaChannelWriterThread writerThread;

	private final JLabel imageLabel = new JLabel();

	public SwingTest() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setSize(800, 600);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanel(), BorderLayout.NORTH);
		getContentPane().add(imageLabel, BorderLayout.CENTER);

		ovlImage.addListener(new ChangeListener<BufferedImage>() {

			@Override
			public void changed(ObservableValue<? extends BufferedImage> observable, BufferedImage oldValue,
					BufferedImage newValue) {
				imageLabel.setIcon(new ImageIcon(newValue));
			}
		});
		setVisible(true);
	}

	private Component getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		final JButton connect = new JButton("connect");
		final JButton disconnect = new JButton("disconnect");
		final JTextField ipText = new JTextField("192.168.100.217");

		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
				((Component) e.getSource()).setEnabled(false);
				disconnect.setEnabled(true);
				ipText.setEditable(false);
			}
		});

		disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				((Component) e.getSource()).setEnabled(false);
				connect.setEnabled(true);
				ipText.setEditable(true);
				imageLabel.setIcon(null);
			}
		});

		ipText.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				ipAddress.set(((JTextField) e.getSource()).getText());
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		panel.add(ipText);
		panel.add(connect);
		panel.add(disconnect);
		return panel;
	}

	public boolean connect() {
		if (ipAddress.get() == null || ipAddress.get().isEmpty()) {
			status.set("invalid ip detected: " + ipAddress.get());
			return false;
		}

		if (connected.get()) {
			disconnect();
		}

		int port;
		try {
			// GUI socket
			port = 49151 + 4000 + Integer.parseInt(ipAddress.get().split("\\.")[3]);
		} catch (Exception e) {
			status.set("invalid ip detected: " + ipAddress.get());
			return false;
		}

		final SocketAddress adr = new InetSocketAddress(ipAddress.get(), port);

		try {
			logger.debug("start writer..");
			writerThread = new VdmaChannelWriterThread("MscWriterThread");
			writerThread.open(adr);

			logger.debug("start reader..");
			readerThread = new VdmaXmlChannelReaderThread(this);
			readerThread.open(port);
		} catch (IOException e) {
			status.set(e.getMessage());
			return false;
		}

		connected.set(true);
		status.set("connected");
		return true;
	}

	public boolean disconnect() {

		if (writerThread != null) {
			try {
				writerThread.close();
				writerThread.interrupt();
				writerThread.join(1000);
			} catch (Exception e1) {
				logger.error(e1.getMessage(), e1);
			}
			writerThread = null;
		}

		if (readerThread != null) {
			readerThread.close();
			readerThread.interrupt();
			try {
				readerThread.join(1000);
			} catch (InterruptedException e) {
			}
			readerThread = null;
		}

		connected.set(false);
		status.set("disconnected");
		return true;
	}

	@Override
	public void processXML(byte[] xml) {
		logger.info("xml received: {}", xml.length);

	}

	@Override
	public void processXML(byte[] xml, byte[] image) {
		logger.info("image received: {}", image.length);

		int[] imgIntArray = new int[image.length];
		for (int i = 0; i < imgIntArray.length; i++) {
			imgIntArray[i] = image[i];
		}

		int height = AspectRatioTool.getHeight(image.length, AR.FOUR_THREE);
		int width = image.length / height;

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster myRaster = bufferedImage.getRaster();
		myRaster.setPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), imgIntArray);
		MSCGUI gui = null;

		try {
			gui = loader.get(new ByteArrayInputStream(xml));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		drawComponents(g, gui);
		g.dispose();
		ovlImage.set(bufferedImage);

	}

	private void drawComponents(Graphics2D g, MSCGUI gui) {

		SndPICtoGUI pic = gui.getSndPICtoGUI();
		// g.setWidth(pic.getDx());
		// g.setHeight(pic.getDy());
		g.setComposite(AlphaComposite.Clear);
		
		for (DrawRectArea e : gui.getDrawRectArea()) {
			logger.debug("draw rect: {},{},{},{}", e.getPx(), e.getPy(), e.getDx(), e.getDy());
			g.clearRect(e.getPx(), e.getPy(), e.getDx(), e.getDy());
		}
		g.setPaintMode();
		g.setColor(Color.YELLOW);
		
		for (DrawFrame e : gui.getDrawFrame()) {
			logger.debug("draw frame: {},{},{},{}", e.getPx(), e.getPy(), e.getDx(), e.getDy());
			// g.setLineWidth(e.getThickn());
			// g.setStroke(Stroke(e.getCol()));
			g.drawRect(e.getPx(), e.getPy(), e.getDx(), e.getDy());
		}
		for (PriStrBsp e : gui.getPriStrBsp()) {
			logger.debug("draw text: {},{}", e.getPx(), e.getPy());

			// g.setTextBaseline(VPos.TOP);
			// g.setFont(getFont(e.getSize()));
			//
			// FontMetrics fm =
			// Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont());
			// g.setFill(Color.web(e.getBgcol()));
			// g.fillRect(e.getPx(), e.getPy(),
			// fm.computeStringWidth(e.getString()), fm.getLineHeight());
			//
			// g.setFill(Color.web(e.getFgcol()));
			//
			// g.fillText(e.getString(), e.getPx(), e.getPy());
		}
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new SwingTest();

			}
		});
	}

}
