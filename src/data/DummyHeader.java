package data;

public class DummyHeader implements Header {
	private final int i;

	public DummyHeader() {
		this(0);
	}

	public DummyHeader(int i) {
		this.i = i;
	}

	@Override
	public int getSequence() {
		return i;
	}
}
