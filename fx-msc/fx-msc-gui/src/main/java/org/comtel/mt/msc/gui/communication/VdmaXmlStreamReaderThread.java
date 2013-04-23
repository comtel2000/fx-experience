package org.comtel.mt.msc.gui.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.LoggerFactory;

public class VdmaXmlStreamReaderThread extends Thread implements XMLStreamConstants{
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(VdmaXmlStreamReaderThread.class);

	private final static int BUFFER_SIZE = 8 * 1024;
	private final static int LF = 10;
	private SocketAddress socketAdr;
	private final AtomicBoolean running = new AtomicBoolean(false);

	private final VdmaMessageProcessor processor;

	private StreamFilter filter;

	public final boolean isRunning() {
		return running.get();
	}

	public VdmaXmlStreamReaderThread(VdmaMessageProcessor processor) {
		setName("MSCChannelReaderThread");
		this.processor = processor;
		filter = new StreamFilter() {
			
			@Override
			public boolean accept(XMLStreamReader reader) {
				if (reader.getEventType() == START_ELEMENT && reader.getName().equals("data")){
					System.err.println("ignore data!!");
					return false;
				}
				return true;
			}
		};
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
			
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream(76800);
			ByteArrayOutputStream xmlStream = new ByteArrayOutputStream(1024);
			ByteArrayOutputStream tagStream = new ByteArrayOutputStream(1024);

			try (SocketChannel socket = serverSocket.accept()) {
				socket.configureBlocking(true);
				socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				
				  
				XMLInputFactory inputFactory = XMLInputFactory2.newInstance();
				XMLStreamReader streamReader = inputFactory.createXMLStreamReader(new InputStreamReader(socket.socket().getInputStream(), "ISO-8859-1"));
				XMLStreamReader reader = inputFactory.createFilteredReader(streamReader, filter);

				while (socket.isConnected()){
				while (reader.hasNext()) {
					switch (reader.next()) {
					case START_DOCUMENT:
						System.err.println("start doc");
						break;
					case START_ELEMENT:
						System.err.println("\t" + reader.getName());
						if (reader.getName().equals("data")){
							
						}
						break;
					case END_DOCUMENT:
						System.err.println("end doc");
						break;
					default:
						System.err.print("," + reader.getEventType());
						break;
					}
					
				}
				}
			} catch (XMLStreamException e) {
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