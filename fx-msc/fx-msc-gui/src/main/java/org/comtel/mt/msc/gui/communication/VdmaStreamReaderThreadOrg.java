package org.comtel.mt.msc.gui.communication;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.stream.XMLStreamConstants;

import org.slf4j.LoggerFactory;

public class VdmaStreamReaderThreadOrg extends Thread implements XMLStreamConstants {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(VdmaStreamReaderThreadOrg.class);

	private final static int BUFFER_SIZE = 8 * 1024;
	private final static int LF = 10;
	private SocketAddress socketAdr;
	private final AtomicBoolean running = new AtomicBoolean(false);

	private final VdmaMessageProcessor processor;

	private final int MAX_XML_CMD_LENGTH = 25;

	public final boolean isRunning() {
		return running.get();
	}

	public VdmaStreamReaderThreadOrg(VdmaMessageProcessor processor) {
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
		int chr = 0;
		CharArrayWriter strBuffer = new CharArrayWriter();
		ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream(76800);
		CharArrayWriter xmlTagBuffer = new CharArrayWriter(MAX_XML_CMD_LENGTH + 1);
		String xmlTagString, imageData;
		boolean dataStream = false;
		boolean mscVideoMain = false;
		boolean mscStreamEnd = false;
		long time = 0;
		try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
			serverSocket.configureBlocking(true);
			serverSocket.bind(socketAdr);
			try (SocketChannel socket = serverSocket.accept()) {
				socket.configureBlocking(true);
				socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				InputStreamReader in = new InputStreamReader(new BufferedInputStream(socket.socket().getInputStream()),
						"ISO-8859-1");

				while (socket.isConnected()) {
					if ((chr=in.read()) > -1) {
						//System.err.println(chr);
						if (strBuffer.size() == 0) {
							time = System.currentTimeMillis();
						}

						if (chr == (byte) 6 && !dataStream && !mscVideoMain) {
							continue;
						} else if (chr == '<') {
							xmlTagBuffer.reset();
							xmlTagBuffer.write(chr);
						} else if (chr == '>') {
							xmlTagBuffer.write(chr);
							xmlTagString = xmlTagBuffer.toString();
							xmlTagBuffer.reset();
							if (!dataStream && mscVideoMain && xmlTagString.equals("<data>")) {
								dataStream = true;
								strBuffer.write(chr);
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

							if (dataStream && Character.isWhitespace(chr)) {
								xmlTagBuffer.reset();
							} else {
								xmlTagBuffer.write(chr);
							}
						}

						// switch between "BSP_VIDEO_MAIN" image and xml
						// data
						if (dataStream) {
							dataBuffer.write(chr);
						} else {
							strBuffer.write(chr);
						}
						if (mscStreamEnd) { // finished transfer
							time = System.currentTimeMillis() - time;
							logger.debug("XML size: {} received in {} ms", dataBuffer.size(), time);
							mscStreamEnd = false;
							mscVideoMain = false;
							//
							if (dataBuffer.size() > 0) { // contains image

								byte[] data = dataBuffer.toByteArray();
								byte[] image = Arrays.copyOf(data, data.length - 7);

								System.out.println("image size: " + image.length);
								processor.processXML(data, image);

								dataBuffer.reset();

							} else { // contains only xml data
								processor.processXML(strBuffer.toString().getBytes());
							}
							strBuffer.reset();
							xmlTagBuffer.reset();
						}
					} else {
						Thread.sleep(5);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
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