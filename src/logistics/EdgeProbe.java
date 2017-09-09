package logistics;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import data.Body;
import data.ChunkedMessageBody;
import data.Header;
import data.IdentifierInfo;
import transfer.UdpByteSenderSocket;
import transfer.UdpGbnSenderSocket;
import transfer.UdpMessageSenderSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpSharedReceiver;
import transfer.UdpSharingSocketAdapter;
import util.Pair;

public class EdgeProbe implements Runnable {
	private class EndlessData implements Iterator<Body> {
		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Body next() {
			final ChunkedMessageBody b = new ChunkedMessageBody();
			b.c = 'a';
			return b;
		}

		@Override
		public void remove() {
		}
	}

	private final Integer destination;
	private final UdpSharedReceiver recSocket;
	private final UdpRawSenderSocket sendSocket;
	private UdpGbnSenderSocket socket;
	private final Integer source;

	public EdgeProbe(Integer destination, UdpSharedReceiver recSocket, UdpRawSenderSocket sendSocket, Integer source) {
		super();
		this.destination = destination;
		this.recSocket = recSocket;
		this.sendSocket = sendSocket;
		this.source = source;
	}

	public Integer getDestination() {
		return destination;
	}

	public Integer getLost() {
		return socket.sent - socket.received;
	}

	public Integer getSent() {
		return socket.sent;
	}

	private UdpGbnSenderSocket init() {
		final IdentifierInfo info = new IdentifierInfo(destination, source, -2);
		final UdpSharingSocketAdapter<Pair<Header, Body>> acks = recSocket
				.register(new UdpSharingSocketAdapter<Pair<Header, Body>>(info, recSocket));
		return new UdpGbnSenderSocket(new UdpMessageSenderSocket(
				new UdpByteSenderSocket(sendSocket, new ReceiverInfo(destination, InetAddress.getLoopbackAddress())),
				info), 500, 5, acks);
	}

	public Float rate() {
		return socket == null ? 1.0f : 1.0f - (float) socket.received / (float) socket.sent;
	}

	@Override
	public void run() {
		socket = init();
		try {
			socket.send(new EndlessData());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
