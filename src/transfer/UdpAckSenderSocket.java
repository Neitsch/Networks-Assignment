package transfer;

import java.io.IOException;

import data.AckBody;
import data.Body;
import util.Pair;

public class UdpAckSenderSocket implements UdpSenderSocket<Integer> {
	private final UdpSenderSocket<Pair<Integer, Body>> socket;

	public UdpAckSenderSocket(UdpSenderSocket<Pair<Integer, Body>> socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public void send(Integer data) throws IOException {
		socket.send(new Pair<Integer, Body>(data, new AckBody()));
	}
}
