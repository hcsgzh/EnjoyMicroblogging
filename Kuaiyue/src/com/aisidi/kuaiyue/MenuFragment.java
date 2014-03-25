package com.aisidi.kuaiyue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aisidi.kuaiyue.activity.ProfileActivity;
import com.aisidi.kuaiyue.activity.SetActivity;
import com.aisidi.kuaiyue.bean.LoginInfoBean;
import com.aisidi.kuaiyue.database.KuaiDataBase;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class MenuFragment extends Fragment implements MenuChange{


	private Context mContext;
	public LinearLayout[] menuLayouts = new LinearLayout[5];
//	public PopupWindow menuPopupWindow;
	public static MenuFragment menuFragment;
	private int one, two, three, four, five;
	private Intent intent;
	private static MenuChange menuChange;
	private String item_uid;
	private ProgressDialog mpDialog;
	private boolean creat;
	private boolean isMyMenu;
	private static FollowListener followListener;
	public static int stauts, follows, friends, fiverise;
	private ListView listView, groupsListView;
	private UserAdapter adapter;
	public static boolean longing = false;
	private LinearLayout groups;
	private RelativeLayout groups_title;
	private TextView groupsize,user_size, menu_versionCode;
	private ImageView groups_pointing, user_pointing;
	private KuaiDataBase base;
	private ArrayList<LoginInfoBean> loginUserList;
	private RelativeLayout menu4;
	private int selectedPosition;
	private GroupsAdapter groupsAdapter;
	
	public static MenuChange getInstance() {
		// TODO Auto-generated constructor stub
		return menuChange;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = getActivity();
		// TODO Auto-generated method stub
		
		base = WeiboApplication.getInstance().getDBInstance();
		loginUserList = base.selectAllLoginUser();
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.menu_layout, null);
		
		menuLayouts[1] = (LinearLayout)view.findViewById(R.id.menu1);
		menuLayouts[2] = (LinearLayout)view.findViewById(R.id.menu2);
		menuLayouts[3] = (LinearLayout)view.findViewById(R.id.menu3);
		menu4 = (RelativeLayout)view.findViewById(R.id.menu4);
		listView = (ListView)view.findViewById(R.id.menu_user_list);
		groupsListView = (ListView)view.findViewById(R.id.groups_list);
		groups = (LinearLayout)view.findViewById(R.id.groups);
		groups_title = (RelativeLayout)view.findViewById(R.id.groups_title);
		groupsize = (TextView)view.findViewById(R.id.groupsize);
		user_size = (TextView)view.findViewById(R.id.user_size);
		menu_versionCode = (TextView)view.findViewById(R.id.menu_versionCode);
		groups_pointing = (ImageView)view.findViewById(R.id.groups_pointing);
		user_pointing = (ImageView)view.findViewById(R.id.user_pointing);
		
		PackageInfo pkg;
		try {
			pkg = getActivity().getPackageManager().getPackageInfo(getActivity().getApplication().getPackageName(), 0);
			menu_versionCode.setText(pkg.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		user_size.setText(loginUserList.size()+"");
		groupsListView.setScrollbarFadingEnabled(false);
		groups_title.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (groupsListView.getVisibility()==View.VISIBLE) {
					groupsListView.setVisibility(View.GONE);
					groups_pointing.setImageResource(R.drawable.xia);
//					user_pointing.setImageResource(R.drawable.shang);
//					if (loginUserList.size()>1) {
//						listView.setVisibility(View.VISIBLE);
//					}
					
				}else {
					groups_pointing.setImageResource(R.drawable.shang);
					groupsListView.setVisibility(View.VISIBLE);
//					user_pointing.setImageResource(R.drawable.xia);
//					listView.setVisibility(View.GONE);
				}
			}
		});
		
		adapter = new UserAdapter(mContext, loginUserList);
		listView.setAdapter(adapter);
//		查看本人的账户
		menu4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2 = new Intent();
				if (MainFragment.bean!=null) {
					intent2.putExtra("user", MainFragment.bean);
					intent2.setClass(mContext, ProfileActivity.class);
					mContext.startActivity(intent2);
				}
			}
		});
//		账号管理
		menuLayouts[1].setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listView.getVisibility()==View.VISIBLE) {
					listView.setVisibility(View.GONE);
//					groupsListView.setVisibility(View.VISIBLE);
//					groups_pointing.setImageResource(R.drawable.shang);
					user_pointing.setImageResource(R.drawable.xia);
				}else {
//					groupsListView.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
//					groups_pointing.setImageResource(R.drawable.xia);
					user_pointing.setImageResource(R.drawable.shang);
//					Log.i("userList", loginUserList.size()+"");
					if (adapter!=null) {
						adapter.notifyDataSetChanged();
					}
				}
			}
		});
//		添加账号
		menuLayouts[2].setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mContext instanceof HomeActivity) {
					((HomeActivity)mContext).OpenLonginPage();
					longing = true;
				}
			}
		});
		
//		设置
		menuLayouts[3].setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MainFragment.bean!=null) {
					Intent intent2 = new Intent();
					intent2.putExtra("profile_image_url", MainFragment.bean.getProfile_image_url());
					intent2.setClass(mContext, SetActivity.class);
					mContext.startActivity(intent2);
				}
				
			}
		});
		
		return view;
	}

	/**
	 * 
	 * @param one 微博数
	 * @param two 粉丝数
	 * @param three 关注数
	 */
	public void setNum(int one, int two, int three)
	{
		this.one = one;
		this.two = two;
		this.three = three;
		
	}
	/**
	 * 
	 * @param one 微博数
	 * @param two 粉丝数
	 * @param three 关注数
	 * @param four 收藏数
	 */
	public void setNum(int one, int two, int three, int four)
	{
		this.one = one;
		this.two = two;
		this.three = three;
		this.four = four;
		stauts = one;
		follows = two;
		friends = three;
		fiverise = four;
		
	}
	
	public void showGroups(ArrayList<HashMap<String, String>> list)
	{
		
		if (list.size()>0) {
			groups.setVisibility(View.VISIBLE);
			GroupsAdapter adapter = new GroupsAdapter(mContext, list);
			groupsAdapter = adapter;
			groupsListView.setAdapter(adapter);
		}
		
	}
	
	private class GroupsAdapter extends BaseAdapter
	{
		private ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflater;
		
		GroupsAdapter(Context context, ArrayList<HashMap<String, String>> list) {
			// TODO Auto-generated constructor stub
			this.list = list;
			inflater = LayoutInflater.from(context);
			
			groupsize.setText(list.size()+"");
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = inflater.inflate(R.layout.groups_item, null);
			final HashMap<String, String> map = list.get(position);
			
			String name = map.get("name");
			if (position==0) {
				name = MainFragment.screen_name + "(全部)";
			}
			TextView groupsName = (TextView)convertView.findViewById(R.id.groups_item_name);
			ImageView groupsImage = (ImageView)convertView.findViewById(R.id.groups_item_image);
			
			groupsName.setText(name);
			
			
			groupsName.setTextColor(mContext.getResources().getColor(R.color.menu_item_unselect));
			groupsImage.setImageResource(R.drawable.right);
			if (selectedPosition==position) {
				groupsName.setTextColor(mContext.getResources().getColor(R.color.blue));
				groupsImage.setImageResource(R.drawable.right_sel);
			}
			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mContext instanceof HomeActivity) {
						((HomeActivity)mContext).changeGroupsTimeLine(map.get("idstr"));
						selectedPosition = position;
						notifyDataSetChanged();
					}
					
				}
			});
			return convertView;
		}
		
	}
	
	private class UserAdapter extends BaseAdapter
	{
		private LayoutInflater inflater;
		private Context mContext;
		
		public UserAdapter(Context context, ArrayList<LoginInfoBean> list) {
			// TODO Auto-generated constructor stub
//			this.list = list;
			mContext = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loginUserList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return loginUserList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = inflater.inflate(R.layout.menu_list_item, null);
			TextView textView = (TextView)convertView.findViewById(R.id.menu_list_name);
			ImageView imageView = (ImageView)convertView.findViewById(R.id.menu_list_image);
			
			textView.setText(loginUserList.get(position).getName().toString());
			if (loginUserList.get(position).isLogined()) {
				imageView.setImageResource(R.drawable.select);
				textView.setTextColor(mContext.getResources().getColor(R.color.blue));
			}else {
				imageView.setImageResource(R.drawable.delete);
				imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						builder.setMessage("移除此账户？");
						builder.setPositiveButton("移除", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								int delete = base.deleteAdmin(loginUserList.get(position).getUid());
//								Log.i("delete", delete+"");
								loginUserList = base.selectAllLoginUser();
//								Log.i("userList", loginUserList.size()+"");
								updateList();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						builder.show();
					}
				});
//				
				textView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!loginUserList.get(position).isLogined()) {
							AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							builder.setMessage("确定切换账户到:"+loginUserList.get(position).getName());
							builder.setPositiveButton("切换", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									base.makeAdminLogin(MainFragment.adminId, false);
									MainFragment.adminId = loginUserList.get(position).getUid();
									String token = loginUserList.get(position).getToken();
									String expires_in = loginUserList.get(position).getExpires();
									base.makeAdminLogin(MainFragment.adminId, true);
									((HomeActivity)mContext).changeUserUI(token, expires_in);
									loginUserList = base.selectAllLoginUser();
									updateList();
								}
							});
							builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							builder.show();
						}
					}
				});
			}
			
			
			return convertView;
		}
		
	}
	
	public void updateList()
	{
		if (adapter!=null) {
			selectedPosition = 0;
			loginUserList = base.selectAllLoginUser();
			user_size.setText(loginUserList.size()+"");
			if (adapter!=null) {
				adapter.notifyDataSetChanged();
			}
			
			if (groupsAdapter!=null) {
				groupsAdapter.notifyDataSetChanged();
			}
			
		}
	}
	
    private String createFriends(Weibo weibo, String uid) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/create.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
    
    private String destroyFriends(Weibo weibo, String uid) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/destroy.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
    
    class SetFriends extends Thread
    {
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String lrt = "";
			
			try {
				Log.i("createFriends", creat+"");
				if (creat) {
					lrt = createFriends(Weibo.getInstance(), item_uid);
				}else {
					lrt = destroyFriends(Weibo.getInstance(), item_uid);
				}
				if (!lrt.equals("")) {
					Message message = handler.obtainMessage();
					message.what = 0;
					handler.sendMessage(message);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				Message message = handler.obtainMessage();
				message.what = 1;
				handler.sendMessage(message);
				e.printStackTrace();
			}
		}
    }
    public void setFollowListener(FollowListener followListener)
    {
    	this.followListener = followListener;
    }
    Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mpDialog!=null) {
					mpDialog.cancel();
					
					if (menuChange!=null) {
						if (creat) {
							menuChange.addFollowCount();
						}else {
							menuChange.deleteFollowCount();
						}
						
						if (followListener!=null) {
							followListener.setFollow(intent.getIntExtra("index", 0), creat);
						}
						creat = !creat;
						
					}
				}
				Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (mpDialog!=null) {
					mpDialog.cancel();
					Toast.makeText(mContext, "微博异常", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
    	
    };
	@Override
	public void addWeiBoCount() {
		// TODO Auto-generated method stub
		setNum(one+1, two, three, four);
	}
	@Override
	public void addFollowCount() {
		// TODO Auto-generated method stub
		if (isMyMenu) {
			setNum(one, two, three+1, four);
		}else {
			setNum(one, two, three+1);
		}
		
	}
	@Override
	public void deleteFollowCount() {
		// TODO Auto-generated method stub
		if (isMyMenu) {
			setNum(one, two, three-1, four);
		}else {
			setNum(one, two, three-1);
		}
	}
	@Override
	public void addFavoritesCount() {
		// TODO Auto-generated method stub
		setNum(one, two, three, four+1);
	}
	@Override
	public void deleteFavoritesCount() {
		// TODO Auto-generated method stub
		setNum(one, two, three, four-1);
	}
	
	public interface FollowListener{
		void setFollow(int index, boolean following);
	}
}
