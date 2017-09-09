package transfer;

import java.io.IOException;

import data.Body;
import data.EdgeBody;
import data.Header;
import data.MassEdgeBody;
import graph.Graph;
import util.ExecutorUtil;
import util.Pair;

public class UdpEdgeReceiverSocket {
	private final Graph g;
	private final Integer self;
	private final UdpSenderSocket<MassEdgeBody> transfer;
	private final UdpReceiverSocket<Pair<Header, Body>> udpMessageReceiverSocket;

	public UdpEdgeReceiverSocket(UdpReceiverSocket<Pair<Header, Body>> udpMessageReceiverSocket,
			UdpSenderSocket<MassEdgeBody> transfer, Graph g, Integer self) {
		this.g = g;
		this.transfer = transfer;
		this.udpMessageReceiverSocket = udpMessageReceiverSocket;
		this.self = self;
	}

	public void receiveEdges(Pair<Header, Body> m) throws IOException {
		boolean cont = false;
		final MassEdgeBody e = (MassEdgeBody) m.getValue();
		for (final EdgeBody b : e.getBodies()) {
			final boolean isnew = g.addEdge(b.getSource(), b.getDestination(), b.getWeight(), b.getUpdateTime());
			if (isnew) cont = true;
		}
		if (cont == true) {
			transfer.send(e);
			g.dumpBellmannFord(self);
		}
	}

	public void update(final MassEdgeBody massEdgeBody, final boolean start) {
		ExecutorUtil.schedule(new Runnable() {
			@Override
			public void run() {
				Pair<Header, Body> m = null;
				try {
					if (!start) {
						m = udpMessageReceiverSocket.receive();
					}
					transfer.send(massEdgeBody);
					while (true) {
						try {
							if (m != null) {
								receiveEdges(m);
							}
							m = udpMessageReceiverSocket.receive();
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
				} catch (final IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
