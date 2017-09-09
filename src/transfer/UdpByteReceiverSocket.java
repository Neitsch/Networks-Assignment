package transfer;

import java.io.IOException;
import java.net.DatagramPacket;

public class UdpByteReceiverSocket implements UdpReceiverSocket<byte[]> {

	private final UdpReceiverSocket<DatagramPacket> socket;

	public UdpByteReceiverSocket(UdpReceiverSocket<DatagramPacket> socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public byte[] receive() throws IOException {
		final DatagramPacket data = socket.receive();
		return data.getData();
	}
}
