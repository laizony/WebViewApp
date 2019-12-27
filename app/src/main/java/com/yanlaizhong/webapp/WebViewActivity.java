package com.yanlaizhong.webapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class WebViewActivity extends Activity {
	private static final String LOG_TAG = "ylz_web";
	private static final int REQUEST_CONTACT = 123;
//	private ProgressBar progressbar;
	private WebView mWebView;
	private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

        //设置状态栏颜色
        // 经测试在代码里直接声明透明状态栏更有效
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//若执行上面>4.4条件的代码，下面设置状态栏颜色会失效
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//状态栏字体改为黑色
            getWindow().setStatusBarColor(Color.WHITE);
        }
//		setSupportActionBar(toolbar);
//		getActionBar().show();
//		getActionBar().setTitle("");
//		try{
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }catch (Exception e){
//
//        }
		// progressbar = new ProgressBar(this, null,
		// android.R.attr.progressBarStyleHorizontal);
//		progressbar = (ProgressBar) findViewById(R.id.pg_webview);
		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();
//		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setDomStorageEnabled(true);//DOM storage API
        webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
//        mWebView.loadUrl("file:///android_asset/.........");
//        mWebView.loadUrl("http://www.baidu.com");
        mWebView.loadUrl("http://www.zhihu.com/");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONTACT:
			break;
		default:
			break;
		}
	}

//    @Override
//    public boolean onSupportNavigateUp() {
//        finish();
//        return super.onSupportNavigateUp();
//    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.webview, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
////		case android.R.id.home:// 返回按钮
////			Intent upIntent = NavUtils.getParentActivityIntent(this);
////			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
////				TaskStackBuilder.create(this)
////						.addNextIntentWithParentStack(upIntent)
////						.startActivities();
////			} else {
////				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////				NavUtils.navigateUpTo(this, upIntent);
////			}
////			return true;
//		case R.id.action_refresh:// 刷新
//			mWebView.reload();
//			return true;
//		case R.id.action_browse:// 在浏览器中打开
//			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//			startActivity(intent);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();// 返回前一个页面
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 对网页中超链接按钮的响应。当按下某个连接时WebViewClient会调用这个方法，并传递参数：按下的url
			// 用webview打开
            Log.i(LOG_TAG,url);
			view.loadUrl(url);
			return true;

			// 用浏览器打开
			// Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			// startActivity(intent);
			// return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// 页面加载完成时进行的操作
			// 这里写需要在进入页面就执行的代码，避免网页元素没有加载完成导致错误
            view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
			super.onPageFinished(view, url);
		}
	}


	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			Log.i(LOG_TAG, "JsAlert-" + message);
			result.confirm();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			result.cancel();
			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
//			if (newProgress == 100) {
//				progressbar.setVisibility(View.GONE);
//			} else {
//				if (progressbar.getVisibility() == View.GONE)
//					progressbar.setVisibility(View.VISIBLE);
//				progressbar.setProgress(newProgress);
//			}
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			Log.i(LOG_TAG, "TITLE=" + title);
//			try{
//                getSupportActionBar().setTitle(title);
//			}catch (NullPointerException e){
//
//			}
		}
	}
}