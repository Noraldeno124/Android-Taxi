package com.flysfo.shorttrips.statemachine.easyflow;

public interface EventHandler<C extends StatefulContext> extends Handler {
	void call(EventEnum event, StateEnum from, StateEnum to, C context) throws Exception;
}
