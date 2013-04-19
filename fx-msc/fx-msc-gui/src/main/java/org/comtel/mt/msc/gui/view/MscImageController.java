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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Lcd;
import jfxtras.labs.scene.control.gauge.LcdBuilder;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.StyleModel;
import jfxtras.labs.scene.control.gauge.StyleModelBuilder;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;

import org.comtel.mt.msc.gui.Main;
import org.comtel.mt.msc.gui.communication.VdmaChannelReaderThread;
import org.comtel.mt.msc.gui.communication.VdmaChannelReaderThread2;
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
	private Group viewGroup;

	private StyleModel STYLE_MODEL_1 = StyleModelBuilder.create().lcdDesign(LcdDesign.STANDARD_GREEN)
			.lcdValueFont(Gauge.LcdFont.LCD).lcdUnitStringVisible(true).lcdThresholdVisible(true).build();
	
	private ImageView imageView = new ImageView();
	
	private ImageViewPane imageViewPane = new ImageViewPane();
	private Window window = new Window("MSC");
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

	private final SimpleStringProperty ipAddress = new SimpleStringProperty();

	private final SimpleStringProperty status = new SimpleStringProperty();

	private final SimpleIntegerProperty imageCount = new SimpleIntegerProperty();
	
	private final SimpleObjectProperty<Image> ovlImage = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);

	private final ObservableList<Image> imageList = FXCollections.observableArrayList();
	
	private final SimpleIntegerProperty imageListIndex = new SimpleIntegerProperty();
	
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
		if (window.isMinimized()){
			window.setMinimized(false);
		}
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

	@FXML
	public void showPrevious() {
		if (imageListIndex.get() > 0){
			imageListIndex.set(imageListIndex.get() - 1);
			ovlImage.set(imageList.get(imageListIndex.get()));
		}
	}
	
	@FXML
	public void showNext() {
		if (imageListIndex.get() < imageList.size() - 1){
			imageListIndex.set(imageListIndex.get() + 1);
			ovlImage.set(imageList.get(imageListIndex.get()));
		}
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

		int sample = 1;
		if (image.length == 76800) {
			sample = 2;
		} else if (image.length == 19200) {
			sample = 3;
		}

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
				if (imageList.size() > 10){
					imageList.remove(0);
				}
				imageList.add(fxImage);
				imageListIndex.set(imageList.size() - 1);
				
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
		ovlImage.addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
				if(newValue != null){
					imageCount.set(imageCount.get() + 1);
				}else{
					imageCount.set(0);
				}
				
			}
		});
		Lcd lcd1 = LcdBuilder.create().styleModel(STYLE_MODEL_1).threshold(40).bargraphVisible(true)
				.minMeasuredValueVisible(true).minMeasuredValueDecimals(3).maxMeasuredValueVisible(true)
				.maxMeasuredValueDecimals(3).formerValueVisible(true).title("images").unit("").build();
		lcd1.valueProperty().bind(imageCount);
		
		lcd1.setPrefSize(150, 70);
		window.titleProperty().bind(ipAddress);
//		w.setLayoutX(10);
//		w.setLayoutY(10);
		window.setPrefSize(640, 480);
		window.minimizedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue){
					disconnect();
				}
			}
		});
		
		//window.getLeftIcons().add(new CloseIcon(window));
		window.getRightIcons().add(new MinimizeIcon(window));
		window.getContentPane().getChildren().add(imageViewPane);
//		imageView.fitWidthProperty().bind(w.widthProperty());
//		imageView.fitHeightProperty().bind(w.heightProperty());
		
		imageViewPane.setImageView(imageView);
		imageViewPane.setPrefSize(640, 480);

		
		viewGroup.getChildren().add(window);
		//viewGroup.getChildren().add(lcd1);
	}

	@Override
	public void close() throws Exception {
		disconnect();
	}

}
