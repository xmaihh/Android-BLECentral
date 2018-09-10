package com.ble.statemachine;

public class SimpleStateMachineTest {
    enum Event {
        START, STOP
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SimpleStateMachine stateMachine = new SimpleStateMachine();
        A a = new A();
        B b = new B();
        a.linkTo(b, Event.START);
        b.linkTo(a, Event.STOP);
        stateMachine.addState(a);
        stateMachine.addState(b);
        stateMachine.setInitState(a);
        stateMachine.start();

        stateMachine.postEvent(Event.START);
        stateMachine.postEvent(Event.STOP);
    }

    public static class A extends SimpleState {

        public A() {
            super("A");
        }

        public void onEnter(State fromState, Enum<?> event, Object data) {
            System.out.println("----" + this);
            System.out.println(fromState);
            System.out.println(event.ordinal());
            System.out.println(data);
        }
    }

    public static class B extends SimpleState {

        public B() {
            super("B");
        }

        public void onEnter(State fromState, Enum<?> event, Object data) {
            System.out.println("----" + this);
            System.out.println(fromState);
            System.out.println(event.ordinal());
            System.out.println(data);
        }
    }

}
