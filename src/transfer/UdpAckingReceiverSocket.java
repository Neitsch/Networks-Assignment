package transfer;

import java.io.IOException;

import data.Body;
import data.DummyHeader;
import data.Header;
import util.Pair;

public class UdpAckingReceiverSocket implements UdpReceiverSocket<Body> {
	private static final Integer WINDOW_SIZE = 1;
	private Integer lastAcked;
	private final UdpSenderSocket<Integer> senderSocket;
	private final UdpReceiverSocket<Pair<Header, Body>> socket;

	public UdpAckingReceiverSocket(UdpReceiverSocket<Pair<Header, Body>> ada, UdpSenderSocket<Integer> senderSocket) {
		socket = ada;
		lastAcked = -1;
		this.senderSocket = senderSocket;
	}

	@Override
	public void cleanup() {
		senderSocket.cleanup();
		socket.cleanup();
	}

	@Override
	public Body receive() throws IOException {
		while (true) {
			final Pair<Header, Body> pair = socket.receive();
			if (((DummyHeader) pair.getKey()).getSequence() == lastAcked + 1) {
				lastAcked++;
				senderSocket.send(lastAcked);
				return pair.getValue();
			} else {
				senderSocket.send(lastAcked);
			}
		}
	}
}
