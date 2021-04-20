package top.niunaijun.blackbox.client.hook.proxies.net;

import android.content.Context;

import mirror.android.net.IConnectivityManager;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;

/**
 * Created by Milk on 4/12/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ConnectivityManagerStub extends BinderInvocationStub {
    public ConnectivityManagerStub() {
        super(ServiceManager.getService.call(Context.CONNECTIVITY_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IConnectivityManager.Stub.asInterface.call(ServiceManager.getService.call(Context.CONNECTIVITY_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();

    }
}
