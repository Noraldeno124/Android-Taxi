package com.flysfo.shorttrips.statemachine.easyflow;

import android.util.Pair;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.polling.Poller;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.statemachine.easyflow.HandlerCollection.EventType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyFlow<C extends StatefulContext> {
    public class DefaultErrorHandler implements ExecutionErrorHandler<StatefulContext> {
        @Override
        public void call(ExecutionError error, StatefulContext context) {
            String msg = "Execution Error in StateHolder [" + error.getState() + "] ";
            if (error.getEvent() != null) {
                msg += "on EventHolder [" + error.getEvent() + "] ";
            }
            msg += "with Context [" + error.getContext() + "] ";

            Exception e = new Exception(msg, error);
            log.error("Error", e);
        }
    }

    private StateEnum startState;
    private TransitionCollection transitions;

    private HandlerCollection handlers = new HandlerCollection();
    private boolean trace = false;
    private FlowLogger log = new FlowLoggerImpl();

    protected EasyFlow(StateEnum startState) {
        this.startState = startState;
        this.handlers.setHandler(HandlerCollection.EventType.ERROR, null, null, new DefaultErrorHandler());
    }

    protected void processAllTransitions(boolean skipValidation) {
        transitions = new TransitionCollection(Transition.consumeTransitions(), !skipValidation);
    }

    protected void setTransitions(Collection<Transition> collection, boolean skipValidation) {
        transitions = new TransitionCollection(collection, !skipValidation);
    }

    public void start(final C context) {
        start(false, context);
    }

    public void start(boolean enterInitialState, final C context) {
        context.setFlow(this);

        if (context.getState() == null) {
            setCurrentState(startState, false, context);
        } else if (enterInitialState) {
            setCurrentState(context.getState(), true, context);
        }
    }

    protected void setCurrentState(final StateEnum state, final boolean enterInitialState, final C context) {
      if (!enterInitialState) {
        StateEnum prevState = context.getState();
        if (prevState != null) {
          leave(prevState, context);
        }
      }

      context.setState(state);
      enter(state, context);
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEvent(EventEnum event, ContextHandler<C1> onEvent) {
        handlers.setHandler(EventType.EVENT_TRIGGER, null, event, onEvent);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEvent(EventHandler<C1> onEvent) {
        handlers.setHandler(EventType.ANY_EVENT_TRIGGER, null, null, onEvent);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEnter(StateEnum state, ContextHandler<C1> onEnter) {
        handlers.setHandler(EventType.STATE_ENTER, state, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEnter(StateHandler<C1> onEnter) {
        handlers.setHandler(EventType.ANY_STATE_ENTER, null, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenLeave(StateEnum state, ContextHandler<C1> onEnter) {
        handlers.setHandler(EventType.STATE_LEAVE, state, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenLeave(StateHandler<C1> onEnter) {
        handlers.setHandler(EventType.ANY_STATE_LEAVE, null, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenError(ExecutionErrorHandler<C1> onError) {
        handlers.setHandler(EventType.ERROR, null, null, onError);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenFinalState(StateHandler<C1> onFinalState) {
        handlers.setHandler(EventType.FINAL_STATE, null, null, onFinalState);
        return (EasyFlow<C1>) this;
    }

    public void waitForCompletion(C context) {
      context.awaitTermination();
    }

    public <C1 extends StatefulContext> EasyFlow<C1> trace() {
        trace = true;
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> logger(FlowLogger log) {
        this.log = log;
        return (EasyFlow<C1>) this;
    }

    public boolean safeTrigger(final EventEnum event, final C context) {
        try {
            return trigger(event, true, context);
        } catch (LogicViolationError logicViolationError) {
            return false;
        }
    }

    public void trigger(final EventEnum event, final C context) throws LogicViolationError {
        trigger(event, false, context);
    }

    public List<Transition> getAvailableTransitions(StateEnum stateFrom) {
        return transitions.getTransitions(stateFrom);
    }

    public boolean isEventHandledByState(final StateEnum state, final EventEnum event) {
        for (Transition transition : transitions.getTransitions(state)) {
            if (transition.getEvent() == event) return true;
        }
        return false;
    }

    private boolean trigger(final EventEnum event, final boolean safe, final C context) throws LogicViolationError {
        if (context.isTerminated()) {
            Answers.getInstance().logCustom(new CustomEvent("trigger:context.isTerminated()"));
            return false;
        }

        final StateEnum stateFrom = context.getState();
        final Transition transition = transitions.getTransition(stateFrom, event);

        if (transition != null) {
          try {
            StateEnum stateTo = transition.getStateTo();
            if (isTrace())
              log.info("when triggered %s in %s for %s <<<", event, stateFrom, context);

            handlers.callOnEventTriggered(event, stateFrom, stateTo, context);
            context.setLastEvent(event);

            if (isTrace())
              log.info("when triggered %s in %s for %s >>>", event, stateFrom, context);

            setCurrentState(stateTo, false, context);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if (!safe){
            throw new LogicViolationError("Invalid Event: " + event +
                " triggered while in State: " + context.getState() + " for " + context);
        }

        return transition != null;
    }

    private void enter(final StateEnum state, final C context) {
        if (context.isTerminated()) {
            return;
        }

        try {
            // first enter state
            if (isTrace())
                log.info("when enter %s for %s <<<", state, context);

            handlers.callOnStateEntered(state, context);

            if (isTrace())
                log.info("when enter %s for %s >>>", state, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leave(StateEnum state, final C context) {
        if (context.isTerminated()) {
            return;
        }

        try {
            if (isTrace())
                log.info("when leave %s for %s <<<", state, context);

            handlers.callOnStateLeaved(state, context);

            if (isTrace())
                log.info("when leave %s for %s >>>", state, context);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    protected boolean isTrace() {
        return trace;
    }

    protected StateEnum getStartState() {
        return startState;
    }

  // added by MCL  :]

  private Map<Event, List<ContextHandler>> eventHandlerMap = new HashMap<>();

  public void addEventHandler(Event event, ContextHandler handler) {
    List<ContextHandler> handlers = eventHandlerMap.get(event);
    if (handlers == null) {
      handlers = new ArrayList<>();
    }
    handlers.add(handler);
    eventHandlerMap.put(event, handlers);
    whenEvent(event, clump(handlers));
  }

  private Map<Pair<State, Boolean>, List<ContextHandler>> handlerMap = new HashMap<>();

  public void addPoller(State state, final Poller poller) {
    addHandler(Pair.create(state, true), poller);
    addHandler(Pair.create(state, false), new ContextHandler() {
      @Override
      public void call(StatefulContext context) throws Exception {
        poller.stop();
      }
    });
  }

  public void addEntryHandler(State state, ContextHandler handler) {
    addHandler(Pair.create(state, true), handler);
  }

  public void removeEntryHandler(State state, ContextHandler handler) {
    exchangeHandler(Pair.create(state, true), handler, false);
  }

  private void addHandler(Pair<State, Boolean> stateEntering, ContextHandler handler) {
    exchangeHandler(stateEntering, handler, true);
  }

  private void exchangeHandler(Pair<State, Boolean> stateEntering, ContextHandler handler,
                               Boolean adding) {

    List<ContextHandler> handlers = handlerMap.get(stateEntering);
    if (handlers == null) {
      handlers = new ArrayList<>();
    }

    if (adding) {
      handlers.add(handler);
    } else {
      handlers.remove(handler);
    }

    handlerMap.put(stateEntering, handlers);

    if (stateEntering.second) {
      whenEnter(stateEntering.first, clump(handlers));
    } else {
      whenLeave(stateEntering.first, clump(handlers));
    }
  }

  private ContextHandler clump(final List<ContextHandler> handlers) {
    return new ContextHandler() {
      @Override
      public void call(StatefulContext context) throws Exception {
        for (ContextHandler handler : handlers) {
          handler.call(context);
        }
      }
    };
  }
}
