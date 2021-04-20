package top.niunaijun.blackbox.client.hook;

import java.lang.reflect.Method;

/**
 * @author Lody
 */

public class ResultStaticMethodProxy extends MethodHook {

	Object mResult;
	String mName;

	public ResultStaticMethodProxy(String name, Object result) {
		mResult = result;
		mName = name;
	}

	public Object getResult() {
		return mResult;
	}

	@Override
	protected String getMethodName() {
		return mName;
	}

	@Override
	protected Object hook(Object who, Method method, Object[] args) throws Throwable {
		return mResult;
	}
}
