package com.aisidi.kuaiyue.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import com.aisidi.kuaiyue.R;


public class Utils {
	private static SimpleDateFormat formater = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);
	private static HashMap<String, Drawable> map = new HashMap<String, Drawable>();
	public static Drawable[] faces;
	public static String[] face_names;
	private static boolean isShowingNetDiaLog = false;
	
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int getHomePadding(Context context)
	{
		return dip2px(context, 13);
	}
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static String[] getPicUrl(String str)
	{
		String[] pics = new String[3];
		pics[0] = str;
		pics[1] = str.replaceFirst("thumbnail", "bmiddle");
		pics[2] = str.replaceFirst("thumbnail", "large");
		
		return pics;
	}
	
	
	
	public static SpannableStringBuilder setTextSize(String str, int size, Context context)
	{
		int dp = dip2px(context, size);
		SpannableStringBuilder spannables = new SpannableStringBuilder(str);
		
		AbsoluteSizeSpan span = new AbsoluteSizeSpan(dp);
		int start = str.indexOf("(");
		int end = str.indexOf(")")+1;
		spannables.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return spannables;
	}
	
	public static String checkURL(String str)
	{
		boolean flag2 = false;
		int start2 = 0;
		int end2 = 0;
		for (int i = 0; i < str.length(); i++) {
			if ((!flag2)&&str.indexOf("http://", i)!=-1) {
			start2 = str.indexOf("http://", i);
				if (start2==i) {
					flag2 = true;
				}
			}else if (flag2&&(str.charAt(i)==' '||str.charAt(i)==' '||str.charAt(i)>256||str.length()==i+1)) {
				
				if (str.charAt(i)>256) {
					end2 = i;
				}else {
					end2 = i+1;
				}
				str = str.substring(0, end2)+" "+str.substring(end2, str.length());
				flag2 = false;
				
			}
		}
		return str;
	}
	 public class Defs {
		 public static final String SCHEMA = "devdiv://sina_profile";
		 public static final String SCHEMA1 = "devdiv://sina_profile1";
		 public static final String PARAM_UID = "uid";
		}
//	public static void extractMention2Link(TextView v) {
//        v.setAutoLinkMask(0);
//        Pattern pattern = Pattern.compile("@(\\w+?)(?=\\W|$)");  
//        String scheme = String.format("%s/?%s=", Defs.SCHEMA, Defs.PARAM_UID);
//        Linkify.addLinks(v, pattern, scheme, null, new TransformFilter() {
//        @Override
//        public String transformUrl(Matcher match, String url) {
//         Log.d("extractMention2Link", match.group(1));
//         return match.group(1); // Ҫ����������ҳ��Ķ���
//      }
//     });        
////        Pattern pattern1 = Pattern.compile("#(\\w+?)#");
////        String scheme1 = String.format("%s/?%s=", Defs.SCHEMA1, Defs.PARAM_UID);
////        Linkify.addLinks(v, pattern1, scheme1, null, new TransformFilter() {
////      @Override
////      public String transformUrl(Matcher match, String url) {
////       Log.d("extractMention2Link", match.group(1));
////       return match.group(1); // Ҫ����������ҳ��Ķ���
////      }
////     });        
// }
	
	 
	public static SpannableStringBuilder setTextColor(String str, int color, Context context)
    {
    	SpannableStringBuilder spannables = new SpannableStringBuilder(str);
    	int end = 0;
		int start = 0;
		int start2 = 0;
		int end2 = 0;
		int start3 = 0;
		int end3 = 0;
		int start4 = 0;
		int end4 = 0;
		
		boolean flag = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i)=='@') {
				flag = true;
				start = i;
//				Log.i("start", start+"");
			}else if (flag&&(str.charAt(i)=='：'||str.charAt(i)==':'||str.charAt(i)==' '||str.charAt(i)==' '||str.length()==i+1)) {
				end = i+1;
				ForegroundColorSpan span = new ForegroundColorSpan(color);
				spannables.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				flag = false;
//				Log.i("end", end+"");
				
//				View.OnClickListener l = new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Log.i("onClick", "onClick");
//					}
//				};
//				spannables.setSpan(new Clickable(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
			else if ((!flag3)&&str.charAt(i)=='#') {
				flag3 = true;
				start3 = i;
			}else if (flag3&&(str.charAt(i)=='#')) {
				end3 = i+1;
				ForegroundColorSpan span = new ForegroundColorSpan(color);
				spannables.setSpan(span, start3, end3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				flag3 = false;
			}else if ((!flag4)&&str.charAt(i)=='[') {
				flag4 = true;
				start4 = i;
			}else if (flag4&&str.charAt(i)==']') {
				end4 = i+1;
				flag4 = false;
				String face = str.substring(start4+1, end4-1);
				Drawable drawable = getDrawable(face, context);
				if (drawable==null) {
					continue;
				}
				ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
				spannables.setSpan(span, start4, end4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
//			if ((!flag2)&&str.indexOf("http://", i)!=-1) {
//				start2 = str.indexOf("http://", i);
//				if (start2==i) {
//					flag2 = true;
//				}
////				Log.i("start2", start2+"");
//			}else if (flag2&&(str.charAt(i)==' '||str.charAt(i)==' '||str.charAt(i)>256||str.length()==i+1)) {
//				
//				if (str.charAt(i)>256) {
//					end2 = i;
//				}else {
//					end2 = i+1;
//				}
////				Log.i("end2", end2+"");
//				final String url = str.substring(start2, end2);
//				ForegroundColorSpan span = new ForegroundColorSpan(color);
//				spannables.setSpan(span, start2, end2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//				spannables.setSpan(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						
//						Log.i("OnClickListener", url);
//					};
//				}, start2, end2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//				flag2 = false;
//			}
		}
    	return spannables;
    }
	
	static class Clickable extends ClickableSpan
	{
		
//		Clickable(View.OnClickListener l) {
//			// TODO Auto-generated constructor stub
//		}

		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			Log.i("onClick", "onClick");
		}
		
	}
	public static SpannableStringBuilder setTextColor(String pre,String name,String last,int color)
	{
		SpannableStringBuilder spannable = new SpannableStringBuilder(pre+name+last);
		ForegroundColorSpan span = new ForegroundColorSpan(color);
		spannable.setSpan(span, pre.length(), pre.length()+name.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		
		return spannable;
	}
	
	public static void setFace(Context context)
	{
		faces = new Drawable[57];
		faces[0] = context.getResources().getDrawable(R.drawable.face3);
		faces[1] = context.getResources().getDrawable(R.drawable.face6);
		faces[2] = context.getResources().getDrawable(R.drawable.face7);
		faces[3] = context.getResources().getDrawable(R.drawable.face13);
		faces[4] = context.getResources().getDrawable(R.drawable.face17);
		faces[5] = context.getResources().getDrawable(R.drawable.face25);
		faces[6] = context.getResources().getDrawable(R.drawable.face90);
		faces[7] = context.getResources().getDrawable(R.drawable.face100);
		faces[8] = context.getResources().getDrawable(R.drawable.face102);
		faces[9] = context.getResources().getDrawable(R.drawable.face106);
		faces[10] = context.getResources().getDrawable(R.drawable.face120);
		faces[11] = context.getResources().getDrawable(R.drawable.face121);
		faces[12] = context.getResources().getDrawable(R.drawable.face180);
		faces[13] = context.getResources().getDrawable(R.drawable.face182);
		faces[14] = context.getResources().getDrawable(R.drawable.face191);
		faces[15] = context.getResources().getDrawable(R.drawable.face196);
		faces[16] = context.getResources().getDrawable(R.drawable.face198);
		faces[17] = context.getResources().getDrawable(R.drawable.face201);
		faces[18] = context.getResources().getDrawable(R.drawable.face202);
		faces[19] = context.getResources().getDrawable(R.drawable.face205);
		faces[20] = context.getResources().getDrawable(R.drawable.face208);
		faces[21] = context.getResources().getDrawable(R.drawable.face218);
		faces[22] = context.getResources().getDrawable(R.drawable.face219);
		faces[23] = context.getResources().getDrawable(R.drawable.face220);
		faces[24] = context.getResources().getDrawable(R.drawable.face229);
		faces[25] = context.getResources().getDrawable(R.drawable.face231);
		faces[26] = context.getResources().getDrawable(R.drawable.face233);
		faces[27] = context.getResources().getDrawable(R.drawable.face234);
		faces[28] = context.getResources().getDrawable(R.drawable.face238);
		faces[29] = context.getResources().getDrawable(R.drawable.face239);
		faces[30] = context.getResources().getDrawable(R.drawable.face242);
		faces[31] = context.getResources().getDrawable(R.drawable.face247);
		faces[32] = context.getResources().getDrawable(R.drawable.face248);
		faces[33] = context.getResources().getDrawable(R.drawable.face251);
		faces[34] = context.getResources().getDrawable(R.drawable.face252);
		faces[35] = context.getResources().getDrawable(R.drawable.face253);
		faces[36] = context.getResources().getDrawable(R.drawable.face254);
		faces[37] = context.getResources().getDrawable(R.drawable.face255);
		faces[38] = context.getResources().getDrawable(R.drawable.face257);
		faces[39] = context.getResources().getDrawable(R.drawable.face258);
		faces[40] = context.getResources().getDrawable(R.drawable.face261);
		faces[41] = context.getResources().getDrawable(R.drawable.face263);
		faces[42] = context.getResources().getDrawable(R.drawable.face264);
		faces[43] = context.getResources().getDrawable(R.drawable.face266);
		faces[44] = context.getResources().getDrawable(R.drawable.face268);
		faces[45] = context.getResources().getDrawable(R.drawable.face270);
		faces[46] = context.getResources().getDrawable(R.drawable.face271);
		faces[47] = context.getResources().getDrawable(R.drawable.face273);
		faces[48] = context.getResources().getDrawable(R.drawable.face274);
		faces[49] = context.getResources().getDrawable(R.drawable.face277);
		faces[50] = context.getResources().getDrawable(R.drawable.face278);
		faces[51] = context.getResources().getDrawable(R.drawable.face279);
		faces[52] = context.getResources().getDrawable(R.drawable.face280);
		faces[53] = context.getResources().getDrawable(R.drawable.face281);
		faces[54] = context.getResources().getDrawable(R.drawable.face300);
		faces[55] = context.getResources().getDrawable(R.drawable.face311);
		faces[56] = context.getResources().getDrawable(R.drawable.face321);
		
		face_names = new String[57];
		face_names[0] = "蜡烛";
		face_names[1] = "衰";
		face_names[2] = "晕";
		face_names[3] = "哼";
		face_names[4] = "亲亲";
		face_names[5] = "呵呵";
		face_names[6] = "话筒";
		face_names[7] = "good";
		face_names[8] = "ok";
		face_names[9] = "赞";
		face_names[10] = "汗";
		face_names[11] = "囧";
		face_names[12] = "悲伤";
		face_names[13] = "吃惊";
		face_names[14] = "打哈欠";
		face_names[15] = "钱";
		face_names[16] = "泪";
		face_names[17] = "害羞";
		face_names[18] = "睡觉";
		face_names[19] = "吐";
		face_names[20] = "可爱";
		face_names[21] = "围观";
		face_names[22] = "威武";
		face_names[23] = "奥特曼";
		face_names[24] = "浮云";
		face_names[25] = "礼物";
		face_names[26] = "嘻嘻";
		face_names[27] = "哈哈";
		face_names[28] = "馋嘴";
		face_names[29] = "抓狂";
		face_names[30] = "怒";
		face_names[31] = "偷笑";
		face_names[32] = "酷";
		face_names[33] = "怒骂";
		face_names[34] = "鄙视";
		face_names[35] = "挖鼻屎";
		face_names[36] = "花心";
		face_names[37] = "鼓掌";
		face_names[38] = "思考";
		face_names[39] = "生病";
		face_names[40] = "抱抱";
		face_names[41] = "右哼哼";
		face_names[42] = "左哼哼";
		face_names[43] = "委屈";
		face_names[44] = "可怜";
		face_names[45] = "握手";
		face_names[46] = "耶";
		face_names[47] = "弱";
		face_names[48] = "不要";
		face_names[49] = "来";
		face_names[50] = "蛋糕";
		face_names[51] = "心";
		face_names[52] = "伤心";
		face_names[53] = "猪头";
		face_names[54] = "飞个吻";
		face_names[55] = "泪流满面";
		face_names[56] = "江南style";
		
		for (int i = 0; i < face_names.length; i++) {
			map.put(face_names[i], faces[i]);
		}
		
//		map.put("����", context.getResources().getDrawable(R.drawable.face3));
//		map.put("˥", context.getResources().getDrawable(R.drawable.face6));
//		map.put("��", context.getResources().getDrawable(R.drawable.face7));
//		map.put("��", context.getResources().getDrawable(R.drawable.face13));
//		map.put("����", context.getResources().getDrawable(R.drawable.face17));
//		map.put("�Ǻ�", context.getResources().getDrawable(R.drawable.face25));
//		map.put("��Ͳ", context.getResources().getDrawable(R.drawable.face90));
//		map.put("good", context.getResources().getDrawable(R.drawable.face100));
//		map.put("ok", context.getResources().getDrawable(R.drawable.face102));
//		map.put("��", context.getResources().getDrawable(R.drawable.face106));
//		map.put("��", context.getResources().getDrawable(R.drawable.face120));
//		map.put("��", context.getResources().getDrawable(R.drawable.face121));
//		map.put("����", context.getResources().getDrawable(R.drawable.face180));
//		map.put("�Ծ�", context.getResources().getDrawable(R.drawable.face182));
//		map.put("���Ƿ", context.getResources().getDrawable(R.drawable.face191));
//		map.put("Ǯ", context.getResources().getDrawable(R.drawable.face196));
//		map.put("��", context.getResources().getDrawable(R.drawable.face198));
//		map.put("����", context.getResources().getDrawable(R.drawable.face201));
//		map.put("˯��", context.getResources().getDrawable(R.drawable.face202));
//		map.put("��", context.getResources().getDrawable(R.drawable.face205));
//		map.put("�ɰ�", context.getResources().getDrawable(R.drawable.face208));
//		map.put("Χ��", context.getResources().getDrawable(R.drawable.face218));
//		map.put("����", context.getResources().getDrawable(R.drawable.face219));
//		map.put("������", context.getResources().getDrawable(R.drawable.face220));
//		map.put("����", context.getResources().getDrawable(R.drawable.face229));
//		map.put("����", context.getResources().getDrawable(R.drawable.face231));
//		map.put("����", context.getResources().getDrawable(R.drawable.face233));
//		map.put("����", context.getResources().getDrawable(R.drawable.face234));
//		map.put("����", context.getResources().getDrawable(R.drawable.face238));
//		map.put("ץ��", context.getResources().getDrawable(R.drawable.face239));
//		map.put("ŭ", context.getResources().getDrawable(R.drawable.face242));
//		map.put("͵Ц", context.getResources().getDrawable(R.drawable.face247));
//		map.put("��", context.getResources().getDrawable(R.drawable.face248));
//		map.put("ŭ��", context.getResources().getDrawable(R.drawable.face251));
//		map.put("����", context.getResources().getDrawable(R.drawable.face252));
//		map.put("�ڱ�ʺ", context.getResources().getDrawable(R.drawable.face253));
//		map.put("����", context.getResources().getDrawable(R.drawable.face254));
//		map.put("����", context.getResources().getDrawable(R.drawable.face255));
//		map.put("˼��", context.getResources().getDrawable(R.drawable.face257));
//		map.put("��", context.getResources().getDrawable(R.drawable.face258));
//		map.put("����", context.getResources().getDrawable(R.drawable.face261));
//		map.put("�Һߺ�", context.getResources().getDrawable(R.drawable.face263));
//		map.put("��ߺ�", context.getResources().getDrawable(R.drawable.face264));
//		map.put("ί��", context.getResources().getDrawable(R.drawable.face266));
//		map.put("����", context.getResources().getDrawable(R.drawable.face268));
//		map.put("����", context.getResources().getDrawable(R.drawable.face270));
//		map.put("Ү", context.getResources().getDrawable(R.drawable.face271));
//		map.put("��", context.getResources().getDrawable(R.drawable.face273));
//		map.put("��Ҫ", context.getResources().getDrawable(R.drawable.face274));
//		map.put("��", context.getResources().getDrawable(R.drawable.face277));
//		map.put("����", context.getResources().getDrawable(R.drawable.face278));
//		map.put("��", context.getResources().getDrawable(R.drawable.face279));
//		map.put("����", context.getResources().getDrawable(R.drawable.face280));
//		map.put("��ͷ", context.getResources().getDrawable(R.drawable.face281));
//		map.put("�ɸ���", context.getResources().getDrawable(R.drawable.face300));
//		map.put("��������", context.getResources().getDrawable(R.drawable.face311));
//		map.put("����style", context.getResources().getDrawable(R.drawable.face321));
	}
	
	private static Drawable getDrawable(String face, Context context)
	{
		Drawable drawable;
		drawable = map.get(face);
		if (drawable!=null) {
			drawable.setBounds(0, 0, context.getResources().getDimensionPixelOffset(R.dimen.text_normal)
					, context.getResources().getDimensionPixelOffset(R.dimen.text_normal));
		}
		return drawable;
	}
	
	public static synchronized String getTime(String time)
	{
		try {
			Date date;
			if (time!=null&&!time.equals("")) {
				date = formater.parse(time);
			}else {
				date = new Date(System.currentTimeMillis());
			}
			
			String s = getSpaceTimeDiffDescription(date.getTime());
			return s;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "无效时间";
	}
	
	/**
	 * 
	 * @param strTime ��λ��
	 * @return ��ǰʱ���Ҫ�Ƚϵ�ʱ���ʱ���
	 */
	public static String getSpaceTimeDiffDescription(Long strTime) {
		long d = System.currentTimeMillis();
		
		long l = d-strTime;
			long s =  l/ 1000;// ��
			long min = s / 60;// ��
			long hour = min / 60;// Сʱ
			long day = hour / 24;// ��
			long timeDiff5 = day / 30;// ��
			long timeDiff6 = timeDiff5 / 12;// ��

			String des = "";
			if ((0 <= s) && (s < 60)) {
				des = s + "秒钟前";
			} else if ((1 <= min) && (min < 60)) {
				des = min + "分钟前";
			} else if ((1 <= hour) && (hour < 24)) {
				des = hour + "小时前";
			} else if ((1 <= day)&&(day<30)) {
				des = day + "天前";
			} else if ((1 <= timeDiff5)&&(timeDiff5<12)) {
				des = timeDiff5 + "月前";
			} else if ((1 <= timeDiff6)) {
				des = timeDiff6 + "年前";
			} else {
				des = "0天以前";
			}
			   return des;
	}
	
	public static String setSource(String str)
	{
		int start = 0;
		int end = 0;
		boolean flag = false;
		
		for (int i = 0; i < str.length(); i++) {
			if ((!flag)&&str.charAt(i)=='>') {
				start = i+1;
				flag = true;
			}else if (flag&&(str.charAt(i)=='<')) {
				end = i;
			}
		}
		return str.substring(start, end);
	}
	
	/**
     * ɾ���ļ���
     * @param filePathAndName String �ļ���·������� ��c:/fqf
     * @param fileContent String
     * @return boolean
     */
    public static void delFolder(String folderPath) {
            try {
                    delAllFile(folderPath); //ɾ����������������
                    String filePath = folderPath;
                    filePath = filePath.toString();
                    java.io.File myFilePath = new java.io.File(filePath);
                    myFilePath.delete(); //ɾ����ļ���

            }
            catch (Exception e) {
                    System.out.println("ɾ���ļ��в�������");
                    e.printStackTrace();

            }
    }

    /**
     * ɾ���ļ�������������ļ�
     * @param path String �ļ���·�� �� c:/fqf
     */
    public static void delAllFile(String path) {
            File file = new File(path);
            if (!file.exists()) {
                    return;
            }
            if (!file.isDirectory()) {
           return;
            }
            String[] tempList = file.list();
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                    if (path.endsWith(File.separator)) {
                            temp = new File(path + tempList[i]);
                    }
                    else {
                            temp = new File(path + File.separator + tempList[i]);
                    }
                    if (temp.isFile()) {
                            temp.delete();
                    }
                    if (temp.isDirectory()) {
                            delAllFile(path+"/"+ tempList[i]);//��ɾ���ļ���������ļ�
                            delFolder(path+"/"+ tempList[i]);//��ɾ����ļ���
                    }
            }
    }
    
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }
    
    public static void showNetDialog(Context context)
    {
    	if (isShowingNetDiaLog) {
			return;
		}
    	isShowingNetDiaLog = true;
    	
    	final Context mContext = context;
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("û������");
		
		final AlertDialog alert = builder.create();
		alert.setButton("�˳�", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		alert.setButton2("���", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent=null;
                //�ж��ֻ�ϵͳ�İ汾  ��API����10 ����3.0�����ϰ汾 
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }else{
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                mContext.startActivity(intent);
                alert.dismiss();
                isShowingNetDiaLog = false;
			}
		});
		alert.show();
    }
}
