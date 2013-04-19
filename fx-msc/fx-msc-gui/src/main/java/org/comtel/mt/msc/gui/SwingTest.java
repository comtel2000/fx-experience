package org.comtel.mt.msc.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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

import org.comtel.mt.msc.gui.communication.VdmaChannelReaderThread;
import org.comtel.mt.msc.gui.communication.VdmaChannelWriterThread;
import org.comtel.mt.msc.gui.communication.VdmaMessageProcessor;
import org.slf4j.LoggerFactory;

public class SwingTest extends JFrame implements VdmaMessageProcessor {

	private static final long serialVersionUID = 1L;

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SwingTest.class);

	private final SimpleStringProperty ipAddress = new SimpleStringProperty("192.168.100.217");

	private final SimpleStringProperty status = new SimpleStringProperty();

	private final SimpleObjectProperty<BufferedImage> ovlImage = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);

	private VdmaChannelReaderThread readerThread;
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
			readerThread = new VdmaChannelReaderThread(this);
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
	public void processImage(byte[] image) {
		logger.info("image received: {}", image.length);

		int[] imgIntArray = new int[image.length];
		for (int i = 0; i < imgIntArray.length; i++) {
			imgIntArray[i] = image[i];
		}

		int sample = 640 * 480 / image.length;
		BufferedImage bufferedImage = new BufferedImage(640 / sample, 480 / sample, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster myRaster = bufferedImage.getRaster();
		myRaster.setPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), imgIntArray);

		ovlImage.set(bufferedImage);

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
