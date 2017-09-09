package logistics;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import data.Body;
import data.ChunkedMessageBody;
import data.Header;
import data.IdentifierInfo;
import failure.PartialFailure;
import transfer.UdpAckSenderSocket;
import transfer.UdpAckingReceiverSocket;
import transfer.UdpByteSenderSocket;
import transfer.UdpFailingReceiverSocket;
import transfer.UdpListReceiverSocket;
import transfer.UdpMessageSenderSocket;
import transfer.UdpReceiverSocket;
import transfer.UdpSenderSocket;
import transfer.UdpSharedReceiver;
import transfer.UdpSharingSocketAdapter;
import transfer.UdpStringReceiverSocket;
import util.ExecutorUtil;
import util.Pair;

public class ReceiverConversationFactory implements ConversationFactory<Pair<Header, Body>> {
	private final PartialFailure failure;
	private final UdpSenderSocket<DatagramPacket> socket;

	public ReceiverConversationFactory(UdpSenderSocket<DatagramPacket> socket, PartialFailure failure) {
		super();
		this.socket = socket;
		this.failure = failure;
	}

	@Override
	public void newConv(IdentifierInfo info, Pair<Header, Body> data, UdpSharedReceiver receiver) {
		if (data.getValue() instanceof ChunkedMessageBody) {
			final UdpSharingSocketAdapter<Pair<Header, Body>> ada = new UdpSharingSocketAdapter<>(info, receiver);
			ada.receiveData(data);
			receiver.register(ada);
			final UdpFailingReceiverSocket fs = new UdpFailingReceiverSocket<>(failure, ada);
			final UdpReceiverSocket<String> sock = new UdpStringReceiverSocket(
					new UdpListReceiverSocket(
							new UdpAckingReceiverSocket(fs,
									new UdpAckSenderSocket(new UdpMessageSenderSocket(new UdpByteSenderSocket(socket,
											new ReceiverInfo(info.getSenderPort(), InetAddress.getLoopbackAddress())),
											info)))));
			ExecutorUtil.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						sock.receive();
						System.out.println(fs.statistics());
						fs.reset();
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
}
