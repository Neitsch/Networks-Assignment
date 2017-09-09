package transfer;

import java.io.IOException;
import java.text.DecimalFormat;

import data.AckBody;
import data.Body;
import data.Header;
import failure.PartialFailure;
import io.Console;
import io.Console.LOGMODE;
import util.Pair;

public class UdpFailingReceiverSocket<T> implements UdpReceiverSocket<T> {
	private static DecimalFormat df = new DecimalFormat("#.##");
	private int count;
	private int fail;
	private final PartialFailure failure;

	private final UdpReceiverSocket<T> socket;

	public UdpFailingReceiverSocket(PartialFailure failure, UdpReceiverSocket<T> socket) {
		super();
		this.failure = failure;
		this.socket = socket;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public T receive() throws IOException {
		T data;
		boolean fail;
		do {
			data = socket.receive();
			fail = failure.shouldFail();
			try {
				final Pair<Header, Body> dt = (Pair<Header, Body>) data;
				if (fail) {
					this.fail++;
					if (dt.getValue() instanceof AckBody) {
						Console.log("ACK" + dt.getKey().getSequence() + " discarded", LOGMODE.PACKET);
					} else {
						Console.log(
								"packet" + dt.getKey().getSequence() + " " + dt.getValue().toString() + " discarded",
								LOGMODE.PACKET);
					}
				} else {
					if (dt.getValue() instanceof AckBody) {
						Console.log("ACK" + dt.getKey().getSequence() + " received", LOGMODE.PACKET);
					} else {
						Console.log("packet" + dt.getKey().getSequence() + " " + dt.getValue().toString() + " received",
								LOGMODE.PACKET);
					}
				}
				count++;
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} while (fail);
		return data;
	}

	public void reset() {
		fail = 0;
		count = 0;
	}

	public String statistics() {
		return "[Summary] " + fail + "/" + count + " packets dropped, loss rate = " + df.format(100.0d * fail / count)
				+ "%";
	}
}
