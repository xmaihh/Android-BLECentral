package cn.com.heaton.blelibrary.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import java.io.Serializable;

/**
 * Created by LiuLei on 2016/11/26.
 */

public class BleDevice implements Serializable {

    public final static String TAG = BleDevice.class.getSimpleName();
    private static final long serialVersionUID = -2576082824642358033L;

    /**
     * 连接状态
     * 2503 未连接状态
     * 2504 正在连接
     * 2505 连接成功
     */
    private int mConnectionState = BleStates.BleStatus.DISCONNECT;

    /*蓝牙地址*/
    private String mBleAddress;

    /*蓝牙名称*/
    private String mBleName;

    /*蓝牙重命名名称（别名）*/
    private String mBleAlias;

    /*是否自动连接*/
    private boolean mAutoConnect = true;//The default is not automatic connection

    /*通知的特征*/
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    /**
     * Use the address and name of the BluetoothDevice object
     * to construct the address and name of the {@code BleDevice} object
     *
     * @param device BleDevice
     */
    protected BleDevice(BluetoothDevice device) {
        this.mBleAddress = device.getAddress();
        this.mBleName = device.getName();
    }

    public boolean isConnected() {
        return mConnectionState == BleStates.BleStatus.CONNECTED;
    }

    public boolean isConnectting() {
        return mConnectionState == BleStates.BleStatus.CONNECTING;
    }

    public boolean isAutoConnect() {
        return mAutoConnect;
    }

    public void setAutoConnect(boolean mAutoConnect) {
        this.mAutoConnect = mAutoConnect;
    }

    public int getConnectionState() {
        return mConnectionState;
    }

    public void setConnectionState(@BleStates.BleStatus int state) {
        mConnectionState = state;
    }


    public String getBleAddress() {
        return mBleAddress;
    }

    public void setBleAddress(String mBleAddress) {
        this.mBleAddress = mBleAddress;
    }

    public String getBleName() {
        return mBleName;
    }

    public void setBleName(String mBleName) {
        this.mBleName = mBleName;
    }

    public String getBleAlias() {
        return mBleAlias;
    }

    public void setBleAlias(String mBleAlias) {
        this.mBleAlias = mBleAlias;
    }

    public BluetoothGattCharacteristic getNotifyCharacteristic() {
        return mNotifyCharacteristic;
    }

    public void setNotifyCharacteristic(BluetoothGattCharacteristic notifyCharacteristic) {
        this.mNotifyCharacteristic = notifyCharacteristic;
    }

    @Override
    public String toString() {
        return "BleDevice{" +
                "mConnectionState=" + mConnectionState +
                ", mBleAddress='" + mBleAddress + '\'' +
                ", mBleName='" + mBleName + '\'' +
                ", mBleAlias='" + mBleAlias + '\'' +
                ", mAutoConnect=" + mAutoConnect +
                ", mNotifyCharacteristic=" + mNotifyCharacteristic +
                '}';
    }
}
