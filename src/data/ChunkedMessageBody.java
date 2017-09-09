package data;

public class ChunkedMessageBody implements Body {
	public Character c;

	@Override
	public String toString() {
		return Character.toString(c);
	}
}
