package transfer;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import data.Body;
import data.ChunkedMessageBody;

public class UdpStringSenderSocket implements UdpSenderSocket<String> {
	private final UdpSenderSocket<Iterator<Body>> socket;

	public UdpStringSenderSocket(UdpSenderSocket<Iterator<Body>> socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public void send(String data) throws IOException {
		final char[] chars = data.toCharArray();
		final List<Body> msgs = new LinkedList<>();
		for (final Character c : chars) {
			final ChunkedMessageBody b = new ChunkedMessageBody();
			b.c = c;
			msgs.add(b);
		}
		socket.send(msgs.iterator());
	}
}
