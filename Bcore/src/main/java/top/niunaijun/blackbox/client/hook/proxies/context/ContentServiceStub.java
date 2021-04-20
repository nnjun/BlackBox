package top.niunaijun.blackbox.client.hook.proxies.context;

import java.lang.reflect.Method;

import mirror.android.content.IContentService;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;

/**
 * Created by Milk on 4/6/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ContentServiceStub extends BinderInvocationStub {

    public ContentServiceStub() {
        super(ServiceManager.getService.call("content"));
    }

    @Override
    protected Object getWho() {
        return IContentService.Stub.asInterface.call(ServiceManager.getService.call("content"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("content");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new RegisterContentObserver());
        addMethodHook(new NotifyChange());
    }

    public static class RegisterContentObserver extends MethodHook {

        @Override
        protected String getMethodName() {
            return "registerContentObserver";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    static class NotifyChange extends MethodHook {

        @Override
        protected String getMethodName() {
            return "notifyChange";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
