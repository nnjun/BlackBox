package top.niunaijun.blackbox.client.hook.proxies.os.storage;

import android.os.IInterface;
import android.os.Process;

import java.lang.reflect.Method;

import mirror.android.os.ServiceManager;
import mirror.android.os.mount.IMountService;
import mirror.android.os.storage.IStorageManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by Milk on 4/10/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StorageManagerStub extends BinderInvocationStub {

    public StorageManagerStub() {
        super(ServiceManager.getService.call("mount"));
    }

    @Override
    protected Object getWho() {
        IInterface mount;
        if (BuildCompat.isOreo()) {
            mount = IStorageManager.Stub.asInterface.call(ServiceManager.getService.call("mount"));
        } else {
            mount = IMountService.Stub.asInterface.call(ServiceManager.getService.call("mount"));
        }
        return mount;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("mount");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new GetVolumeList());
    }

    static class GetVolumeList extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getVolumeList";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args == null) {
                return BlackBoxCore.getBStorageManager().getVolumeList(Process.myUid(), null, 0, BClient.getUserId());
            }
            int uid = (int) args[0];
            String packageName = (String) args[1];
            int flags = (int) args[2];
            return BlackBoxCore.getBStorageManager().getVolumeList(uid, packageName, flags, BClient.getUserId());
        }
    }
}
