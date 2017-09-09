package transfer;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import data.Body;
import data.Header;
import data.IdentifierInfo;
import data.Message;
import data.MessageWithIdAndAck;
import data.SimpleMessage;
import logistics.ConversationFactory;
import logistics.MessageMatcher;
import util.ExecutorUtil;
import util.Pair;

public class UdpSharedReceiver {
	private final ConversationFactory<Pair<Header, Body>> convFact;
	private final MessageMatcher<Pair<Header, Body>, IdentifierInfo> matcher;
	private final Collection<UdpSharingReceiverSocket<Pair<Header, Body>>> receivers = Collections
			.synchronizedList(new LinkedList<UdpSharingReceiverSocket<Pair<Header, Body>>>());
	private final Semaphore sem = new Semaphore(1);
	private final UdpReceiverSocket<Message> socket;

	public UdpSharedReceiver(UdpReceiverSocket<Message> socket,
			MessageMatcher<Pair<Header, Body>, IdentifierInfo> matcher,
			ConversationFactory<Pair<Header, Body>> convFact) {
		this.socket = socket;
		this.matcher = matcher;
		this.convFact = convFact;
	}

	public void deregister(UdpSharingReceiverSocket<Pair<Header, Body>> socket) {
		receivers.remove(socket);
	}

	public void register(UdpSharingReceiverSocket<Pair<Header, Body>> socket) {
		receivers.add(socket);
	}

	public UdpSharingSocketAdapter<Pair<Header, Body>> register(UdpSharingSocketAdapter<Pair<Header, Body>> socket) {
		receivers.add(socket);
		return socket;
	}

	public void start() {
		ExecutorUtil.schedule(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						final Message data = socket.receive();
						final MessageWithIdAndAck d2 = (MessageWithIdAndAck) data;
						ExecutorUtil.schedule(new Runnable() {
							@Override
							public void run() {
								try {
									sem.acquire();
									final UdpSharingReceiverSocket<Pair<Header, Body>> receiver = matcher
											.matches(receivers, d2.getIdentifier());
									if (receiver == null) {
										convFact.newConv(d2.getIdentifier(),
												new Pair<>(((SimpleMessage) data).getHeader(),
														((SimpleMessage) data).getBody()),
												UdpSharedReceiver.this);
									} else {
										receiver.receiveData(new Pair<>(((SimpleMessage) data).getHeader(),
												((SimpleMessage) data).getBody()));
									}
									sem.release();
								} catch (final InterruptedException e) {
									e.printStackTrace();
								}
							}
						});
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
