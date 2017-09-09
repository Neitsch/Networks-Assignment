package transfer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import data.Body;
import data.EndBody;
import data.Header;
import util.ExecutorUtil;
import util.Pair;

public class UdpGbnSenderSocket implements UdpSenderSocket<Iterator<Body>> {
	private class Timeout implements Runnable {
		private final Semaphore sem = new Semaphore(0);
		public boolean trigger;

		public Timeout() {
			trigger = true;
		}

		public void cancel() {
			trigger = false;
			sem.release();
		}

		@Override
		public void run() {
			try {
				sem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			if (trigger) try {
				timeoutCount++;
				callback();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private final UdpReceiverSocket<Pair<Header, Body>> acks;
	private final List<Body> data;
	private Iterator<Body> pointer;
	public int received;
	private final Semaphore sem = new Semaphore(1);
	public int sent;
	private int seq = 0;
	private final int timeout;
	private int timeoutCount;
	private Timeout to;
	private final UdpSenderSocket<Pair<Integer, Body>> udpMessageSenderSocket;
	private final int window;

	public UdpGbnSenderSocket(UdpSenderSocket<Pair<Integer, Body>> udpMessageSenderSocket, int timeout, int window,
			UdpReceiverSocket<Pair<Header, Body>> acks) {
		this.udpMessageSenderSocket = udpMessageSenderSocket;
		this.timeout = timeout;
		this.window = window;
		this.acks = acks;
		data = new LinkedList<>();
		timeoutCount = 0;
	}

	private void ack(Integer id) {
		received++;
		timeoutCount = 0;
		try {
			sem.acquire();
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (id >= seq) {
			to.cancel();
			final int diff = id - seq + 1;
			boolean ended = false;
			if (data.get(data.size() - 1) instanceof EndBody) ended = true;
			for (int i = 0; i < diff; i++)
				data.remove(0);
			final boolean added = pointer.hasNext();
			for (int i = 0; i < diff && pointer.hasNext(); i++) {
				data.add(pointer.next());
			}
			if (data.size() < window && !ended) {
				data.add(new EndBody());
			}
			try {
				if (added)
					sendWindow(data.subList(window - diff, Math.min(window, data.size())), seq + window - id + seq + 1);
			} catch (final IOException e) {
				e.printStackTrace();
			}
			seq = id + 1;
			if (data.size() > 0) {
				to = new Timeout();
				ExecutorUtil.schedule(to);
			}
		}
		if (data.size() > 0) {
			ExecutorUtil.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						final Pair<Header, Body> msg = acks.receive();
						ack(msg.getKey().getSequence());
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		sem.release();
	}

	private void callback() throws IOException {
		try {
			sem.acquire();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (timeoutCount <= 20) {
			sendWindow(data, seq);
			if (to != null) to.cancel();
			to = new Timeout();
			ExecutorUtil.schedule(to);
		} else {
			System.out.println("Timeout occured to often - terminating");
		}
		sem.release();
	}

	@Override
	public void cleanup() {
		acks.cleanup();
		udpMessageSenderSocket.cleanup();
		to.cancel();
	}

	@Override
	public void send(Iterator<Body> data) throws IOException {
		ExecutorUtil.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					final Pair<Header, Body> msg = acks.receive();
					ack(msg.getKey().getSequence());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});
		pointer = data;
		for (int i = 0; i < window; i++) {
			if (pointer.hasNext()) this.data.add(pointer.next());
		}
		callback();
	}

	private void sendWindow(Collection<Body> data, Integer seqStart) throws IOException {
		for (final Body b : data) {
			sent++;
			udpMessageSenderSocket.send(new Pair<>(seqStart++, b));
		}
	}
}
