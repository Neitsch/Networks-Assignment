package logistics;

import data.IdentifierInfo;
import transfer.UdpSharedReceiver;

public interface ConversationFactory<T> {
	public void newConv(IdentifierInfo info, T data, UdpSharedReceiver receiver);
}
