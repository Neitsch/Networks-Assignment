package util;

public class Pair<S, T> {
	private final S key;
	private final T value;

	public Pair(S key, T value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			try {
				final Pair<S, T> pair = (Pair<S, T>) obj;
				return key.equals(pair.getKey()) && value.equals(pair.getValue());
			} catch (final Exception e) {
				return false;
			}
		}
		return false;
	}

	public S getKey() {
		return key;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
	}

	@Override
	public String toString() {
		return "K: " + key + ", V: " + value;
	}
}
