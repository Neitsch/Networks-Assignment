package logistics;

import java.net.DatagramSocket;
import java.net.SocketException;

import failure.PartialFailure;
import transfer.UdpByteReceiverSocket;
import transfer.UdpMessageReceiverSocket;
import transfer.UdpRawReceiverSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpSharedReceiver;

public class Receiver {
	private final UdpSharedReceiver sharedReceiver;

	public Receiver(int port, UdpRawSenderSocket socket, PartialFailure failure) throws SocketException {
		sharedReceiver = new UdpSharedReceiver(
				new UdpMessageReceiverSocket<>(
						new UdpByteReceiverSocket(new UdpRawReceiverSocket(new DatagramSocket(port)))),
				new MsgHeaderMatcher(), new ReceiverConversationFactory(socket, failure));
		sharedReceiver.start();
	}

	public UdpSharedReceiver receiver() {
		return sharedReceiver;
	}
}
