package test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import data.AckBody;
import data.Body;
import data.DummyHeader;
import data.EdgeBody;
import data.Header;
import data.IdentifierInfo;
import data.MassEdgeBody;
import graph.Graph;
import io.Console;
import io.Console.LOGMODE;
import logistics.ConversationFactory;
import logistics.EdgeProbe;
import logistics.EdgeProbeAck;
import logistics.MsgHeaderMatcher;
import logistics.ReceiverInfo;
import transfer.UdpByteReceiverSocket;
import transfer.UdpByteSenderSocket;
import transfer.UdpEdgeReceiverSocket;
import transfer.UdpEdgeUpdaterSenderSocket;
import transfer.UdpFanOutSocketSender;
import transfer.UdpMessageReceiverSocket;
import transfer.UdpMessageSenderSocket;
import transfer.UdpRawReceiverSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpSenderSocket;
import transfer.UdpSharedReceiver;
import transfer.UdpSharingSocketAdapter;
import util.ExecutorUtil;
import util.Pair;

public class cnnode2 {
	private static UdpSharedReceiver sharedReceiver;
	private static boolean started = false;

	public static void main(String[] args) throws IOException {
		Console.activate(LOGMODE.GRAPH);
		int pointer = 2;
		final List<Integer> peers = new LinkedList<>();
		final Integer self = Integer.parseInt(args[0]);
		System.out.println("Listening on " + self);
		final List<EdgeProbe> probes = new LinkedList<>();
		final UdpRawSenderSocket sendSocket = new UdpRawSenderSocket();
		final UdpSharedReceiver recSocket = new UdpSharedReceiver(
				new UdpMessageReceiverSocket<>(
						new UdpByteReceiverSocket(new UdpRawReceiverSocket(new DatagramSocket(self)))),
				new MsgHeaderMatcher(), new ConversationFactory<Pair<Header, Body>>() {

					@Override
					public void newConv(IdentifierInfo info, Pair<Header, Body> data, UdpSharedReceiver receiver) {
						if (info.getReceiverPort() == 0 && info.getSenderPort() == 0 && info.getSeq() == 0) {
							try {
								startProbing(probes, peers, sendSocket, self);
							} catch (final IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
		sharedReceiver = recSocket;
		while (!args[pointer].equals("send")) {
			ExecutorUtil.schedule(new EdgeProbeAck(Integer.parseInt(args[pointer]), recSocket, sendSocket, self,
					Float.parseFloat(args[pointer + 1])));
			peers.add(Integer.parseInt(args[pointer]));
			pointer += 2;
		}
		pointer++;
		while (pointer < args.length && !args[pointer].equals("last")) {
			probes.add(new EdgeProbe(Integer.valueOf(args[pointer]), recSocket, sendSocket, self));
			peers.add(Integer.valueOf(args[pointer]));
			pointer++;
		}
		ExecutorUtil.schedule(new Runnable() {
			@Override
			public void run() {
				recSocket.start();
			}
		});
		if (args[args.length - 1].equals("last")) {
			startProbing(probes, peers, sendSocket, self);
		}
	}

	private static boolean startProbing(final List<EdgeProbe> probes, List<Integer> peers,
			UdpRawSenderSocket sendSocket, final Integer self) throws IOException {
		if (started) {
			return false;
		}
		for (final EdgeProbe p : probes) {
			ExecutorUtil.schedule(p);
		}
		final ArrayList<UdpSenderSocket<Pair<Integer, Body>>> fanout = new ArrayList<>();
		for (final Integer peer : peers) {
			new UdpMessageSenderSocket(
					new UdpByteSenderSocket(sendSocket, new ReceiverInfo(peer, InetAddress.getLoopbackAddress())),
					new IdentifierInfo(0, 0, 0)).send(new Pair<Integer, Body>(-1, new AckBody()));
			fanout.add(new UdpMessageSenderSocket(
					new UdpByteSenderSocket(sendSocket,
							new ReceiverInfo(Integer.valueOf(peer), InetAddress.getLoopbackAddress())),
					new IdentifierInfo(1, 1, 1)));
		}
		final Graph g = new Graph();
		final UdpSenderSocket<MassEdgeBody> transfer = new UdpEdgeUpdaterSenderSocket(
				new UdpFanOutSocketSender<>(fanout));
		final UdpSharingSocketAdapter<Pair<Header, Body>> sada = new UdpSharingSocketAdapter<>(
				new IdentifierInfo(1, 1, 1), sharedReceiver);
		sharedReceiver.register(sada);
		final UdpEdgeReceiverSocket sock = new UdpEdgeReceiverSocket(sada, transfer, g, self);
		ExecutorUtil.schedule(new Runnable() {

			@Override
			public void run() {
				sock.update(new MassEdgeBody(new ArrayList<EdgeBody>()), true);
			}
		});
		ExecutorUtil.schedule(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					for (final EdgeProbe p : probes) {
						Console.log("Link to " + p.getDestination() + ": " + p.getSent() + " packets sent, "
								+ p.getLost() + " packets lost, loss rate " + p.rate(), LOGMODE.GRAPH);
					}
				}
			}
		});
		ExecutorUtil.schedule(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					final List<EdgeBody> bodies = new LinkedList<>();
					final Long time = System.currentTimeMillis();
					for (final EdgeProbe p : probes) {
						bodies.add(new EdgeBody(self, p.getDestination(), p.rate(), time));
					}
					try {
						sock.receiveEdges(new Pair<Header, Body>(new DummyHeader(), new MassEdgeBody(bodies)));
					} catch (final IOException e) {
						e.printStackTrace();
					}
					g.dumpBellmannFord(self);
				}
			}
		});
		return started = true;
	}
}
