package com.ble.statemachine;

/**
 * 状态机接口
 */
public interface State {

    /**
     * 投入硬币
     */
    void insertQuarter();

    /**
     * 根据摇动情况,处理摇动结果,返回处理结果,释放糖果
     */
    void ejectQuarter();

    /**
     * 转动摇柄
     */
    void turnCrank();

    /**
     * 机器释放糖果,处理机器内部状态,返回初始可投币状态
     */
    void dispense();
}
