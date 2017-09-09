package util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorUtil {
	private static final ExecutorService execService;

	static {
		execService = Executors.newCachedThreadPool();
	}

	public static <T> Future<T> schedule(Callable<T> task) {
		return execService.submit(task);
	}

	public static void schedule(Runnable task) {
		execService.execute(task);
	}
}
