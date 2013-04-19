package org.comtel.mt.msc.gui.view;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import org.comtel.mt.msc.gui.Main;
import org.comtel.mt.msc.gui.communication.VdmaChannelReaderThread;
import org.comtel.mt.msc.gui.communication.VdmaChannelWriterThread;
import org.comtel.mt.msc.gui.communication.VdmaMessageProcessor;
import org.slf4j.LoggerFactory;

public class MscImageController implements Initializable, AutoCloseable, VdmaMessageProcessor {

	@FXML
	private TextField ipTextField;

	@FXML
	private Button connectButton;
	
	@FXML
	private Button disconnectButton;
	
	
	@FXML
	private Label statusLabel;

	@FXML
	private StackPane viewGroup;
	
	private ImageView imageView = new ImageView();
	
	private ImageViewPane imageViewPane = new ImageViewPane();
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

	private final SimpleStringProperty ipAddress = new SimpleStringProperty();

	private final SimpleStringProperty status = new SimpleStringProperty();

	private final SimpleObjectProperty<Image> ovlImage = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);

	private VdmaChannelReaderThread readerThread;
	private VdmaChannelWriterThread writerThread;

	@FXML
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

	@FXML
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
		ovlImage.set(null);
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

		final WritableImage fxImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
		// PixelWriter writer = fxImage.getPixelWriter();
		// writer.setPixels(0, 0, 640 / sample, 480 / sample,
		// PixelFormat.getByteBgraInstance(), image, 0, 0);

		SwingFXUtils.toFXImage(bufferedImage, fxImage);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				status.set("image size: " + fxImage.getWidth() + "/" + fxImage.getHeight());
				ovlImage.set(fxImage);
			}
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ipAddress.bind(ipTextField.textProperty());
		statusLabel.textProperty().bind(status);

		connectButton.disableProperty().bind(connected);
		disconnectButton.disableProperty().bind(connected.not());
		ipTextField.editableProperty().bind(connected.not());
		
		imageView.setCache(true);
		imageView.setSmooth(true);
		imageView.setPreserveRatio(true);

		imageView.imageProperty().bind(ovlImage);
		imageViewPane.setImageView(imageView);
		imageViewPane.setPrefSize(640, 480);
		
		viewGroup.getChildren().add(imageViewPane);
	}

	@Override
	public void close() throws Exception {
		disconnect();
	}

}
