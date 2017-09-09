package transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpRawReceiverSocket implements UdpReceiverSocket<DatagramPacket> {
	private final DatagramSocket socket;

	public UdpRawReceiverSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.close();
	}

	@Override
	public DatagramPacket receive() throws IOException {
		final DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
		socket.receive(packet);
		return packet;
	}
}
