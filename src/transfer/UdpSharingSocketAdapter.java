package transfer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import data.Body;
import data.Header;
import data.IdentifierInfo;
import util.Pair;

public class UdpSharingSocketAdapter<T> implements UdpSharingReceiverSocket<T>, UdpReceiverSocket<T> {
	private final IdentifierInfo info;
	private final BlockingQueue<T> queue;
	private final UdpSharedReceiver rec;

	public UdpSharingSocketAdapter(IdentifierInfo info, UdpSharedReceiver rec) {
		queue = new LinkedBlockingQueue<>();
		this.info = info;
		this.rec = rec;
	}

	@Override
	public void cleanup() {
		rec.deregister((UdpSharingReceiverSocket<Pair<Header, Body>>) this);
		rec.register(new DeadReceiver(info));
	}

	@Override
	public IdentifierInfo getIdentifierInfo() {
		return info;
	}

	@Override
	public T receive() throws IOException {
		try {
			return queue.take();
		} catch (final InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void receiveData(T data) {
		try {
			queue.put(data);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
