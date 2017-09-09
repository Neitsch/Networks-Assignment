package transfer;

import java.io.IOException;

public interface UdpReceiverSocket<T> {
	public void cleanup();

	public T receive() throws IOException;
}
