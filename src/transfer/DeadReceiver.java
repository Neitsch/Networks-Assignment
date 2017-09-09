package transfer;

import data.Body;
import data.Header;
import data.IdentifierInfo;
import util.Pair;

public class DeadReceiver implements UdpSharingReceiverSocket<Pair<Header, Body>> {
	private final IdentifierInfo info;

	public DeadReceiver(IdentifierInfo info) {
		this.info = info;
	}

	@Override
	public IdentifierInfo getIdentifierInfo() {
		return info;
	}

	@Override
	public void receiveData(Pair<Header, Body> data) {
	}
}
