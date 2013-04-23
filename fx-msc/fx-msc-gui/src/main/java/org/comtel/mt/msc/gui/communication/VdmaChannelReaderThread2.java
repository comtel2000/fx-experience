package org.comtel.mt.msc.gui.communication;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.LoggerFactory;

public class VdmaChannelReaderThread2 extends Thread {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(VdmaChannelReaderThread2.class);

	private final static int BUFFER_SIZE = 8 * 1024;
	private final static int LF = 10;
	private SocketAddress socketAdr;
	private final AtomicBoolean running = new AtomicBoolean(false);

	private final VdmaMessageProcessor processor;

	public final boolean isRunning() {
		return running.get();
	}

	public VdmaChannelReaderThread2(VdmaMessageProcessor processor) {
		setName("MSCChannelReaderThread");
		this.processor = processor;
	}

	public void open(int port) throws IOException {
		socketAdr = new InetSocketAddress(port);
		logger.info("try to open input channel on: {}", socketAdr);
		start();
	}

	public void close() {
		running.set(false);

	}

	@Override
	public void run() {
		running.set(true);
		try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
			serverSocket.configureBlocking(true);
			serverSocket.bind(socketAdr);

			ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
			boolean receiveImage = false;
			boolean receiveTag = false;
			boolean sampleData = false;
			int maxImageWidth = 640;
			int maxImageHeight = 480;
			int imageSize = 76800;

			long time = 0;

			ByteArrayOutputStream imageStream = new ByteArrayOutputStream(76800);

			ByteArrayOutputStream xmlStream = new ByteArrayOutputStream(1024);
			CharArrayWriter tagStream = new CharArrayWriter(1024);

			try (SocketChannel socket = serverSocket.accept()) {
				// socket.configureBlocking(true);
				socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				while (running.get() && socket.read(buffer) > 0) {

					buffer.flip();

					while (buffer.hasRemaining()) {
						if (xmlStream.size() == 0) {
							time = System.currentTimeMillis();
						}
						int b = buffer.get();
						if (sampleData) {
							int sample = Character.getNumericValue(b);
							imageSize = (maxImageWidth / sample) * (maxImageHeight / sample);
							logger.debug("sample: {} -> imageSize: {}", sample, imageSize);
							sampleData = false;
							continue;
						}
						if (receiveImage && imageStream.size() < imageSize) {
							imageStream.write(b);
							continue;
						}
						xmlStream.write(b);

						if (!receiveTag && b == '<') {
							receiveTag = true;
							tagStream.write(b);
							continue;
						}
						if (receiveTag){
							if (b == '<'){
								tagStream.reset();
							}
							tagStream.write(b);
						}

						if (receiveTag && b == '>') {
							String tag = tagStream.toString();
							tagStream.reset();
							receiveTag = false;
							if (tag.equals("<sample>")) {
								sampleData = true;
								continue;
							}
							if (!receiveImage && tag.equals("<data>")) {
								receiveImage = true;
							} else if (receiveImage && tag.equals("</data>")) {
								receiveImage = false;
							} else if (!receiveImage && tag.equals("</MSC_GUI>")) {
								time = System.currentTimeMillis() - time;
								logger.debug("XML size: {} received in {} ms", xmlStream.size(), time);
								if (imageStream.size() > 0) {
									processor.processXML(xmlStream.toByteArray(), imageStream.toByteArray());
									imageStream.reset();
								} else {
									processor.processXML(xmlStream.toByteArray());
								}
								logger.debug("xml final size: {}", xmlStream.size());

								xmlStream.reset();
							}
						}
					}
					// logger.warn("partial xml size: {}", xmlStream.size());
					buffer.clear();
				}
			}
			logger.warn("in socket closed");

		} catch (ClosedByInterruptException e) {
			logger.warn("server socket closed");
		} catch (IOException e) {
			logger.error("server socket closed", e);
		}
		close();
	}

}