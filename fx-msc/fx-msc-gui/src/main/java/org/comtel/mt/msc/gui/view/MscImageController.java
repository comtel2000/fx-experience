package org.comtel.mt.msc.gui.view;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Lcd;
import jfxtras.labs.scene.control.gauge.LcdBuilder;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.StyleModel;
import jfxtras.labs.scene.control.gauge.StyleModelBuilder;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;

import org.comtel.mt.msc.gui.AspectRatioTool;
import org.comtel.mt.msc.gui.AspectRatioTool.AR;
import org.comtel.mt.msc.gui.DefaultXmlUtils;
import org.comtel.mt.msc.gui.Main;
import org.comtel.mt.msc.gui.communication.VdmaChannelReaderThread;
import org.comtel.mt.msc.gui.communication.VdmaChannelWriterThread;
import org.comtel.mt.msc.gui.communication.VdmaMessageProcessor;
import org.comtel.mt.msc.gui.control.ImageViewPane;
import org.comtel.mt.msc.gui.model.MscGuiContext;
import org.comtel.mt.msc.gui.model.MscImage;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.DrawFrame;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.DrawRectArea;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.PriStrBsp;
import org.comtel.mt.msc.gui.model.jaxb.MSCGUI.SndPICtoGUI;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class MscImageController implements Initializable, AutoCloseable, VdmaMessageProcessor {

	@FXML
	private ToggleButton dataViewButton;

	@FXML
	private TextField ipTextField;

	@FXML
	private AnchorPane menuPane;

	@FXML
	private Button connectButton;

	@FXML
	private Button disconnectButton;

	@FXML
	private Label statusLabel;

	@FXML
	private Group viewGroup;

	// private MscImageViewPane mscImageViewPane = new MscImageViewPane();

	private ImageViewPane imageViewPane = new ImageViewPane();

	private Window window = new Window("MSC FX");

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

	private final SimpleStringProperty ipAddress = new SimpleStringProperty();

	private final SimpleStringProperty status = new SimpleStringProperty();

	private final SimpleIntegerProperty imageCount = new SimpleIntegerProperty();

	private final SimpleObjectProperty<MscImage> mscImageProperty = new SimpleObjectProperty<>();

	private final SimpleObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty dataVisibleProperty = new SimpleBooleanProperty();

	private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);

	private final ObservableList<MscImage> imageList = FXCollections.observableArrayList();

	private final SimpleIntegerProperty imageListIndex = new SimpleIntegerProperty(0);

	private final SimpleIntegerProperty imageIndex = new SimpleIntegerProperty(0);

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
		if (window.isMinimized()) {
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
		mscImageProperty.set(null);
		return true;
	}

	@FXML
	public void showDataView(ActionEvent action) {
		dataVisibleProperty.set(true);
	}

	@FXML
	public void hideDataView() {
		dataVisibleProperty.set(false);
	}

	@FXML
	public void showPrevious() {
		if (imageListIndex.get() > 0) {
			imageListIndex.set(imageListIndex.get() - 1);
			drawImage(imageList.get(imageListIndex.get()));
		}
	}

	@FXML
	public void showNext() {
		if (imageListIndex.get() < imageList.size() - 1) {
			imageListIndex.set(imageListIndex.get() + 1);
			drawImage(imageList.get(imageListIndex.get()));
		}
	}

	@Override
	public void processXML(byte[] xml) {
		logger.info("xml without image received: {}", xml.length);
		try {
			Document doc = DefaultXmlUtils.bytes2doc(xml);
			System.out.println(DefaultXmlUtils.docToString(doc));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
//		final MscImage mscImg = new MscImage(null, xml);
//		if (dataVisibleProperty.get()) {
//			try {
//				mscImg.setMSCGUI(loader.get(new ByteArrayInputStream(xml)));
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//
//		if (mscImg.getMSCGUI().getMNURelease() != null){
//			logger.info("skip on MNURelease");
//			return;
//		}
//		Platform.runLater(new Runnable() {
//			@Override
//			public void run() {
//				Image i = createDataLayerImage(mscImg, 800, 600);
//				imageViewProperty.get().setImage(i);
//			}
//		});

	}

	@Override
	public void processXML(byte[] xml, byte[] image) {

		logger.info("data received: {}/{}", xml != null ? xml.length : "-", image != null ? image.length : "-");

		int[] imgIntArray = new int[image.length];
		for (int i = 0; i < imgIntArray.length; i++) {
			imgIntArray[i] = image[i];
		}

		int height = AspectRatioTool.getHeight(image.length, AR.FOUR_THREE);
		int width = image.length / height;

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster myRaster = bufferedImage.getRaster();
		myRaster.setPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), imgIntArray);

		WritableImage fxImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());

		SwingFXUtils.toFXImage(bufferedImage, fxImage);
		final MscImage mscImg = new MscImage(fxImage, xml);
		if (dataVisibleProperty.get()) {
			try {
				mscImg.setMSCGUI(loader.get(new ByteArrayInputStream(xml)));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		if (imageList.size() > 10) {
			imageList.remove(0);
		}
		imageList.add(mscImg);
		imageListIndex.set(imageList.size() - 1);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				imageCount.set(imageCount.get() + 1);
				drawImage(mscImg);
			}
		});
	}

	private Font getFont(int style) {
		logger.debug("get font style: {}", style);

		if (style == 1) {
			return Font.font("Dialog", 12);
		}
		return Font.font("Dialog", 24);
	}

	private void drawComponents(Canvas canvas, MSCGUI gui) {

		GraphicsContext context = canvas.getGraphicsContext2D();

		for (DrawRectArea e : gui.getDrawRectArea()) {
			logger.debug("draw rect: {},{},{},{},{}", e.getPx(), e.getPy(), e.getDx(), e.getDy(), e.getCol());
			if ("CLEAR".equals(e.getCol())) {
				context.rect(e.getPx(), e.getPy(), e.getDx(), e.getDy());
			} else {
				context.setFill(Color.web(e.getCol()));
				context.fillRect(e.getPx(), e.getPy(), e.getDx(), e.getDy());
			}
		}

		for (DrawFrame e : gui.getDrawFrame()) {
			logger.debug("draw frame: {},{},{},{}", e.getPx(), e.getPy(), e.getDx(), e.getDy());
			context.setLineWidth(e.getThickn());
			context.setStroke(Color.web(e.getCol()));
			context.strokeRect(e.getPx(), e.getPy(), e.getDx(), e.getDy());
		}
		for (PriStrBsp e : gui.getPriStrBsp()) {
			logger.debug("draw text: {},{}", e.getPx(), e.getPy());

			context.setTextBaseline(VPos.TOP);
			context.setFont(getFont(e.getSize()));

			FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(context.getFont());
			context.setFill(Color.web(e.getBgcol()));
			context.fillRect(e.getPx(), e.getPy(), fm.computeStringWidth(e.getString()), fm.getLineHeight());

			context.setFill(Color.web(e.getFgcol()));

			context.fillText(e.getString(), e.getPx(), e.getPy());
		}
	}

	private void drawImage(MscImage img) {

		mscImageProperty.set(img);
	}

	private final MscGuiContext loader = new MscGuiContext();

	private Image createDataLayerImage(MscImage mscImage) {
		return createDataLayerImage(mscImage, 0, 0);
	}

	private Image createDataLayerImage(MscImage mscImage, double width, double height) {

		if (mscImage.getMSCGUI() == null) {
			try {
				mscImage.setMSCGUI(loader.get(new ByteArrayInputStream(mscImage.getXML())));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		MSCGUI gui = mscImage.getMSCGUI();

		Canvas canvas = new Canvas(width, height);
		SndPICtoGUI pic = gui.getSndPICtoGUI();
		if (pic != null) {
			canvas.setWidth(pic.getDx());
			canvas.setHeight(pic.getDy());
		}
		if (mscImage.getImage() != null){
			canvas.getGraphicsContext2D().drawImage(mscImage.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
		}
		drawComponents(canvas, gui);

		WritableImage wimg = null;
		wimg = canvas.snapshot(null, wimg);
		return wimg;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ipAddress.bind(ipTextField.textProperty());
		statusLabel.textProperty().bind(status);

		connectButton.disableProperty().bind(connected);
		disconnectButton.disableProperty().bind(connected.not());
		ipTextField.disableProperty().bind(connected);

		ImageView imageView = new ImageView();
		imageView.getStyleClass().add("msc-imageview");
		imageView.setCache(true);
		imageView.setSmooth(true);
		imageView.setPreserveRatio(true);

		imageViewProperty.set(imageView);

		StyleModel STYLE_MODEL_1 = StyleModelBuilder.create().lcdDesign(LcdDesign.GRAY).lcdValueFont(Gauge.LcdFont.LCD)
				.lcdUnitStringVisible(false).lcdThresholdVisible(false).build();

		Lcd lcd1 = LcdBuilder.create().styleModel(STYLE_MODEL_1).threshold(10).bargraphVisible(true)
				.minMeasuredValueVisible(true).minMeasuredValueDecimals(0).maxMeasuredValueVisible(true)
				.maxMeasuredValueDecimals(0).formerValueVisible(false).title("connected").unit("").build();
		lcd1.valueProperty().bind(imageIndex);
		lcd1.maxMeasuredValueProperty().bind(imageCount);
		lcd1.minMeasuredValueProperty().bind(imageCount.subtract(10));
		lcd1.setPrefSize(150, 50);
		lcd1.titleVisibleProperty().bind(connected);

		menuPane.getChildren().add(lcd1);

		window.titleProperty().bind(ipAddress);
		window.setPrefSize(640, 505);

		window.minimizedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					disconnect();
				}
			}
		});

		// window.getLeftIcons().add(new CloseIcon(window));
		window.getRightIcons().add(new MinimizeIcon(window));
		imageViewPane.imageViewProperty().bind(imageViewProperty);

		BorderPane backG = new BorderPane();
		Text t = new Text("MSC FX");
		t.setId("window-text");
		backG.setCenter(t);
		window.getContentPane().getChildren().add(backG);
		window.getContentPane().getChildren().add(imageViewPane);
		// window.getContentPane().getChildren().add(lcd1);

		mscImageProperty.addListener(new ChangeListener<MscImage>() {

			@Override
			public void changed(ObservableValue<? extends MscImage> arg0, MscImage oldI, MscImage newI) {
				if (newI == null) {
					return;
				}

				if (dataVisibleProperty.get()) {
					imageViewProperty.get().setImage(createDataLayerImage(newI));
				} else if (newI.getImage() != null){
					imageViewProperty.get().setImage(newI.getImage());
				}
				if (newI.getImage() != null) {
					imageIndex.set((1 + imageCount.get() - (imageList.size() - imageListIndex.get())));

					status.set("image size: " + newI.getImage().getWidth() + "/" + newI.getImage().getHeight() + " ("
							+ imageIndex.get() + "/" + imageCount.get() + ")");
				}
			}
		});

		dataVisibleProperty.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (mscImageProperty.get() == null) {
					return;
				}
				if (arg2) {
					imageViewProperty.get().setImage(createDataLayerImage(mscImageProperty.get()));
				} else {
					imageViewProperty.get().setImage(mscImageProperty.get().getImage());
				}
			}
		});

		viewGroup.getChildren().add(window);

		dataViewButton.setSelected(true);
		dataVisibleProperty.bind(dataViewButton.selectedProperty());
	}

	@Override
	public void close() throws Exception {
		disconnect();
	}

}
