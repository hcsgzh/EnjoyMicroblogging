package com.aisidi.kuaiyue.pager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aisidi.kuaiyue.HomeActivity;
import com.aisidi.kuaiyue.MainFragment;
import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.bean.CommTimeLineBean;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.database.KuaiDataBase;
import com.aisidi.kuaiyue.utils.FileManager;
import com.aisidi.kuaiyue.utils.ProcessType;
import com.aisidi.kuaiyue.utils.ReplayState;
import com.aisidi.kuaiyue.utils.Utils;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class UpdateWeibo extends LinearLayout{

	private EditText editText;
	private TextView countText;
	private int num = 140;
	private Context mContext;
	private ImageView getImage,pic,atIamge, topic_btn,face_btn;
	private String oldContent = "";
	private boolean submitEnable = true;
	private ProgressDialog mpDialog;
	private static int SELECT_PICTURE=0;// ���ر�־λ filed
	private static int CROP_PICTURE=1;// ���ر�־λ filed
	private HomeActivity activity;
	private String path="";
	private Button submit;
	private int itemCount;
//	private ArrayList<String> list = new ArrayList<String>();
	private String[] names = new String[0];
	private String[] trends = new String[0];
	private ArrayList<String> nameList = new ArrayList<String>();
	private AlertDialog picDialog;
	private GridView faceGridView;
	private boolean isShowing;
	public static int next_cursor = -1;
	private ListView listView;
	private LinearLayout friendView;
	private EditText txtKey;
	private boolean atLast = false;
	private TextView zhuanText, commentT;
	private KuaiDataBase base;
	private int mState;
	private HomeTimeLineBean bean;
	
	public UpdateWeibo(Context context, int state) {
		super(context);
		
		this.mState = state;
		mContext = context;
		activity = (HomeActivity)context;
		
		initView(context);
	}
	
	public UpdateWeibo(Context context, int state, HomeTimeLineBean bean) {
		super(context);
		this.bean = bean;
		this.mState = state;
		mContext = context;
		
		initView(context);
	}
	
	private void initView(Context context)
	{
		base = WeiboApplication.getInstance().getDBInstance();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.update_weibo, null);
		
		editText = (EditText)view.findViewById(R.id.update_edit);
		countText = (TextView)view.findViewById(R.id.count_text);
		submit = (Button)view.findViewById(R.id.submit_btn);
		pic = (ImageView)view.findViewById(R.id.pic_btn);
		getImage = (ImageView)view.findViewById(R.id.picImage);
		atIamge = (ImageView)view.findViewById(R.id.at_btn);
		topic_btn = (ImageView)view.findViewById(R.id.topic_btn);
		face_btn = (ImageView)view.findViewById(R.id.face_btn);
		faceGridView = (GridView)view.findViewById(R.id.face_gridView);
		listView = (ListView)view.findViewById(R.id.friendList);
		friendView = (LinearLayout)view.findViewById(R.id.friendView);
		txtKey = (EditText)view.findViewById(R.id.name_auto);
		zhuanText = (TextView)view.findViewById(R.id.zhuan);
		commentT = (TextView)view.findViewById(R.id.commentT);
		
		countText.setText(140+"");
		
		txtKey.addTextChangedListener(new TextWatcher() {  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
                  
            	String key =  txtKey.getText().toString();  
                if(key!=null && !"".equals(key.trim())){  
                	names = base.queryUserName(key,MainFragment.adminId);
                	ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.text_view, names);
    				listView.setAdapter(adapter);
                    
                }else{  
                    listView.setAdapter(null);  
                }  
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
            }  
              
            @Override  
            public void afterTextChanged(Editable s) {  
                  
            }  
        });  
		editText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				isShowing = true;
				showFaceView();
				return false;
			}
		});
		
		editText.addTextChangedListener(new TextWatcher() {
			
			 private CharSequence temp;
	         private int selectionStart;
	         private int selectionEnd;
	            
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				temp = s;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Integer number = num -s.length();
//				Log.i("countText", number+"");
				countText.setText(number+"");
				selectionStart = editText.getSelectionStart();
				selectionEnd = editText.getSelectionEnd();
				
				if (temp.length()>num) {
					atLast = true;
					s.delete(selectionStart-1, selectionEnd);
					int tempSlection = selectionStart;
					editText.setText(s);
					editText.setSelection(s.length());
				}else {
					atLast = false;
				}
				
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (submitEnable) {
					submitEnable = false;
					oldContent = editText.getText().toString();
					
					if (mState==ReplayState.PUBLISH) {
						if (!TextUtils.isEmpty(path)&&TextUtils.isEmpty(oldContent)) {
							oldContent = "分享图片";
						}else if (TextUtils.isEmpty(oldContent)) {
							oldContent = "转发微博";
						}
					}
					(new updateThread()).start();
					invisibleInput();
					if (picDialog!=null&&picDialog.isShowing()) {
						picDialog.dismiss();
					}
					mpDialog = new ProgressDialog(mContext);  
	                mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//���÷��ΪԲ�ν����  
	                mpDialog.setTitle("提示");//���ñ���  
	                mpDialog.setMessage("发表中");  
	                mpDialog.setIndeterminate(false);//���ý�����Ƿ�Ϊ����ȷ 
					mpDialog.show();
				}
			}
		});
		
		atIamge.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (atLast) {
					Toast.makeText(mContext, "空间已满", Toast.LENGTH_SHORT).show();
					return;
				}
				invisibleInput();
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,0);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				faceGridView.setLayoutParams(lp);
				
				if(friendView.isShown())
				{
					friendView.setVisibility(View.GONE);
				}else {
					friendView.setVisibility(View.VISIBLE);
				}
				txtKey.setVisibility(View.VISIBLE);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.text_view, names);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Editable editable = editText.getEditableText();
//						editable.insert(0, names[position]+" ");
						editable.append(names[position]);
						friendView.setVisibility(View.GONE);
						txtKey.setText("");
					}
				});
			}
		});
		
		topic_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (atLast) {
					Toast.makeText(mContext, "空间已满", Toast.LENGTH_SHORT).show();
					return;
				}
				invisibleInput();
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,0);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				faceGridView.setLayoutParams(lp);
				
				if(friendView.isShown())
				{
					friendView.setVisibility(View.GONE);
				}else {
					friendView.setVisibility(View.VISIBLE);
					txtKey.setVisibility(View.GONE);
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.text_view, trends);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						editText.append("#"+trends[position]+"#");
						friendView.setVisibility(View.GONE);
					}
				});
			}
		});
		
		pic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (mState!=ReplayState.PUBLISH) {
					return;
				}
				invisibleInput();
				oldContent = editText.getText().toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				picDialog = builder.create();
				builder.setTitle("添加图片").setItems(R.array.pic, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FileManager.setFilePath();
						// TODO Auto-generated method stub
						switch (which) {
						case 0://ͼ��
							ProcessType.processType = true;
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					        intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							intent.putExtra("setWallpaper", false);
							intent.putExtra("crop", "true");
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileManager.tempFile));
							intent.putExtra("outputFormat", "JPEG");
							activity.startActivityForResult(Intent.createChooser(intent, "选择图片"),
									SELECT_PICTURE);
							break;
						case 1://����
							ProcessType.processType = false;
							Intent intent2 = new Intent("android.media.action.IMAGE_CAPTURE");
							intent2.putExtra("outputFormat", "JPEG");// ���ظ�ʽ
							intent2.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(FileManager.tempFile));
							activity.startActivityForResult(intent2, CROP_PICTURE);
							break;
						default:
							break;
						}
						
						picDialog.dismiss();
					}
				});
				builder.show();
			}
		});
		
		face_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (atLast) {
					Toast.makeText(mContext, "空间已满", Toast.LENGTH_SHORT).show();
					return;
				}
				showFaceView();
			}
		});
		faceGridView.setAdapter(new FaceAdapter(mContext, Utils.faces));
		
		faceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (atLast) {
					Toast.makeText(mContext, "空间已满", Toast.LENGTH_SHORT).show();
					return;
				}
				editText.append("["+Utils.face_names[position]+"]");
				
			}
		});
		addView(view);
		
		setSubmitButton(mState);
	}
	public void shareToWeibo(String text, String path)
	{
		if (!TextUtils.isEmpty(path)) {
			setPicPath(path);
		}
		if (!TextUtils.isEmpty(text)) {
			editText.setText(text);
		}
	}
	
	public void getNameAndTrend()
	{
		names = base.queryUserName(MainFragment.adminId);
		trends = base.queryTrends(MainFragment.adminId);
	}
	

	public void setFocus()
	{
		if (picDialog!=null) {
			picDialog.dismiss();
		}
		editText.requestFocus();
		editText.requestFocusFromTouch();
		invisibleInput();
		editText.setText(oldContent);
		editText.setSelection(oldContent.length());
	}

	public void setPicPath(String path)
	{
		Log.i("path", path);
		this.path = path;
		getImage.setImageBitmap(BitmapFactory.decodeFile(path));
	}
	public void invisibleInput()
	{
//		if (mContext instanceof HomeActivity) {
//			friendView.setVisibility(View.GONE);
//			((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
//			.hideSoftInputFromWindow(((HomeActivity)mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);   
//		}
	}
	
	public void visibleInput()
	{
		friendView.setVisibility(View.GONE);
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
	}
	
	private void setSubmitButton(int state)
	{
		switch (state) {
		case ReplayState.PUBLISH:
			
			break;
		case ReplayState.REPOST:
			if (!TextUtils.isEmpty(bean.getText())&&bean.getRe_id()!=0) {
				editText.setText("//@"+bean.getUser().getScreen_name()+": "+bean.getText());
				editText.setSelection(0);
			}else if (!TextUtils.isEmpty(bean.getText())&&bean.getRe_id()==0) {
				String text = "@"+bean.getUser().getScreen_name()+": "+bean.getText();
				if (text.length()>20) {
					text = text.substring(0, 20);
				}
				zhuanText.setVisibility(View.VISIBLE);
				zhuanText.setText(Utils.setTextColor(text,this.getResources().getColor(R.color.blue),mContext));
			}
			submit.setText("转发");
			break;
		case ReplayState.COMMENTS:
			if (!TextUtils.isEmpty(bean.getText())) {
				String text = "@"+bean.getUser().getScreen_name()+": "+bean.getText();
				if (text.length()>20) {
					text = text.substring(0, 20);
				}
				commentT.setVisibility(View.VISIBLE);
				commentT.setText(Utils.setTextColor(text,this.getResources().getColor(R.color.blue),mContext));
			}
			submit.setText("评论");
			break;
		case ReplayState.REPLAY:
			submit.setText("回复");
			break;
		default:
			break;
		}
	}
	
	class updateThread extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				String lrt = "";
				switch (mState) {
				case ReplayState.PUBLISH:
					if (path.equals("")) {
						lrt = update(Weibo.getInstance(), "", oldContent, "", "");
					}else {
						lrt = upload(Weibo.getInstance(), "", path, oldContent, "", "");
					}
					break;
				case ReplayState.REPOST:
					lrt = createrepost(Weibo.getInstance(), bean.getId()+"", oldContent, 0);
					break;
				case ReplayState.COMMENTS:
					lrt = createComments(Weibo.getInstance(), oldContent, bean.getId()+"", 0);
					break;
				case ReplayState.REPLAY:
					lrt = replyComment(Weibo.getInstance(), bean.getId()+"", bean.getCid()+"",oldContent, 0, 0);
					break;
				default:
					break;
				}
				
				
				if (lrt!="") {
					Message message = handler.obtainMessage();
					message.what = 0;
					handler.sendMessage(message);
				}
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				int error = e.getStatusCode();
				Message message = handler.obtainMessage();
				message.arg1 = error;
				message.what = 1;
				handler.sendMessage(message);
				
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				submitEnable = true;
			}
			
		}
		
	}
	//��ȡ�û���ע�б�
	private String getFriends(Context context, Weibo weibo, String uid, int cursor, int count) throws MalformedURLException, IOException, WeiboException {
	  	String url = Weibo.SERVER + "friendships/friends.json";
	  	WeiboParameters bundle = new WeiboParameters();
	  	bundle.add("source", Weibo.getAppKey());
	  	bundle.add("uid", uid);
	  	bundle.add("count", count+"");
	  	bundle.add("cursor", cursor+"");
	  	String rlt = weibo.request(context, url, bundle, "GET", weibo.getAccessToken());
	  	return rlt;
	  }
	/**
	@param comment_ori 当评论转发微博时，是否评论给原微博，0：否、1：是，默认为0。
	*/
//	回复一条评论
	/**
	 * 
	 * @param weibo
	 * @param id 需要评论的微博ID。
	 * @param cid 需要回复的评论ID。
	 * @param comment 回复评论内容，必须做URLencode，内容不超过140个汉字。
	 * @param without_mention 回复中是否自动加入“回复@用户名”，0：是、1：否，默认为0。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博，0：否、1：是，默认为0。
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws WeiboException
	 */
    private String replyComment(Weibo weibo, String id, String cid,String comment, int without_mention, int comment_ori) throws MalformedURLException, IOException, WeiboException {
    	String url = Weibo.SERVER + "comments/reply.json";
    	WeiboParameters bundle = new WeiboParameters();
    	bundle.add("source", Weibo.getAppKey());
    	bundle.add("id", id);
    	bundle.add("cid", cid);
    	bundle.add("comment", comment);
    	bundle.add("without_mention", without_mention+"");
    	bundle.add("comment_ori", comment_ori+"");
    	String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
    	return rlt;
    }
    private String createComments(Weibo weibo, String comment, String id, int comment_ori) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "comments/create.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("comment", comment);
		bundle.add("id", id);
		bundle.add("comment_ori", comment_ori+"");
		String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
    /**
	@param is_comment 是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论，默认为0 。
	*/
    private String createrepost(Weibo weibo, String id,String status,int is_comment) throws MalformedURLException, IOException, WeiboException {
    	String url = Weibo.SERVER + "statuses/repost.json";
    	WeiboParameters bundle = new WeiboParameters();
    	bundle.add("source", Weibo.getAppKey());
    	bundle.add("id", id);
    	Log.i("createrepost", id);
    	if (!status.equals("")) {
    		bundle.add("status", status);
		}
    	bundle.add("is_comment", is_comment+"");
    	String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
    	return rlt;
    }
	/**
	 * 
	 * @param weibo
	 * @param source
	 * @param file
	 * @param status
	 * @param lon
	 * @param lat
	 * @return
	 * @throws WeiboException
	 */
	private String upload(Weibo weibo, String source, String file, String status, String lon,
            String lat) throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("pic", file);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/upload.json";
        try {
            rlt = weibo.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, Weibo.getInstance().getAccessToken());
        } catch (WeiboException e) {
            throw e;
        }
        return rlt;
    }

	//��������΢��
    private String update(Weibo weibo, String source, String status, String lon, String lat)
            throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/update.json";
        rlt = weibo.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, Weibo.getInstance().getAccessToken());
        return rlt;
    }
    
    /**
     * �������һ���ڵ����Ż���
     * @param weibo
     * @param source
     * @param base_app �Ƿ�ֻ��ȡ��ǰӦ�õ���ݡ�0Ϊ��������ݣ���1Ϊ�ǣ�����ǰӦ�ã���Ĭ��Ϊ0��
     * @return
     * @throws WeiboException
     */
    private String getDaily(Weibo weibo, String source, int base_app)
            throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("base_app", base_app+"");
        String rlt = "";
        String url = Weibo.SERVER + "trends/daily.json";
        rlt = weibo.request(mContext, url, bundle, Utility.HTTPMETHOD_GET, weibo.getAccessToken());
        return rlt;
    }
    
    public void getTrends(Weibo weibo, String source, int base_app)
    {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String lrt;
				try {
					lrt = getDaily(Weibo.getInstance(), "", 0);
					getTrendsJSON(lrt);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
    }
	
    public void getJSON(String str)
	{
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("users");
			itemCount = jsonArray.length();
			for (int i = 0; i < itemCount; i++) {
				JSONObject user = jsonArray.getJSONObject(i);
				nameList.add("@"+user.getString("screen_name")+" ");
			}
			try {
				next_cursor = jsonObject.getInt("next_cursor");
				if (itemCount==200) {
					getMyFriends();
				}else {
					SharedPreferences store = mContext.getSharedPreferences("token",0);
					Editor editor = store.edit();
					editor.putLong("friendsTime", System.currentTimeMillis());
					editor.commit();
					
					base.deleteAllFriendsName(MainFragment.adminId);
					base.insertFriendsName(nameList,MainFragment.adminId);
				}
				
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
    public void getTrendsJSON(String str)
	{
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONObject trendsObject = jsonObject.getJSONObject("trends");
			JSONArray jsonArray = trendsObject.names();
			String arrayName = jsonArray.getString(0);
			JSONArray trendsArray = trendsObject.getJSONArray(arrayName);
			itemCount = trendsArray.length();
			trends = new String[itemCount];
			for (int i = 0; i < itemCount; i++) {
				JSONObject user = trendsArray.getJSONObject(i);
				trends[i] = user.getString("name");
			}
			base.deleteAllTrends(MainFragment.adminId);
			base.insertTrends(trends,MainFragment.adminId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mpDialog!=null) {
					mpDialog.cancel();
				}
				oldContent = "";
				editText.setText("");
				clearIamge();
				Toast.makeText(mContext.getApplicationContext(), "发表成功", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (mpDialog!=null) {
					mpDialog.cancel();
				}
				String error = "";
				switch (msg.arg1) {
				case 40076:
					error = "含有非法词";
					break;
				case 20019:
					error = "不要太贪心哦，发一次就够啦";
					break;
				case 20017:
					error = "你刚刚已经发送过相似内容了哦，先休息一会吧";
					break;
				case 20016:
					error = "发微博太多啦，休息一会儿吧";
					break;
				case 20008:
					error = "内容为空";
					break;
				case 10003:
					error = "远程服务出错";
					break;
				case 10009:
					error = "任务过多，系统繁忙";
					break;
				case 10001 :
					error = "系统错误";
					break;
				case 21331:
					error = "服务暂时无法访问";
					break;
				default:
					error = "发表失败，错误代码:"+msg.arg1;
					break;
				}
				Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				
				break;
			default:
				break;
			}
		}
		
	};
	private void clearIamge()
	{
		path = "";
		getImage.setImageResource(R.color.alpha);
	}
	
	class FaceAdapter extends BaseAdapter
	{
	
		private Context mContext;
		private Drawable[] faces;
		FaceAdapter(Context context,Drawable[] faces) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.faces = faces;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return faces.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return faces[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			imageView.setScaleType(ScaleType.CENTER);
			imageView.setAdjustViewBounds(true);
			imageView.setImageDrawable(faces[position]);
			imageView.setPadding(15, 15, 15, 15);
			
			return imageView;
		}
		
	}
	
	public void getMyFriends()
	{
		long time = base.getLonginTimeStamp(MainFragment.adminId);
		if (System.currentTimeMillis()-time>24*3600*1000) {
			new Thread(new getFriends()).start();
			base.updateAdminTimeStamp(MainFragment.adminId, System.currentTimeMillis());
			Log.d("getMyFriends", "大于一天取列表");
		}else {
			Log.d("getMyFriends", "小于一天不取列表");
		}
	}
	
	class getFriends extends Thread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String str = getFriends(mContext, Weibo.getInstance(), MainFragment.adminId, 0, 200);
				getJSON(str);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				Message message = handler.obtainMessage();
				message.what = 2;
				message.obj = e;
				handler.sendMessage(message);
				e.printStackTrace();
			}
		}
		
	}
	
	private void showFaceView()
	{
		if (!isShowing) {
			invisibleInput();
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,Utils.dip2px(mContext, 200));
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			faceGridView.setLayoutParams(lp);
			
		}else {
			visibleInput();
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,0);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			faceGridView.setLayoutParams(lp);
		}
		isShowing = !isShowing;
	}

}
