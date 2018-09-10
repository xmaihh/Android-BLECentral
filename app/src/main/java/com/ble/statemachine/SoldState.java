package com.ble.statemachine;

public class SoldState implements State {
    private Machine machine;

    public SoldState(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void insertQuarter() {
        System.out.println("please wait,we are already giving you a gumball!");
    }

    @Override
    public void ejectQuarter() {
        System.out.println("Sorry,you have turned the crank!");
    }

    @Override
    public void turnCrank() {
        System.out.println("Turning twice dose not get you another gumball!");
    }

    @Override
    public void dispense() {
        machine.releaseBall();
        if (machine.getCount() > 0) {
            machine.setState(machine.getNoQuarterState());
        } else {
            System.out.println("Out of Gumball!");
            machine.setState(machine.getSoldOutState());
        }
    }
}
