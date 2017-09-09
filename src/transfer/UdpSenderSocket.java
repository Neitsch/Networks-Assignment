package transfer;

import java.io.IOException;

public interface UdpSenderSocket<T> {
	public void cleanup();

	public void send(T data) throws IOException;
}
