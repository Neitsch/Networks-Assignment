package logistics;

import java.net.InetAddress;

public class ReceiverInfo {
	private final InetAddress address;
	private final Integer port;

	public ReceiverInfo(Integer port, InetAddress address) {
		this.port = port;
		this.address = address;
	}

	public InetAddress getAddress() {
		return address;
	}

	public Integer getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "Receiver port: " + port + ", inet: " + address;
	}
}
