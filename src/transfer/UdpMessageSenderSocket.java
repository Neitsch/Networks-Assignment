package transfer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import data.AckBody;
import data.Body;
import data.DummyHeader;
import data.IdentifierInfo;
import data.Message;
import data.SimpleMessage;
import io.Console;
import io.Console.LOGMODE;
import util.Pair;

public class UdpMessageSenderSocket implements UdpSenderSocket<Pair<Integer, Body>> {
	private final IdentifierInfo info;
	private final UdpSenderSocket<byte[]> socket;

	public UdpMessageSenderSocket(UdpSenderSocket<byte[]> socket, IdentifierInfo info) {
		this.socket = socket;
		this.info = info;
	}

	@Override
	public void cleanup() {
		socket.cleanup();
	}

	@Override
	public void send(Pair<Integer, Body> data) throws IOException {
		if (false && !(data.getValue() instanceof AckBody))
			Console.log("packet" + data.getKey() + " " + data.getValue() + " sent", LOGMODE.PACKET);
		final Message m = new SimpleMessage(data.getValue(), new DummyHeader(data.getKey()), info);
		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		final ObjectOutputStream so = new ObjectOutputStream(bo);
		so.writeObject(m);
		final byte[] result = bo.toByteArray();
		so.flush();
		so.close();
		bo.close();
		socket.send(result);
	}
}
