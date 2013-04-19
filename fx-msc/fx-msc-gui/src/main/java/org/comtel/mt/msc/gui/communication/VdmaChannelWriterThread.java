package org.comtel.mt.msc.gui.communication;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.LoggerFactory;

public class VdmaChannelWriterThread extends Thread {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(VdmaChannelWriterThread.class);

	private final AtomicBoolean running = new AtomicBoolean(false);

	public final boolean isRunning() {
		return running.get();
	}

	private final BlockingQueue<ByteBuffer> sendQueue;

	private SocketAddress adr;

	public VdmaChannelWriterThread(String name) {
		setName(name);
		sendQueue = new LinkedBlockingQueue<>(8);

	}

	public void open(SocketAddress adr) throws IOException {
		this.adr = adr;
		start();
	}

	public void write(byte[] b) throws IOException {
		write(ByteBuffer.wrap(b));
	}

	public void write(ByteBuffer b) throws IOException {
		if (!sendQueue.offer(b)) {
			sendQueue.clear();
			throw new IOException("send buffer overflow");
		}
	}

	public void writeACK() throws IOException {
		if (!sendQueue.offer(ByteBuffer.wrap(new byte[] { 6 }))) {
			sendQueue.clear();
			throw new IOException("send buffer overflow");
		}
	}

	public void close() {
		running.set(false);
		sendQueue.clear();
	}

	@Override
	public void run() {
		try (SocketChannel socketChannel = SocketChannel.open()) {
			if (socketChannel.isOpen()) {
				socketChannel.configureBlocking(true);

				// socketChannel.setOption(StandardSocketOptions.SO_SNDBUF,
				// 128 * 1024);
				socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
				socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				// socketChannel.setOption(StandardSocketOptions.SO_LINGER,
				// 5);
				logger.info("try to open output channel on: {}", adr);
				running.set(socketChannel.connect(adr));

				while (socketChannel.isConnected() && running.get()) {
					socketChannel.write(sendQueue.take());
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.debug("socket closed by interrupt");
		}
	}
}