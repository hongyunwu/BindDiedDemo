package com.why.ipcbinddeath;
import com.why.ipcbinddeath.ICallBack;

interface IInterface {

    void registerCallBack(String UUID,in ICallBack callback);

    void unRegisterCallBack(String UUID);
}
