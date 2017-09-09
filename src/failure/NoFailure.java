package failure;

public class NoFailure implements PartialFailure {

	@Override
	public boolean shouldFail() {
		return false;
	}

}
