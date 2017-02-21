package com.spdata.em55.px.print.utils;

import android.app.Application;
import android.content.Context;

import rego.printlib.export.regoPrinter;

public class ApplicationContext extends Application {
    private regoPrinter printer;
    private int myState = 0;
    private String printName = "RG-MTP58B";
    private int alignTypetext;
    private static Context context;
    private preDefiniation.TransferMode printmode = preDefiniation.TransferMode.TM_NONE;
    private boolean labelmark = true;

    public static Context getContext() {
        return context;
    }

    public regoPrinter getObject() {
        return printer;
    }

    public int setAlignType(int n) {
        alignTypetext = n;
        return alignTypetext;

    }

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
    }

    public int getAlignType() {
        return alignTypetext;
    }

    public void setObject() {
        printer = new regoPrinter(this);
    }

    public String getName() {
        return printName;
    }

    public void setName(String name) {
        printName = name;
    }

    public void setState(int state) {
        myState = state;
    }

    public int getState() {
        return myState;
    }

    public void setPrintway(int printway) {

        switch (printway) {
            case 0:
                printmode = com.spdata.em55.px.print.utils.preDefiniation.TransferMode.TM_NONE;
                break;
            case 1:
                printmode = com.spdata.em55.px.print.utils.preDefiniation.TransferMode.TM_DT_V1;
                break;
            default:
                printmode = com.spdata.em55.px.print.utils.preDefiniation.TransferMode.TM_DT_V2;
                break;
        }

    }

    public int getPrintway() {
        return printmode.getValue();
    }

    public boolean getlabel() {
        return labelmark;
    }

    public void setlabel(boolean labelprint) {
        labelmark = labelprint;
    }

    /**
     * 将打印n行空行。n的值应该在0-255之间
     *
     * @param
     */

    public void PrintNLine(int lines) {
        // TODO Auto-generated method stub
        printer.ASCII_PrintBuffer(myState, new byte[]{0x1B, 0x66, 1,
                (byte) lines}, 4);

    }

    public void printSettings() {
        // 1D 28 41 00 00 00 02
        byte[] printdata = {0x1d, 0x28, 0x41, 0x00, 0x00, 0x00, 0x02};
        printer.ASCII_PrintBuffer(myState, printdata, printdata.length);
    }


}
