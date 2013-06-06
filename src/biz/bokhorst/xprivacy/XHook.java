package biz.bokhorst.xprivacy;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Binder;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public abstract class XHook {

	private String mMethodName;
	private String mRestrictionName;

	public XHook(String methodName, String restrictionName, String[] permissions) {
		mMethodName = methodName;
		mRestrictionName = restrictionName;
		XRestriction.registerMethod(methodName, restrictionName, permissions);
	}

	public String getMethodName() {
		return mMethodName;
	}

	public String getRestrictionName() {
		return mRestrictionName;
	}

	abstract protected void before(MethodHookParam param) throws Throwable;

	abstract protected void after(MethodHookParam param) throws Throwable;

	protected boolean isRestricted(MethodHookParam param) throws Throwable {
		Context context = getApplicationContext();
		int uid = Binder.getCallingUid();
		return getRestricted(context, uid, true);
	}

	protected Context getApplicationContext() {
		return AndroidAppHelper.currentApplication();
	}

	protected boolean getRestricted(Context context, int uid, boolean usage) {
		return XRestriction.getRestricted(this, context, uid, mRestrictionName, usage);
	}

	protected void setRestricted(Context context, int uid, boolean restricted) {
		XRestriction.setRestricted(this, context, uid, mRestrictionName, restricted);
	}

	protected void notifyUser() throws Throwable {
		notifyUser(getApplicationContext());
	}

	protected void notifyUser(Context context) throws Throwable {
		String format = XUtil.getXString(context, R.string.msg_restricted);
		String text = String.format(format, this.getClass().getSimpleName(), getMethodName());
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}

	protected void info(String message) {
		XUtil.log(this, Log.INFO, message);
	}

	protected void warning(String message) {
		XUtil.log(this, Log.WARN, message);
	}

	protected void error(String message) {
		XUtil.log(this, Log.ERROR, message);
	}
}
