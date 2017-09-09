package transfer;

import java.io.IOException;

import data.Body;
import data.Header;
import data.Message;
import data.SimpleMessage;
import util.Pair;

public class UdpSingleReceiverSocket implements UdpReceiverSocket<Pair<Header, Body>> {
	private final UdpReceiverSocket<Message> socket;

	public UdpSingleReceiverSocket(UdpReceiverSocket<Message> socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public Pair<Header, Body> receive() throws IOException {
		final SimpleMessage m = (SimpleMessage) socket.receive();
		return new Pair<>(m.getHeader(), m.getBody());
	}
}
