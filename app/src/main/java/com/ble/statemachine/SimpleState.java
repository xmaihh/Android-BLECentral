package com.ble.statemachine;

import java.util.HashMap;

public abstract class SimpleState {

        HashMap<Enum<?>, SimpleState> mToStates = new HashMap<>();
        private SimpleStateMachine mStateMachine;

        @SuppressWarnings("unused")
        private String mName = "UNKNOWN";

        public SimpleState(String name) {
            mName = name;
        }

        public void linkTo(SimpleState toState, Enum<?> event) {
            if (toState == null) {
                throw new IllegalArgumentException("toState cannot be null");
            }
            mToStates.put(event, toState);
        }

        public void onStart() {
        }

        public void onStop(int cause) {
        }

        public void onReset(int cause) {
        }

        public void onUnhandleEvent(Enum<?> event, Object data) {
        }

        public void onEnter(SimpleState fromState, Enum<?> event, Object data) {
        }

        public void onLeave(SimpleState toState, Enum<?> event, Object data) {
        }

        protected SimpleStateMachine getStateMachine() {
            return mStateMachine;
        }

        protected void setStateMachine(SimpleStateMachine stateMachine) {
            mStateMachine = stateMachine;
        }
    }
