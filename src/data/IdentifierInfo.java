package data;

import java.io.Serializable;

public class IdentifierInfo implements Serializable {
	private final int receiverPort;
	private final int senderPort;
	private final int seq;

	public IdentifierInfo(int receiverPort, int senderPort, int seq) {
		super();
		this.receiverPort = receiverPort;
		this.senderPort = senderPort;
		this.seq = seq;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdentifierInfo) {
			final IdentifierInfo info = (IdentifierInfo) obj;
			return info.receiverPort == receiverPort && info.senderPort == senderPort && info.seq == seq;
		}
		return false;
	}

	public int getReceiverPort() {
		return receiverPort;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public int getSeq() {
		return seq;
	}

	@Override
	public int hashCode() {
		return ((Integer) seq).hashCode() + ((Integer) receiverPort).hashCode() + 2 * ((Integer) senderPort).hashCode();
	}

	@Override
	public String toString() {
		return this.getClass() + "[recPort=" + receiverPort + ",senderPort=" + senderPort + ",seq=" + seq + "]";
	}
}
