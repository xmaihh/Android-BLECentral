package com.ble.central.aop;

/**
 * 检查设备是否连接
 */
public @interface CheckConnect {
    // 检查蓝牙是否连接,0 代表ble 1 代表spp
    int value() default 0;
}
