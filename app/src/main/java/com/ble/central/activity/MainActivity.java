package com.ble.central.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ble.central.R;
import com.ble.central.adapter.LeDeviceListAdapter;
import com.ble.central.aop.CheckConnect;
import com.ble.central.annotation.ContentView;
import com.ble.central.annotation.OnClick;
import com.ble.central.annotation.OnItemClick;
import com.ble.central.aop.SingleClick;
import com.ble.central.annotation.ViewInit;
import com.ble.central.command.Command;
import com.ble.central.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.L;
import cn.com.heaton.blelibrary.ble.callback.BleConnCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleReadCallback;
import cn.com.heaton.blelibrary.ble.callback.BleReadRssiCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteEntityCallback;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private static final String TAG = "BleCentral";
    @ViewInit(R.id.listView)
    ListView mListView;
    @ViewInit(R.id.connected_num)
    TextView mConnectedNum;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Ble<BleDevice> mBle;

    @SingleClick
    @CheckConnect
    @OnClick({R.id.readRssi, R.id.sendData, R.id.readData, R.id.requestMtu, R.id.sendEntityData})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendData:  // 发送数据
                List<BleDevice> list = Ble.getInstance().getConnetedDevices();
                synchronized (mBle.getLocker()) {
                    for (BleDevice device : list) {
                        sendData(device);
                    }
                }
                break;
            case R.id.readData: // 读取数据
                List<BleDevice> l = Ble.getInstance().getConnetedDevices();
                synchronized (mBle.getLocker()) {
                    for (BleDevice device : l) {
                        read(device);
                    }
                }
                break;
            case R.id.readRssi:  // 读取Rssi
                mBle.readRssi(mBle.getConnetedDevices().get(0), new BleReadRssiCallback<BleDevice>() {
                    @Override
                    public void onReadRssiSuccess(int rssi) {
                        super.onReadRssiSuccess(rssi);
                        ToastUtil.show("读取远程Rssi成功：" + rssi);
                    }
                });
                break;
            case R.id.requestMtu://请求MTU
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //此处第二个参数  不是特定的   比如你也可以设置500   但是如果设备不支持500个字节则会返回最大支持数
                    mBle.setMTU(mBle.getConnetedDevices().get(0).getBleAddress(), 96, new BleMtuCallback<BleDevice>() {
                        @Override
                        public void onMtuChanged(BleDevice device, int mtu, int status) {
                            super.onMtuChanged(device, mtu, status);
                            ToastUtil.show("最大支持MTU：" + mtu);
                        }
                    });
                } else {
                    ToastUtil.show("设备不支持MTU");
                }
                break;
            case R.id.sendEntityData://发送大数据
                try {
                    byte[] data = toByteArray(getAssets().open("WhiteChristmas.bin"));  // 读流
                    //发送大数据量的包
                    mBle.writeEntity(mBle.getConnetedDevices().get(0), data, 20, 50, new BleWriteEntityCallback<BleDevice>() {
                        @Override
                        public void onWriteSuccess() {
                            L.e("writeEntity", "onWriteSuccess");
                        }

                        @Override
                        public void onWriteFailed() {
                            L.e("writeEntity", "onWriteFailed");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @OnItemClick(R.id.listView)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BleDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        if (mBle.isScanning()) {
            mBle.stopScan();
        }
        if (device.isConnected()) {
            mBle.disconnect(device);
        } else if (!device.isConnectting()) {
            //扫描到设备时   务必用该方式连接(是上层逻辑问题， 否则点击列表  虽然能够连接上，但设备列表的状态不会发生改变)
            mBle.connect(device, connectCallback);
            //此方式只是针对不进行扫描连接（如上，若通过该方式进行扫描列表的连接  列表状态不会发生改变）
//            mBle.connect(device.getBleAddress(), connectCallback);
        }
    }

    @Override
    protected void onInitView() {
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        mListView.setAdapter(mLeDeviceListAdapter);
        //1、请求蓝牙相关权限
        requestPermission(new String[]{Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                "请求蓝牙相关权限", new GrantedResult() {
                    @Override
                    public void onResult(boolean granted) {
                        if (granted) {
                            //2、初始化蓝牙
                            initBle();
                        } else {
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void initLinsenter() {

    }

    //初始化蓝牙
    private void initBle() {
        mBle = Ble.getInstance();
        Ble.Options options = new Ble.Options();
        options.logBleExceptions = true;//设置是否输出打印蓝牙日志
        options.throwBleException = true;//设置是否抛出蓝牙异常
        options.autoConnect = false;//设置是否自动连接
        options.scanPeriod = 12 * 1000;//设置扫描时长
        options.connectTimeout = 10 * 1000;//设置连接超时时长
        options.uuid_service = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");//设置主服务的uuid
        //options.uuid_services_extra = new UUID[]{UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")};//添加额外的服务（如电量服务，心跳服务等）
        options.uuid_write_cha = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");//设置可写特征的uuid
        //options.uuid_read_cha = UUID.fromString("d44bc439-abfd-45a2-b575-925416129601");//设置可读特征的uuid
        mBle.init(getApplicationContext(), options);
        //3、检查蓝牙是否支持及打开
        checkBluetoothStatus();
    }

    //检查蓝牙是否支持及打开
    private void checkBluetoothStatus() {
        // 检查设备是否支持BLE4.0
        if (!mBle.isSupportBle(this)) {
            ToastUtil.show("设备不支持低功耗蓝牙(BLE)");
            finish();
        }
        if (!mBle.isBleEnable()) {
            //4、若未打开，则请求打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
        } else {
            //5、若已打开，则进行扫描
            mBle.startScan(scanCallback);
        }
    }

    // 扫描的回调
    BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
            synchronized (mBle.getLocker()) {
                mLeDeviceListAdapter.addDevice(device);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop: ");
        }
    };

    /*连接的回调*/
    private BleConnCallback<BleDevice> connectCallback = new BleConnCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(final BleDevice device) {
            if (device.isConnected()) {
                /*连接成功后，设置通知*/
                mBle.startNotify(device, bleNotiftCallback);
            }
            Log.e(TAG, "onConnectionChanged: " + device.isConnected());
            mLeDeviceListAdapter.notifyDataSetChanged();
            setConnectedNum();
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            ToastUtil.show("连接异常，异常状态码:" + errorCode);
        }
    };

    /*设置通知的回调*/
    private BleNotiftCallback<BleDevice> bleNotiftCallback = new BleNotiftCallback<BleDevice>() {
        @Override
        public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.e(TAG, "onChanged==uuid:" + uuid.toString());
            Log.e(TAG, "onChanged==address:" + device.getBleAddress());
            Log.e(TAG, "onChanged==data:" + Arrays.toString(characteristic.getValue()));
        }
    };

    /*更新当前连接设备的数量*/
    private void setConnectedNum() {
        if (mBle != null) {
            Log.e("mConnectedNum", "已连接的数量：" + mBle.getConnetedDevices().size() + "");
            for (BleDevice device : mBle.getConnetedDevices()) {
                Log.e("device", "设备地址：" + device.getBleAddress());
            }
            mConnectedNum.setText("连接数量: " + mBle.getConnetedDevices().size());
        }
    }

    /*主动读取数据*/
    public void read(BleDevice device) {
        boolean result = mBle.read(device, new BleReadCallback<BleDevice>() {
            @Override
            public void onReadSuccess(BluetoothGattCharacteristic characteristic) {
                super.onReadSuccess(characteristic);
                byte[] data = characteristic.getValue();
                ToastUtil.show("onReadSuccess: " + Arrays.toString(data));
            }
        });
        if (!result) {
            ToastUtil.show("读取数据失败!");
        }
    }

    //播放音乐
    public byte[] changeLevelInner(int play) {
        byte[] data = new byte[Command.qppDataSend.length];
        System.arraycopy(Command.qppDataSend, 0, data, 0, data.length);
        data[6] = 0x03;
        data[7] = (byte) play;
        ToastUtil.show("data:" + Arrays.toString(data));
        return data;
    }

    /*发送数据*/
    public void sendData(BleDevice device) {
        boolean result = mBle.write(device, changeLevelInner(1),
                new BleWriteCallback<BleDevice>() {
                    @Override
                    public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                        ToastUtil.show("发送数据成功");
                    }
                });
        if (!result) {
            ToastUtil.show("发送数据失败!");
        }
    }

    //inputstream转byte[]
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == Ble.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else if (requestCode == Ble.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //6、若打开，则进行扫描
            mBle.startScan(scanCallback);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBle != null) {
            mBle.destory(getApplicationContext());
        }
    }
}
