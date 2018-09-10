package com.ble.statemachine;

/**
 * 机器类,包含多种状态,处理流程
 */
public class Machine {

    //机器本身包含所有的状态机
    private State soldOutState;
    private State noQuarterState;
    private State hasQuarterState;
    private State soldState;

    private State state; // 机器的当前状态
    private int count = 0; // 机器中当前糖果的数量

    public Machine(int count) {
        this.soldOutState = new SoldOutState(this);
        this.noQuarterState = new NoQuarterState(this);
        this.hasQuarterState = new HasQuarterState(this);
        this.soldState = new SoldOutState(this);
        this.count = count;
        if (this.count > 0) {
            this.state = noQuarterState;
        }
    }

    /**
     * 释放糖果时的内部处理程序
     */

    public void releaseBall() {
        System.out.println("a gumball comes rolling out the solt ...");
        if (count > 0) {
            count -= 1;
        }
    }

    public void insertQuerter() {
        state.insertQuarter();  // 投入硬币
    }

    public void ejectQuarter() {
        state.ejectQuarter();
    }

    public void turnCrank() {
        state.turnCrank();
        state.dispense();
    }

    public State getSoldOutState() {
        return soldOutState;
    }

    public State getNoQuarterState() {
        return noQuarterState;
    }

    public State getHasQuarterState() {
        return hasQuarterState;
    }

    public State getState() {
        return state;
    }

    public State getSoldState() {
        return soldState;
    }

    public int getCount() {
        return count;
    }

    public void setState(State state) {
        this.state = state;
    }
}
