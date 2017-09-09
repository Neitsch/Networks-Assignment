package transfer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import data.Body;
import data.ChunkedMessageBody;

public class UdpStringReceiverSocket implements UdpReceiverSocket<String> {
	private final UdpReceiverSocket<Collection<Body>> socket;

	public UdpStringReceiverSocket(UdpReceiverSocket<Collection<Body>> socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public String receive() throws IOException {
		final Iterator<Body> it = socket.receive().iterator();
		final StringBuilder builder = new StringBuilder();
		while (it.hasNext()) {
			builder.append(((ChunkedMessageBody) it.next()).c);
		}
		return builder.toString();
	}

}
