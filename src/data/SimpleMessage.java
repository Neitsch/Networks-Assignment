package data;

public class SimpleMessage implements MessageWithIdAndAck {
	private final Body body;
	private final Header header;
	private final IdentifierInfo info;

	public SimpleMessage(Body body, Header header, IdentifierInfo info) {
		super();
		this.body = body;
		this.header = header;
		this.info = info;
	}

	public Body getBody() {
		return body;
	}

	public Header getHeader() {
		return header;
	}

	@Override
	public IdentifierInfo getIdentifier() {
		return info;
	}

	public int getSequence() {
		return header.getSequence();
	}

	@Override
	public String toString() {
		return body == null ? "" : body.toString();
	}
}
