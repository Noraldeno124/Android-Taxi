package com.flysfo.shorttrips.statemachine.easyflow;

public interface StateHandler<C extends StatefulContext> extends Handler {
	void call(StateEnum state, C context) throws Exception;
}
