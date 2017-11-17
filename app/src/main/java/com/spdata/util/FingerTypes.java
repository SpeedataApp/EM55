package com.spdata.util;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by suntianwei on 2017/3/28.
 */

public class FingerTypes {
    public static final int BVID = 0x0483;
    public static final int BPID = 0x5710;
    public static final int MVID = 0x2109;
    public static final int MPID = 0x7638;

    public static int getrwusbdevices(Context context) {
        // get FileDescriptor by Android USB Host API
        UsbManager mUsbManager = (UsbManager) context
                .getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("1111111",
                    device.getDeviceName() + " "
                            + Integer.toHexString(device.getVendorId()) + " "
                            + Integer.toHexString(device.getProductId()));
            device.getVendorId();
            device.getProductId();
            for (UsbDevice tdevice : deviceList.values()) {
                if ((device.getVendorId() == BVID) && (BPID == device.getProductId())) {
                    //返回公安部不指纹模块
                    return 1;
                } else if ((device.getVendorId() == MVID) && (MPID == device.getProductId())) {
                    //返回民用指纹模块
                    return 2;
                } else {
                    //返回金色指纹（tcs1G海外版）
                    return 3;
                }
            }
        }
        return 0;
    }

}
