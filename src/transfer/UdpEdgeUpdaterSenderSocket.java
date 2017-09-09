package transfer;

import java.io.IOException;

import data.Body;
import util.Pair;

public class UdpEdgeUpdaterSenderSocket<T extends Body> implements UdpSenderSocket<T> {
	private final UdpSenderSocket<Pair<Integer, Body>> socket;

	public UdpEdgeUpdaterSenderSocket(UdpSenderSocket<Pair<Integer, Body>> socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(T data) throws IOException {
		final Body b = data;
		socket.send(new Pair<>(0, b));
	}
}
