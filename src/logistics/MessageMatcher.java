package logistics;

import java.util.Collection;

import transfer.UdpSharingReceiverSocket;

public interface MessageMatcher<S, T> {
	public UdpSharingReceiverSocket<S> matches(Collection<UdpSharingReceiverSocket<S>> receiver, T data);
}
