package com.ble.statemachine;

public class SoldOutState implements State {
    private Machine machine;

    public SoldOutState(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void insertQuarter() {
        System.out.println("Sorry,there is no gumball in the machine!");
    }

    @Override
    public void ejectQuarter() {
        System.out.println("Sorry,there is no gumball in the machine!");
    }

    @Override
    public void turnCrank() {
        System.out.println("Sorry,there is no gumball in the machine!");
        machine.setState(machine.getNoQuarterState());
    }

    @Override
    public void dispense() {
        System.out.println("Sorry,there is no gumball in the machine!");
    }
}
