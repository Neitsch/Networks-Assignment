package failure;

import java.util.Random;

public class ProbabilisticFailure implements PartialFailure {
	private final float failPercent;
	private final Random ran;

	public ProbabilisticFailure(float failPercent) {
		this.failPercent = failPercent;
		ran = new Random();
	}

	@Override
	public boolean shouldFail() {
		return ran.nextFloat() <= failPercent;
	}

}
