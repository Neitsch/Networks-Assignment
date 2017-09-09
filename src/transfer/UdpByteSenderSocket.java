package transfer;

import java.io.IOException;
import java.net.DatagramPacket;

import logistics.ReceiverInfo;

public class UdpByteSenderSocket implements UdpSenderSocket<byte[]> {
	private final ReceiverInfo info;
	private final UdpSenderSocket<DatagramPacket> socket;

	public UdpByteSenderSocket(UdpSenderSocket<DatagramPacket> socket, ReceiverInfo info) {
		this.socket = socket;
		this.info = info;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public void send(byte[] data) throws IOException {
		final DatagramPacket packet = new DatagramPacket(data, data.length, info.getAddress(), info.getPort());
		socket.send(packet);
	}
}
