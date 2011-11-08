package com.fanfou.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.http.ApnType;
import com.fanfou.app.ui.ActionBar.OnRefreshClickListener;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.30
 * @version 2.0 2011.09.25
 * @version 2.1 2011.10.19
 * @version 2.1 2011.10.25
 * @version 2.2 2011.10.27
 * @version 2.3 2011.11.07
 * 
 */
public abstract class BaseActivity extends Activity implements
		OnRefreshClickListener, OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected static final int REQUEST_CODE_OPTION = 100;

	protected BaseActivity mContext;
	protected LayoutInflater mInflater;
	protected boolean isActive = false;

	protected DisplayMetrics mDisplayMetrics;

	private BroadcastReceiver mBroadcastReceiver;
	private IntentFilter mIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);

		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);

		initialize();
		initReceiver();
	}

	private void initialize() {
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	}

	private void initReceiver() {
		this.mBroadcastReceiver = new MyBroadcastReceiver();
		this.mIntentFilter = getIntentFilter();
		mIntentFilter.setPriority(1000);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (App.DEBUG) {
				Log.d("NotificationReceiver", "active, broadcast received: "
						+ intent.toString());
			}
			if (onBroadcastReceived(intent)) {
				abortBroadcast();
			}
		}

	}

	protected IntentFilter getIntentFilter() {
		return new IntentFilter();
	}

	protected boolean onBroadcastReceived(Intent intent) {
		return true;
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// public void doLogout(){
	// if(isTaskRoot()){
	// IntentHelper.goLoginPage(this);
	// }else{
	// setResult(RESULT_LOGOUT);
	// }
	// finish();
	// }

	@Override
	protected void onResume() {
		super.onResume();
		App.active = isActive = true;
		registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	protected void onPause() {
		App.active = isActive = false;
		unregisterReceiver(mBroadcastReceiver);
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && Build.VERSION.SDK_INT < 7
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	protected boolean isActive() {
		return isActive;
	}

	protected static final int PAGE_NORMAL = 0;
	protected static final int PAGE_HOME = 1;
	protected static final int PAGE_LOGIN = 2;
	protected static final int PAGE_STATUS = 3;
	protected static final int PAGE_USER = 4;
	protected static final int PAGE_TIMELINE = 5;
	protected static final int PAGE_FRIENDS = 6;
	protected static final int PAGE_FOLLOWERS = 7;
	protected static final int PAGE_DRAFTS = 8;

	protected int getPageType() {
		return PAGE_NORMAL;
	}

	protected boolean isHomeScreen() {
		return false;
	}

	protected boolean noConnection() {
		return App.me.apnType == ApnType.NONE;
	}

	protected static final int MENU_ID_PROFILE = 0; //
	protected static final int MENU_ID_OPTION = 1; // 设置
	protected static final int MENU_ID_SEARCH = 2;
	protected static final int MENU_ID_ABOUT = 3; // 关于
	protected static final int MENU_ID_FEEDBACK = 4; //
	protected static final int MENU_ID_LOGOUT = 5; // 退出
	protected static final int MENU_ID_HOME = 6; // 返回首页

	protected static final int MENU_ID_REFRESH = 7; // 返回首页
	protected static final int MENU_ID_CLEAR = 8; // 返回首页

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_ID_OPTION:
			onOptionClick();
			break;
		case MENU_ID_PROFILE:
			onProfileClick();
			break;
		case MENU_ID_SEARCH:
			onSearchClick();
			break;
		case MENU_ID_LOGOUT:
			onLogoutClick();
			break;
		case MENU_ID_ABOUT:
			// startActivity(new Intent(this, NewVersionPage.class));
			onAboutClick();
			break;
		case MENU_ID_FEEDBACK:
			onFeedbackClick();
			break;
		case MENU_ID_HOME:
			onHomeClick();
			break;
		case MENU_ID_CLEAR:
			onClearClick();
			break;
		default:
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int type = getPageType();
		switch (type) {
		case PAGE_NORMAL:
			menu.removeItem(MENU_ID_OPTION);
			menu.removeItem(MENU_ID_PROFILE);
			menu.removeItem(MENU_ID_SEARCH);
			menu.removeItem(MENU_ID_LOGOUT);
			menu.removeItem(MENU_ID_ABOUT);
			menu.removeItem(MENU_ID_FEEDBACK);
			menu.removeItem(MENU_ID_CLEAR);
			break;
		case PAGE_HOME:
			menu.removeItem(MENU_ID_HOME);
			menu.removeItem(MENU_ID_CLEAR);
			break;
		case PAGE_LOGIN:
			menu.clear();
			break;
		case PAGE_DRAFTS:
			menu.removeItem(MENU_ID_OPTION);
			menu.removeItem(MENU_ID_PROFILE);
			menu.removeItem(MENU_ID_SEARCH);
			menu.removeItem(MENU_ID_LOGOUT);
			menu.removeItem(MENU_ID_ABOUT);
			menu.removeItem(MENU_ID_FEEDBACK);
			menu.removeItem(MENU_ID_HOME);
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem option = menu.add(0, MENU_ID_OPTION, MENU_ID_OPTION, "功能设置");
		option.setIcon(R.drawable.i_menu_option);

		MenuItem profile = menu
				.add(0, MENU_ID_PROFILE, MENU_ID_PROFILE, "我的空间");
		profile.setIcon(R.drawable.i_menu_profile);

		MenuItem search = menu.add(0, MENU_ID_SEARCH, MENU_ID_SEARCH, "热词搜索");
		search.setIcon(R.drawable.i_menu_search);

		MenuItem logout = menu.add(0, MENU_ID_LOGOUT, MENU_ID_LOGOUT, "注销登录");
		logout.setIcon(R.drawable.i_menu_logout);

		MenuItem about = menu.add(0, MENU_ID_ABOUT, MENU_ID_ABOUT, "关于饭否");
		about.setIcon(R.drawable.i_menu_about);

		MenuItem feedback = menu.add(0, MENU_ID_FEEDBACK, MENU_ID_FEEDBACK,
				"意见反馈");
		feedback.setIcon(R.drawable.i_menu_feedback);

		MenuItem home = menu.add(0, MENU_ID_HOME, MENU_ID_HOME, "返回首页");
		home.setIcon(R.drawable.i_menu_home);

		MenuItem clear = menu.add(0, MENU_ID_CLEAR, MENU_ID_CLEAR, "清空草稿");
		clear.setIcon(R.drawable.i_menu_clear);
		return true;
	}

	@Override
	public void onRefreshClick() {
	}

	@Override
	public void onClick(View v) {
	}

	protected void onOptionClick() {
		Intent intent = new Intent(this, OptionsPage.class);
		startActivity(intent);
	}

	protected void onProfileClick() {
		ActionManager.doMyProfile(this);
	}

	protected void onSearchClick() {
		Intent intent = new Intent(this, SearchPage.class);
		startActivity(intent);
	}

	protected void onAboutClick() {
		Utils.goAboutPage(this);
	}

	protected void onFeedbackClick() {
		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		intent.putExtra(Commons.EXTRA_TEXT,
				getString(R.string.config_feedback_account) + " ("
						+ Build.MODEL + "-" + Build.VERSION.RELEASE + ") ");
		startActivity(intent);
	}

	protected void onHomeClick() {
		IntentHelper.goHomePage(this, -1);
		finish();
	}

	protected void onClearClick() {
	};

	protected void onLogoutClick() {
		final ConfirmDialog dialog = new ConfirmDialog(this, "注销",
				"确定注销当前登录帐号吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

			@Override
			public void onButton1Click() {
				IntentHelper.goLoginPage(mContext);
				finish();
			}
		});
		dialog.show();
	}

	protected void startRefreshAnimation() {
	}

	protected void stopRefreshAnimation() {
	}

}
