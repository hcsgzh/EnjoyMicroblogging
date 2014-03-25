package com.weibo.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.net.WeiBoHelper;



public class WeiboDialog extends Dialog {

    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private final Weibo mWeibo;
    private String mUrl;
    private WeiboDialogListener mListener;
//    private ProgressDialog mSpinner;
    private ImageView mBtnClose;
    private WebView mWebView;
    private RelativeLayout webViewContainer;
    private RelativeLayout mContent;
    private Context mContext;
    private ProgressBar progressBar;
    private final static String TAG = "Weibo-WebView";

    public WeiboDialog(final Weibo weibo, Context context, String url, WeiboDialogListener listener) {
        super(context,R.style.dialog);
        mWeibo = weibo;
        mUrl = url;
        mListener = listener;
        mContext = context;
        
//        mSpinner = new ProgressDialog(getContext());
//        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mSpinner.setMessage("Loading...");
//        setOrientation(LinearLayout.VERTICAL);
//        mContent = new RelativeLayout(context);
//        progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
//        
//        setUpWebView();
//        
//        addView(progressBar, new LayoutParams(LayoutParams.FILL_PARENT,5));
//        addView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,
//                LayoutParams.FILL_PARENT));
        
    }

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContent = new RelativeLayout(mContext);
        progressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);
        
        setUpWebView();
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(progressBar, new LayoutParams(LayoutParams.FILL_PARENT,5));
        layout.addView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        setContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
	}


	private void setUpWebView() {
		
//		CookieSyncManager.createInstance(mContext);   
//        CookieSyncManager.getInstance().startSync();   
//        CookieManager.getInstance().removeSessionCookie();  
		
        webViewContainer = new RelativeLayout(getContext());

        Log.i("setUpWebView", "setUpWebView");
        mWebView = new WebView(mContext);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WeiboDialog.WeiboWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.requestFocus();
        
//        WebSettings settings = mWebView.getSettings();
//        settings.setSaveFormData(false);
//        settings.setSavePassword(false);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webViewContainer.addView(mWebView);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        mContent.addView(webViewContainer, lp);
    }

    private void setUpCloseBtn() {
        mBtnClose = new ImageView(getContext());
        mBtnClose.setClickable(true);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel();
//                WeiboDialog.this.dismiss();
            }
        });

        mBtnClose.setImageResource(R.drawable.close_selector);
        mBtnClose.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams closeBtnRL = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        closeBtnRL.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeBtnRL.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        closeBtnRL.topMargin = getContext().getResources().getDimensionPixelSize(
//                R.dimen.dialog_btn_close_right_margin);
//        closeBtnRL.rightMargin = getContext().getResources().getDimensionPixelSize(
//                R.dimen.dialog_btn_close_top_margin);

        webViewContainer.addView(mBtnClose, closeBtnRL);
    }

    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirect URL: " + url);
            // 瀵板懎鎮楅崣鏉款杻閸旂姴顕妯款吇闁插秴鐣鹃崥鎴濇勾閸э拷娈戦弨顖涘瘮閸氬簼鎱ㄩ弨閫涚瑓闂堛垻娈戦柅鏄忕帆
            if (url.startsWith(mWeibo.getRedirectUrl())) {
                handleRedirectUrl(view, url);
//                WeiboDialog.this.dismiss();
                return true;
            }
            // launch non-dialog URLs in a full browser
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(new DialogError(description, errorCode, failingUrl));
//            WeiboDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted URL: " + url);
            // google issue. shouldOverrideUrlLoading not executed
            super.onPageStarted(view, url, favicon);
            if (url.startsWith(mWeibo.getRedirectUrl())) {
                handleRedirectUrl(view, url);
                view.stopLoading();
                dismiss();
                return;
            }
            
//            mSpinner.show();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished URL: " + url);
            super.onPageFinished(view, url);
//            mSpinner.dismiss();

            mContent.setBackgroundColor(Color.TRANSPARENT);
//            webViewContainer.setBackgroundResource(R.drawable.dialog_bg);
            // mBtnClose.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.requestFocus();
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

    }
    
    private class MyWebChromeClient extends WebChromeClient
    {
    	
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			progressBar.setProgress(newProgress);
			if (newProgress==100) {
				progressBar.setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}
    }

    private void handleRedirectUrl(WebView view, String url) {
        Bundle values = Utility.parseUrl(url);
        String error = values.getString("error");
        String error_code = values.getString("error_code");

        if (error == null && error_code == null) {
//            mListener.onComplete(values);
        	try {
				getToken(values);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (error.equals("access_denied")) {
            // 閻劍鍩涢幋鏍ㄥ房閺夊啯婀囬崝鈥虫珤閹锋帞绮烽幒鍫滅埃閺佺増宓佺拋鍧楁６閺夊啴妾�
            mListener.onCancel();
            dismiss();
        } else {
            mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
        }
    }
    
    private void getToken(Bundle values) throws ClientProtocolException, IOException, WeiboException, JSONException
    {
    	String code = values.getString("code");
    	HttpClient client = Utility.getNewHttpClient(mContext);
    	HttpPost post = new HttpPost("https://api.weibo.com/oauth2/access_token");
    	
    	List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
    	nameValues.add(new BasicNameValuePair("client_id", WeiBoHelper.CONSUMER_KEY));
    	nameValues.add(new BasicNameValuePair("client_secret", WeiBoHelper.CONSUMER_SECRET));
    	nameValues.add(new BasicNameValuePair("grant_type", "authorization_code"));
	    nameValues.add(new BasicNameValuePair("code", code));
	    nameValues.add(new BasicNameValuePair("redirect_uri", WeiBoHelper.RedirectUrl));
	    post.setEntity(new UrlEncodedFormEntity(nameValues));
	    
	    HttpResponse response = client.execute(post);
    	String s = read(response);
    	Log.d("response", s);
    	Bundle bundle = getSession(s);
    	String expires = bundle.getString(Weibo.EXPIRES);
    	Log.d("expires", expires);
    	long value = Long.parseLong(expires)*1000+System.currentTimeMillis();
    	bundle.putString(Weibo.EXPIRES, value+"");
    	mListener.onComplete(bundle);
    	dismiss();
    	
    }
    
    private Bundle getSession(String s) throws JSONException
    {
    	JSONObject jsonObject = new JSONObject(s);
    	Bundle bundle = new Bundle();
    	bundle.putString(Weibo.TOKEN, jsonObject.getString(Weibo.TOKEN));
    	bundle.putString(Weibo.EXPIRES, jsonObject.getString(Weibo.EXPIRES));
    	bundle.putString("uid", jsonObject.getString("uid"));
    	
    	return bundle;
    }

    private static String read(HttpResponse response) throws WeiboException {
        String result = "";
        HttpEntity entity = response.getEntity();
        InputStream inputStream;
        try {
            inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            Header header = response.getFirstHeader("Content-Encoding");
            if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }

            // Read response into a buffered stream
            int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            // Return result from buffered stream
            result = new String(content.toByteArray());
            return result;
        } catch (IllegalStateException e) {
            throw new WeiboException(e);
        } catch (IOException e) {
            throw new WeiboException(e);
        }
    }
    private static String getHtml(String urlString) {

        try {

            StringBuffer html = new StringBuffer();

            SocketAddress sa = new InetSocketAddress("10.75.0.103", 8093);
            Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, sa);

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);

            InputStreamReader isr = new InputStreamReader(conn.getInputStream());

            BufferedReader br = new BufferedReader(isr);

            String temp;

            while ((temp = br.readLine()) != null) {

                html.append(temp);

            }

            br.close();

            isr.close();
            return html.toString();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }
}
