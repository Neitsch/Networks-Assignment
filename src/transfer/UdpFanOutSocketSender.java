package transfer;

import java.io.IOException;
import java.util.ArrayList;

public class UdpFanOutSocketSender<T> implements UdpSenderSocket<T> {
	private final ArrayList<UdpSenderSocket<T>> fanout;

	public UdpFanOutSocketSender(ArrayList<UdpSenderSocket<T>> fanout) {
		this.fanout = fanout;
	}

	@Override
	public void cleanup() {

	}

	@Override
	public void send(T data) throws IOException {
		for (final UdpSenderSocket<T> socket : fanout) {
			socket.send(data);
		}
	}
}
