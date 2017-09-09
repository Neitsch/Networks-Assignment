package logistics;

import java.util.Collection;

import data.Body;
import data.Header;
import data.IdentifierInfo;
import transfer.UdpSharingReceiverSocket;
import util.Pair;

public class MsgHeaderMatcher implements MessageMatcher<Pair<Header, Body>, IdentifierInfo> {
	@Override
	public UdpSharingReceiverSocket<Pair<Header, Body>> matches(
			Collection<UdpSharingReceiverSocket<Pair<Header, Body>>> receiver, IdentifierInfo data) {
		for (final UdpSharingReceiverSocket<Pair<Header, Body>> rec : receiver) {
			if (data.equals(rec.getIdentifierInfo())) {
				return rec;
			}
		}
		return null;
	}
}
