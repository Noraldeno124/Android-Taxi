package com.flysfo.shorttrips.statemachine.easyflow;

public class LogicViolationError extends Exception {
	private static final long serialVersionUID = 904045792722645067L;

	public LogicViolationError(String message) {
		super(message);
	}
}
