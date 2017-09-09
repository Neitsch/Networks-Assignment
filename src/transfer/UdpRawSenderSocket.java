package transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpRawSenderSocket implements UdpSenderSocket<DatagramPacket> {
	private final DatagramSocket socket;

	public UdpRawSenderSocket() throws SocketException {
		this(new DatagramSocket());
	}

	public UdpRawSenderSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.close();
	}

	@Override
	public void send(DatagramPacket data) throws IOException {
		socket.send(data);
	}
}
