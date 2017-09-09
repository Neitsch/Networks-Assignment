package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import data.Body;
import data.EdgeBody;
import data.IdentifierInfo;
import data.MassEdgeBody;
import graph.Graph;
import io.Console;
import io.Console.LOGMODE;
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
import transfer.UdpSingleReceiverSocket;
import util.Pair;

public class dvnode {
	public static void main(String[] args) throws SocketException {
		Console.activate(LOGMODE.GRAPH);
		final Map<Integer, Float> m = new Hashtable<>();
		int count = 0;
		final Integer selfPort = Integer.valueOf(args[0]);
		final Graph g = new Graph();
		final List<EdgeBody> edges = new LinkedList<>();
		final ArrayList<UdpSenderSocket<Pair<Integer, Body>>> fanout = new ArrayList<>();
		final UdpSenderSocket<DatagramPacket> rawSock = new UdpRawSenderSocket();
		while (count < args.length - 2) {
			g.addEdge(selfPort, Integer.valueOf(args[count + 1]), Float.valueOf(args[count + 2]),
					System.currentTimeMillis());
			edges.add(new EdgeBody(selfPort, Integer.valueOf(args[count + 1]), Float.valueOf(args[count + 2]),
					System.currentTimeMillis()));
			fanout.add(
					new UdpMessageSenderSocket(
							new UdpByteSenderSocket(rawSock,
									new ReceiverInfo(Integer.valueOf(args[count + 1]),
											InetAddress.getLoopbackAddress())),
							new IdentifierInfo(selfPort, Integer.valueOf(args[count + 1]), -1)));
			count += 2;
		}

		final UdpSenderSocket<MassEdgeBody> transfer = new UdpEdgeUpdaterSenderSocket(
				new UdpFanOutSocketSender<>(fanout));
		final UdpEdgeReceiverSocket sock = new UdpEdgeReceiverSocket(
				new UdpSingleReceiverSocket(new UdpMessageReceiverSocket<>(
						new UdpByteReceiverSocket(new UdpRawReceiverSocket(new DatagramSocket(selfPort))))),
				transfer, g, selfPort);
		sock.update(new MassEdgeBody(edges), args[args.length - 1].equals("last"));
	}
}
