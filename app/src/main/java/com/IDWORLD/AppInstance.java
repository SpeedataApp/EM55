package com.IDWORLD;



public class AppInstance {

    private static AppInstance mInstance;

    private HostUsb mHostUsb;

    public static AppInstance getInstance() {
        if (mInstance ==null)
            mInstance = new AppInstance();
        return mInstance;
    }

    public HostUsb getmHostUsb() {
        return mHostUsb;
    }

    public void setmHostUsb(HostUsb mHostUsb) {
        this.mHostUsb = mHostUsb;
    }
}
