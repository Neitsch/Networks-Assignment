package transfer;

import data.IdentifierInfo;

public interface UdpSharingReceiverSocket<T> {
	IdentifierInfo getIdentifierInfo();

	void receiveData(T data);
}
