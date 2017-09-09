package transfer;

import java.io.IOException;
import java.util.Collection;

import data.Body;
import data.EndBody;
import util.Pair;

public class UdpListSenderSocket implements UdpSenderSocket<Collection<Body>> {
	private final UdpSenderSocket<Pair<Integer, Body>> socket;

	public UdpListSenderSocket(UdpSenderSocket<Pair<Integer, Body>> socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public void send(Collection<Body> data) throws IOException {
		int seq = 0;
		for (final Body d : data) {
			socket.send(new Pair<>(seq++, d));
		}
		final Body end = new EndBody();
		socket.send(new Pair<>(seq++, end));
	}
}
