package org.comtel.mt.msc.gui.communication;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringWriter;
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
	private final int MAX_XML_CMD_LENGTH = 25;
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
		char chr = 0;
		CharArrayWriter strBuffer = new CharArrayWriter();
		ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream(76800);
		CharArrayWriter xmlTagBuffer = new CharArrayWriter(MAX_XML_CMD_LENGTH + 1);
		String xmlTagString, imageData;
		boolean dataStream = false;
		boolean mscVideoMain = false;
		boolean mscStreamEnd = false;
		
		try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
			serverSocket.configureBlocking(true);
			serverSocket.bind(socketAdr);

			ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

			try (SocketChannel socket = serverSocket.accept()) {
				socket.configureBlocking(true);
				socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				while (running.get() && socket.read(buffer) > 0) {

					buffer.flip();

					while (buffer.hasRemaining()) {
						chr = (char)buffer.get();
						if (chr == (byte) 6 && !dataStream && !mscVideoMain) {
							continue;
						} else if (chr == '<') {
							xmlTagBuffer.reset();
							xmlTagBuffer.append(chr);
						} else if (chr == '>') {
							xmlTagString = xmlTagBuffer.append(chr).toString();
							xmlTagBuffer.reset();
							if (!dataStream && mscVideoMain && xmlTagString.equals("<data>")) {
								dataStream = true;
								strBuffer.append(chr);
								continue;
							} else if (!mscVideoMain && xmlTagString.equals("</page>")) {
								if (strBuffer.toString().endsWith("BSP_VIDEO_MAIN</page"))
									mscVideoMain = true;
							} else if (mscVideoMain && xmlTagString.equals("</data>")) {
								strBuffer.append("Placeholder for Image</data>");
								dataBuffer.write(chr);
								dataStream = false;
								mscVideoMain = false;
								continue;
							} else if (xmlTagString.equals("</MSC_GUI>")) {
								mscStreamEnd = true;
							}
							// System.err.println(xmlTagBuffer.toString());

							// add to xml command
						} else if (xmlTagBuffer.size() > 0 && xmlTagBuffer.size() < MAX_XML_CMD_LENGTH) {

							if (dataStream && Character.isWhitespace(chr)){
								xmlTagBuffer.reset();
							}
							else{
								xmlTagBuffer.append(chr);
							}
						}

						// switch between "BSP_VIDEO_MAIN" image and xml
						// data
						if (dataStream) {
							dataBuffer.write(chr);
						} else{
							strBuffer.append(chr);
						}
						if (mscStreamEnd) { // finished transfer
							mscStreamEnd = false;
							mscVideoMain = false;
							//
							if (dataBuffer.size() > 0) { // contains image
								
								
								byte[] data = dataBuffer.toByteArray();
								byte[] image = Arrays.copyOf(data, data.length - 7);

								System.out.println("image size: " + image.length);
								processor.processImage(image);

								dataBuffer.reset();

							} else { // contains only xml data
								processor.processXML(strBuffer.toString().getBytes());
							}
							strBuffer.reset();
							xmlTagBuffer.reset();
						}
					}
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