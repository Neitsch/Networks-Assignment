package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.Body;
import data.ChunkedMessageBody;
import data.DummyHeader;
import data.EdgeBody;
import data.Header;
import data.IdentifierInfo;
import data.MassEdgeBody;
import graph.Graph;
import io.Console;
import io.Console.LOGMODE;
import logistics.ConversationFactory;
import logistics.MessageMatcher;
import logistics.MsgHeaderMatcher;
import logistics.ReceiverInfo;
import transfer.UdpAckSenderSocket;
import transfer.UdpAckingReceiverSocket;
import transfer.UdpByteReceiverSocket;
import transfer.UdpByteSenderSocket;
import transfer.UdpEdgeReceiverSocket;
import transfer.UdpEdgeUpdaterSenderSocket;
import transfer.UdpFanOutSocketSender;
import transfer.UdpGbnSenderSocket;
import transfer.UdpMessageReceiverSocket;
import transfer.UdpMessageSenderSocket;
import transfer.UdpRawReceiverSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpReceiverSocket;
import transfer.UdpSenderSocket;
import transfer.UdpSharedReceiver;
import transfer.UdpSharingSocketAdapter;
import util.ExecutorUtil;
import util.Pair;

public class cnnode {

	private static UdpSharingSocketAdapter<Pair<Header, Body>> adapter = null;

	public static void main(String[] args) throws SocketException {
		Console.activate(LOGMODE.GRAPH);
		final List<Integer> sender = new ArrayList<>();
		final List<Pair<Integer, Float>> receiver = new ArrayList<>();
		final Integer selfPort = Integer.valueOf(args[0]);
		final List<Pair<UdpGbnSenderSocket, Pair<Integer, Integer>>> probeSenders = new ArrayList<>();
		int pointer = 2;
		final UdpSenderSocket<DatagramPacket> rawSender = new UdpRawSenderSocket();
		final ArrayList<UdpSenderSocket<Pair<Integer, Body>>> fanout = new ArrayList<>();
		final Graph g = new Graph();
		while (!args[pointer].equals("send")) {
			System.out.println(pointer + "," + args[pointer]);
			receiver.add(new Pair<>(Integer.valueOf(args[pointer]), Float.valueOf(args[pointer + 1])));
			fanout.add(new UdpMessageSenderSocket(
					new UdpByteSenderSocket(rawSender,
							new ReceiverInfo(Integer.valueOf(args[pointer]), InetAddress.getLoopbackAddress())),
					// new IdentifierInfo(selfPort,
					// Integer.valueOf(args[pointer]), -1)));
					new IdentifierInfo(0, 0, 0)));
			pointer += 2;
		}
		pointer++;
		while (pointer < args.length && !args[pointer].equals("last")) {
			sender.add(Integer.valueOf(args[pointer++]));
		}
		final UdpSenderSocket<MassEdgeBody> transfer = new UdpEdgeUpdaterSenderSocket(
				new UdpFanOutSocketSender<>(fanout));
		final MessageMatcher<Pair<Header, Body>, IdentifierInfo> m = new MsgHeaderMatcher();
		final ConversationFactory<Pair<Header, Body>> c = new ConversationFactory<Pair<Header, Body>>() {
			@Override
			public void newConv(IdentifierInfo info, Pair<Header, Body> data, UdpSharedReceiver receiver) {
				if (info.getSeq() == -1) {
				} else {
					if (adapter == null) {
						adapter = new UdpSharingSocketAdapter<>(new IdentifierInfo(0, 0, 0), receiver);
						final UdpEdgeReceiverSocket er = new UdpEdgeReceiverSocket(adapter, transfer, g, selfPort);
						er.update(new MassEdgeBody(new ArrayList<EdgeBody>()), true);
						for (final Pair<UdpGbnSenderSocket, Pair<Integer, Integer>> senderSock : probeSenders) {
							ExecutorUtil.schedule(new Runnable() {
								@Override
								public void run() {
									try {
										senderSock.getKey().send(new Iterator<Body>() {
											@Override
											public boolean hasNext() {
												// TODO Auto-generated method
												// stub
												return true;
											}

											@Override
											public Body next() {
												return new ChunkedMessageBody();
											}

											@Override
											public void remove() {

											}
										});
									} catch (final IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
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
										final Long time = System.currentTimeMillis();
										System.out.println("Rec: " + senderSock.getKey().received + " sen: "
												+ senderSock.getKey().sent);
										g.addEdge(senderSock.getValue().getKey(), senderSock.getValue().getValue(),
												(float) senderSock.getKey().received / (float) senderSock.getKey().sent,
												time);
										g.dumpBellmannFord(selfPort);
									}
								}
							});
						}
					}
					adapter.receiveData(data);
				}
			}
		};
		final UdpSharedReceiver sharedReceiver = new UdpSharedReceiver(new UdpMessageReceiverSocket<>(
				new UdpByteReceiverSocket(new UdpRawReceiverSocket(new DatagramSocket(selfPort)))), m, c);
		for (final Pair<Integer, Float> recs : receiver) {
			final UdpSharingSocketAdapter<Pair<Header, Body>> srd = new UdpSharingSocketAdapter<>(
					new IdentifierInfo(selfPort, recs.getKey(), -1), sharedReceiver);
			final UdpReceiverSocket<Pair<Header, Body>> rec = sharedReceiver.register(srd);
			final UdpAckingReceiverSocket socket = new UdpAckingReceiverSocket(rec,
					new UdpAckSenderSocket(new UdpMessageSenderSocket(
							new UdpByteSenderSocket(rawSender,
									new ReceiverInfo(recs.getKey(), InetAddress.getLoopbackAddress())),
							new IdentifierInfo(selfPort, recs.getKey(), -1))));
			ExecutorUtil.schedule(new Runnable() {
				@Override
				public void run() {
					while (true)
						try {
							socket.receive();
						} catch (final IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			});
		}
		for (final Integer sen : sender) {
			final IdentifierInfo id = new IdentifierInfo(sen, selfPort, -1);
			final UdpReceiverSocket<Pair<Header, Body>> singleRec = sharedReceiver
					.register(new UdpSharingSocketAdapter<Pair<Header, Body>>(id, sharedReceiver));
			probeSenders
					.add(new Pair<>(
							new UdpGbnSenderSocket(
									new UdpMessageSenderSocket(new UdpByteSenderSocket(rawSender,
											new ReceiverInfo(sen, InetAddress.getLoopbackAddress())), id),
									500, 5, singleRec),
							new Pair<>(selfPort, sen)));
		}
		sharedReceiver.start();
		if (args[args.length - 1].equals("last")) {
			c.newConv(new IdentifierInfo(0, 0, 0),
					new Pair<Header, Body>(new DummyHeader(), new MassEdgeBody(new ArrayList<EdgeBody>())),
					sharedReceiver);
		}
	}
}
