package com.flysfo.shorttrips.statemachine.easyflow;

public interface ExecutionErrorHandler<C extends StatefulContext> extends Handler {
	void call(ExecutionError error, C context);
}
