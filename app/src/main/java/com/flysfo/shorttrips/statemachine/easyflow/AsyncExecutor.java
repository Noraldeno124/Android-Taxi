package com.flysfo.shorttrips.statemachine.easyflow;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncExecutor implements Executor {
	private Executor executor = Executors.newSingleThreadExecutor();

	@Override
	public void execute(@NonNull Runnable task) {
		executor.execute(task);
	}
}
