package transfer;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import data.Body;
import data.EndBody;

public class UdpListReceiverSocket implements UdpReceiverSocket<Collection<Body>> {
	private final UdpReceiverSocket<Body> socket;

	public UdpListReceiverSocket(UdpReceiverSocket<Body> socket) {
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public Collection<Body> receive() throws IOException {
		final List<Body> list = new LinkedList<>();
		Body data;
		do {
			data = socket.receive();
			list.add(data);
		} while (!(data instanceof EndBody));
		list.remove(list.size() - 1);
		return list;
	}
}
