package com.ble.statemachine;

import android.os.Handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SimpleStateMachine {
    Set<SimpleState> mStates = new HashSet<>();
    private SimpleState mInitState;
    private SimpleState mCurrentState;
    private Object mHandleLock = new Object();

    Handler mHandler;

    public SimpleStateMachine() {
        mHandler = new Handler();
    }

    public SimpleStateMachine(Handler handler) {
        mHandler = handler;
    }

    public void setInitState(SimpleState initState) {
        mInitState = initState;
    }

    public void addState(SimpleState state) {
        synchronized (this) {
            mStates.add(state);
            state.setStateMachine(this);
        }
    }

    public void start() {
        synchronized (this) {
            for (SimpleState state : mStates) {
                state.onStart();
            }
            mCurrentState = mInitState;
        }
    }

    public void stop(int cause) {
        synchronized (this) {
            for (SimpleState state : mStates) {
                state.onStop(cause);
            }
        }
    }

    public void reset(int cause) {
        synchronized (this) {
            for (SimpleState state : mStates) {
                state.onReset(cause);
            }
            mCurrentState = mInitState;
        }
    }

    public void postEvent(Enum<?> event) {
        postEvent(event, null);
    }

    public void postEvent(final Enum<?> event, final Object data) {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mHandleLock) {
                    SimpleState nextState = mCurrentState.mToStates.get(event);
                    if (nextState == null) {
                        mCurrentState.onUnhandleEvent(event, data);
                        return;
                    }
                    mCurrentState.onLeave(nextState, event, data);
                    nextState.onEnter(mCurrentState, event, data);
                    mCurrentState = nextState;
                }
            }
        });
    }

    public boolean canMoveTo(SimpleState toState) {
        if (toState == null) {
            return false;
        }
        synchronized (this) {
            HashMap<Enum<?>, SimpleState> states = mCurrentState.mToStates;
            for (Enum<?> event : states.keySet()) {
                if (states.get(event).equals(toState)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean canAccept(Enum<?> event) {
        synchronized (this) {
            return mCurrentState.mToStates.containsKey(event);
        }
    }

    public SimpleState getCurrentState() {
        return mCurrentState;
    }
}
