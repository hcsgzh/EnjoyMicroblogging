package com.aisidi.kuaiyue.net;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.aisidi.kuaiyue.utils.FileManager;



public class HttpUtility {

	public static final String HTTPMETHOD_POST = "POST";
    public static final String HTTPMETHOD_GET = "GET";
    public static final String HTTPMETHOD_DELETE = "DELETE";

    public static final int SET_CONNECTION_TIMEOUT = 50000;
    public static final int SET_SOCKET_TIMEOUT = 200000;
    public static final String URL_ENCODE = "UTF-8";

    private static final int DOWNLOAD_CONNECT_TIMEOUT = 15 * 1000;
    private static final int DOWNLOAD_READ_TIMEOUT = 60 * 1000;
    
    /**
     * Get a HttpClient object which is setting correctly .
     * 
     * @param context
     *            : context of activity
     * @return HttpClient: HttpClient object
     */
    private static HttpClient getHttpClient(Context context) {
        BasicHttpParams httpParameters = new BasicHttpParams();
        // Set the default socket timeout (SO_TIMEOUT) // in
        // milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setConnectionTimeout(httpParameters, SET_CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, SET_SOCKET_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParameters);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            // 获取当前正在使用的APN接入�?
            Uri uri = Uri.parse("content://telephony/carriers/preferapn");
            Cursor mCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                // 游标移至第一条记录，当然也只有一�?
                String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
                if (proxyStr != null && proxyStr.trim().length() > 0) {
                    HttpHost proxy = new HttpHost(proxyStr, 80);
                    client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                }
                mCursor.close();
            }
        }
        return client;
    }
    
    public static boolean doGetSaveFile(String urlStr, String path)
    {
    	File file = FileManager.creatNewFileInSDCard(path);
    	if (file==null) {
			return false;
		}
    	
    	FileOutputStream fos = null;
    	InputStream is = null;
    	HttpURLConnection urlConnection = null;
    	
    	try {
			URL url = new URL(urlStr);
			Proxy proxy = getProxy();
			if (proxy!=null) {
				urlConnection = (HttpURLConnection)url.openConnection(proxy);
			}else {
				urlConnection = (HttpURLConnection)url.openConnection();
			}
			
			urlConnection.setRequestMethod(HTTPMETHOD_GET);
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(DOWNLOAD_CONNECT_TIMEOUT);
			urlConnection.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection.setRequestProperty("Accept-Encoding", "gzip,deflate");
			
			urlConnection.connect();
			
			int staut = urlConnection.getResponseCode();
			if (staut!=HttpURLConnection.HTTP_OK) {
				return false;
			}
			
			int byteTotal = urlConnection.getContentLength();
			int byteSum = 0;
			int byteRead = 0;
			fos = new FileOutputStream(file);
			is = urlConnection.getInputStream();
			
			final Thread thread = Thread.currentThread();
			byte[] buffer = new byte[1444];
			while ((byteRead=is.read(buffer))!=-1) {
				if (thread.isInterrupted()) {
					file.delete();
					throw new InterruptedIOException();
				}
				byteSum += byteRead;
				fos.write(buffer, 0, byteRead);
			}
			
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeSilently(is);
			closeSilently(fos);
			if (urlConnection!=null) {
				urlConnection.disconnect();
			}
		}
    	return false;
    }
    
    public static void closeSilently(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException ignored) {

            }
    }
    
    
    private static String handleResponse(HttpURLConnection httpURLConnection){
        int status = 0;
        try {
            status = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpURLConnection.disconnect();
        }

        if (status != HttpURLConnection.HTTP_OK) {
            return handleError(httpURLConnection);
        }

        return readResult(httpURLConnection);
    }
    
    private static String handleError(HttpURLConnection urlConnection){

        String result = readError(urlConnection);
        String err = null;
        int errCode = 0;
        try {
            JSONObject json = new JSONObject(result);
            err = json.optString("error_description", "");
            if (TextUtils.isEmpty(err))
                err = json.getString("error");
            errCode = json.getInt("error_code");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }
    private static String readResult(HttpURLConnection urlConnection)  {
        InputStream is = null;
        BufferedReader buffer = null;
        try {
            is = urlConnection.getInputStream();

            String content_encode = urlConnection.getContentEncoding();

            if (null != content_encode && !"".equals(content_encode) && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
        	try {
				is.close();
				buffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            urlConnection.disconnect();
        }

    }

    private static String readError(HttpURLConnection urlConnection){
        InputStream is = null;
        BufferedReader buffer = null;

        try {
            is = urlConnection.getErrorStream();

            if (is == null) {
            }

            String content_encode = urlConnection.getContentEncoding();

            if (null != content_encode && !"".equals(content_encode) && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
        	
        	try {
        		is.close();
				buffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            urlConnection.disconnect();
        }
        
    }
    /**
     * Read http requests result from response .
     * 
     * @param response
     *            : http response by executing httpclient
     * 
     * @return String : http response content
     */
    public static String read(HttpResponse response) {
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
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        return result;
    }
    public static Bundle decodeUrl(String s) throws UnsupportedEncodingException
    {
    	Bundle bundle = new Bundle();
    	if (!TextUtils.isEmpty(s)) {
			String[] array = s.split("&");
			for (int i = 0; i < array.length; i++) {
				String[] v = array[i].split("=");
				bundle.putString(URLDecoder.decode(v[0], URL_ENCODE), URLDecoder.decode(v[1], URL_ENCODE));
			}
		}
    	
    	return bundle;
    }
    private static Proxy getProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort))
            return new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
        else
            return null;
    }
   
    
}
