package logistics;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import data.Body;
import data.Header;
import data.IdentifierInfo;
import failure.PartialFailure;
import transfer.UdpByteSenderSocket;
import transfer.UdpGbnSenderSocket;
import transfer.UdpMessageSenderSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpSenderSocket;
import transfer.UdpSharedReceiver;
import transfer.UdpSharingSocketAdapter;
import transfer.UdpStringSenderSocket;
import util.Pair;

public class Sender {
	private final PartialFailure failure;
	private final Map<Pair<Integer, Integer>, Integer> map;
	private final UdpSharedReceiver receiver;
	private final Semaphore sem;
	private final UdpRawSenderSocket socket;
	private final int window;

	public Sender(UdpRawSenderSocket socket, UdpSharedReceiver receiver, Integer window, PartialFailure f)
			throws SocketException {
		map = new HashMap<>();
		sem = new Semaphore(1);
		this.socket = socket;
		this.receiver = receiver;
		this.window = window;
		failure = f;
	}

	public void send(String message, int selfPort, int peerPort, InetAddress peerAddress) throws IOException {
		final Pair<Integer, Integer> ports = new Pair<>(selfPort, peerPort);
		Integer seq = 0;
		try {
			sem.acquire();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		if (map.containsKey(ports)) {
			seq = map.get(ports) + 1;
		}
		final IdentifierInfo id = new IdentifierInfo(peerPort, selfPort, seq);
		final UdpSharingSocketAdapter<Pair<Header, Body>> rec = new UdpSharingSocketAdapter<>(id, receiver);
		final UdpSenderSocket<String> msg = new UdpStringSenderSocket(
				new UdpGbnSenderSocket(
						new UdpMessageSenderSocket(
								new UdpByteSenderSocket(socket, new ReceiverInfo(peerPort, peerAddress)), id),
						500, window, rec));
		receiver.register(rec);
		map.put(ports, seq);
		sem.release();
		msg.send(message);
	}
}
