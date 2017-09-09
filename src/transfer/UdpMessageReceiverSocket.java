package transfer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import data.Message;

public class UdpMessageReceiverSocket<T extends Message> implements UdpReceiverSocket<T> {
	private final UdpReceiverSocket<byte[]> socket;

	public UdpMessageReceiverSocket(UdpReceiverSocket<byte[]> socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public T receive() throws IOException {
		final byte[] data = socket.receive();
		final ByteArrayInputStream bi = new ByteArrayInputStream(data);
		final ObjectInputStream si = new ObjectInputStream(bi);
		try {
			return (T) si.readObject();
		} catch (final ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			bi.close();
			si.close();
		}
	}
}
