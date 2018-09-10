package com.ble.statemachine;

public class StateMachineTest {
    public static void main(String[] args) {
        Machine machine = new Machine(1);
        for (int i = 0; i < 2; i++) {
            System.out.println(machine);
            machine.insertQuerter();
            machine.turnCrank();
        }
    }
}
