package org.comtel.mt.msc.gui.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.LoggerFactory;

public class VdmaXmlChannelReaderThread extends Thread implements AutoCloseable{
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(VdmaXmlChannelReaderThread.class);

	private final static int BUFFER_SIZE = 8 * 1024;

	private final String START_DATA = "<data>";
	private final String END_DATA = "</data>";

	private final String START_SAMPLE = "<sample>";

	private final String END_MSC_GUI = "</MSC_GUI>";

	private SocketAddress socketAdr;
	
	private final AtomicBoolean running = new AtomicBoolean(false);

	private final VdmaMessageProcessor processor;

	public final boolean isRunning() {
		return running.get();
	}

	public VdmaXmlChannelReaderThread(VdmaMessageProcessor processor) {
		setName("MSCReaderThread");
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

			ByteBuffer imgBuffer = ByteBuffer.allocateDirect(maxImageWidth * maxImageHeight);
			// ByteArrayOutputStream imageStream = new
			// ByteArrayOutputStream(76800);
			ByteArrayOutputStream xmlStream = new ByteArrayOutputStream(4096);
			ByteArrayOutputStream tagStream = new ByteArrayOutputStream(1024);

			try (SocketChannel socket = serverSocket.accept()) {
				socket.configureBlocking(true);
				socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				socket.setOption(StandardSocketOptions.TCP_NODELAY, true);
				socket.setOption(StandardSocketOptions.SO_RCVBUF, BUFFER_SIZE);

				while (running.get() && socket.read(buffer) > 0) {

					buffer.flip();

					while (buffer.hasRemaining()) {
						if (xmlStream.size() == 0) {
							time = System.currentTimeMillis();
						}
						int b = buffer.get();
						// System.err.print((char)b);
						if (sampleData) {
							int sample = Character.getNumericValue(b);
							imageSize = (maxImageWidth / sample) * (maxImageHeight / sample);
							logger.debug("sample: {} -> imageSize: {}", sample, imageSize);
							sampleData = false;
							imgBuffer.clear();
							imgBuffer.limit(imageSize);
							continue;
						}
						if (receiveImage && imgBuffer.hasRemaining()) {
							imgBuffer.put((byte) b);
							continue;
						}
						xmlStream.write(b);

						if (!receiveTag && b == '<') {
							receiveTag = true;
							tagStream.write(b);
							continue;
						}
						if (receiveTag) {
							tagStream.write(b);
						}

						if (receiveTag && b == '>') {
							String tag = tagStream.toString();
							tagStream.reset();
							receiveTag = false;

							switch (tag) {
							case START_SAMPLE:
								sampleData = true;
								break;
							case START_DATA:
								receiveImage = true;
								break;
							case END_DATA:
								receiveImage = false;
								break;
							case END_MSC_GUI:
								time = System.currentTimeMillis() - time;
								logger.debug("XML size: {} received in {} ms", xmlStream.size(), time);

								if (imgBuffer.position() > 0) {
									imgBuffer.flip();
									byte[] image = new byte[imgBuffer.limit()];
									imgBuffer.get(image);
									processor.processXML(xmlStream.toByteArray(), image);
								} else {
									processor.processXML(xmlStream.toByteArray());
								}
								logger.debug("xml final size: {}", xmlStream.size());
								xmlStream.reset();
								break;
							default:
								break;
							}

						}
					}
					logger.trace("partial xml size: {}", xmlStream.size());
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