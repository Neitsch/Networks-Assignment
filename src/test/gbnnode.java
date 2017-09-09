package test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import failure.DeterministicFailure;
import failure.NoFailure;
import failure.PartialFailure;
import failure.ProbabilisticFailure;
import io.Console;
import io.Console.LOGMODE;
import logistics.MsgHeaderMatcher;
import logistics.ReceiverConversationFactory;
import logistics.Sender;
import transfer.UdpByteReceiverSocket;
import transfer.UdpMessageReceiverSocket;
import transfer.UdpRawReceiverSocket;
import transfer.UdpRawSenderSocket;
import transfer.UdpSharedReceiver;

public class gbnnode {
	public static void main(String[] args) throws IOException {
		Console.activate(LOGMODE.PACKET);
		PartialFailure f = new NoFailure();
		if (args.length >= 5) {
			if (args[3].equals("-p")) {
				f = new ProbabilisticFailure(Float.valueOf(args[4]));
			} else if (args[3].equals("-d")) {
				f = new DeterministicFailure(Integer.valueOf(args[4]));
			} else {
				System.out.println("Bad input args");
			}
		}
		final UdpRawSenderSocket sender = new UdpRawSenderSocket(new DatagramSocket());
		final UdpSharedReceiver sharedReceiver = new UdpSharedReceiver(
				new UdpMessageReceiverSocket<>(new UdpByteReceiverSocket(
						new UdpRawReceiverSocket(new DatagramSocket(Integer.valueOf(args[0]))))),
				new MsgHeaderMatcher(), new ReceiverConversationFactory(sender, f));
		sharedReceiver.start();
		final Sender s = new Sender(sender, sharedReceiver, Integer.valueOf(args[2]), f);
		final Scanner scan = new Scanner(System.in);
		while (true) {
			System.out.print("node> ");
			final String line = scan.nextLine();
			s.send(line.substring(line.indexOf(' ') + 1), Integer.valueOf(args[0]), Integer.valueOf(args[1]),
					InetAddress.getLoopbackAddress());
		}
	}
}
