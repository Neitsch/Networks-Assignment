package failure;

public class DeterministicFailure implements PartialFailure {
	private int count;
	private final int d;

	public DeterministicFailure(int d) {
		this.d = d;
		count = 0;
	}

	@Override
	public boolean shouldFail() {
		return ++count % d == 0;
	}

}
