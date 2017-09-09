package logistics;

import java.io.IOException;
import java.net.InetAddress;

import data.Body;
import data.Header;
import data.IdentifierInfo;
import failure.ProbabilisticFailure;
import transfer.UdpAckSenderSocket;
import transfer.UdpAckingReceiverSocket;
import transfer.UdpByteSenderSocket;
import transfer.UdpFailingReceiverSocket;
import transfer.UdpMessageSenderSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpSharedReceiver;
import transfer.UdpSharingSocketAdapter;
import util.Pair;

public class EdgeProbeAck implements Runnable {
	private final Integer destination;
	private final UdpSharedReceiver recSocket;
	private final UdpRawSenderSocket sendSocket;
	private final UdpAckingReceiverSocket socket;
	private final Integer source;

	public EdgeProbeAck(Integer destination, UdpSharedReceiver recSocket, UdpRawSenderSocket sendSocket, Integer self,
			float failure) {
		super();
		this.destination = destination;
		this.recSocket = recSocket;
		this.sendSocket = sendSocket;
		source = self;
		final IdentifierInfo info = new IdentifierInfo(source, destination, -2);
		final UdpSharingSocketAdapter<Pair<Header, Body>> sharing = new UdpSharingSocketAdapter<>(info, recSocket);
		socket = new UdpAckingReceiverSocket(new UdpFailingReceiverSocket<>(new ProbabilisticFailure(failure), sharing),
				new UdpAckSenderSocket(new UdpMessageSenderSocket(new UdpByteSenderSocket(sendSocket,
						new ReceiverInfo(destination, InetAddress.getLoopbackAddress())), info)));
		recSocket.register(sharing);
	}

	@Override
	public void run() {
		while (true) {
			try {
				socket.receive();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
