package com.mge.tools.http;

import java.util.*;
import java.io.*;

import android.content.Context;

public class CMessage {
    //定义valtype
    private static final byte BYTE = 1;
    private static final byte SHORT = 2;
    private static final byte INT = 3;
    private static final byte LONG = 4;
    private static final byte STR = 5;
    private static final byte BYTEARR = 6;
    private static final byte SHORTARR = 7;
    private static final byte INTARR = 8;
    private static final byte LONGARR = 9;
    private static final byte STRARR = 10;

    //空对象定义
    public static final String STRNULL = "";
    private static final byte[] BYTEARRNULL = {};
    private static final short[] SHORTARRNULL = {};
    private static final int[] INTARRNULL = {};
    private static final long[] LONGARRNULL = {};
    private static final String[] STRARRNULL = {};
    private static final CMessage MSGNULL=new CMessage();
//    private final static String packagename="com.test";
    
//    public static Message getSettings(String filename)
//    {
//    	Message cfg;
//    	byte[] msgdata=ReadSettings(filename);
//    	if(msgdata==null)
//    	{
//    		cfg=new Message();
//    	}else
//    	{
//    		cfg=new Message();
//    		try {
//				cfg.Deserialize(msgdata);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//    	}
//    	return cfg;
//    }
//    public static void setSettings(String filename,Message cfg)
//    {
//		try {
//			byte[] data=cfg.Serialize();
//			WriteSettings(filename,data);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }
//    public static void delSettings(String filename) {
//		File file = null;
//		try {
//			file=new File("/data/data/"+packagename+"/"+filename);
//			if(file.exists())
//			{
//				file.delete();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
//    }
//	public static void WriteSettings(String filename,byte[] data) {
//		FileOutputStream fOut = null;
//		try {
//			fOut = new FileOutputStream("/data/data/"+packagename+"/"+filename);
//			if(fOut!=null){
//			fOut.write(data);
//			fOut.flush();}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(fOut!=null){
//			try {
//				fOut.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}}
//		}
//	}
//	
//	public static byte[] ReadSettings(String filename) {
//		FileInputStream fIn = null;
//
//		byte[] data = null;
//		try {
//			fIn = new FileInputStream("/data/data/"+packagename+"/"+filename);
//			if(fIn!=null){
//			int len=fIn.available();
//			data=new byte[len];
//			fIn.read(data);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(fIn!=null){
//			try {
//				fIn.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}}
//		}
//		return data;
//	}
    public static CMessage getSettings(Context context,String filename)
    {
    	CMessage cfg;
    	byte[] msgdata=ReadSettings(context,filename);
    	if(msgdata==null)
    	{
//    		System.out.println("getSettings Data==NULL");
    		cfg=new CMessage();
    	}else
    	{
//    		System.out.println("getSettings Data!=NULL");
    		cfg=new CMessage();
    		try {
				cfg.Deserialize(msgdata);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return cfg;
    }
    public static void setSettings(Context context,String filename,CMessage cfg)
    {
		try {
			byte[] data=cfg.Serialize();
			WriteSettings(context,filename,data);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void delSettings(Context context,String filename) {
		try {
			context.deleteFile(filename);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
	public static void WriteSettings(Context context,String filename,byte[] data) {
		FileOutputStream fOut = null;
		try {
			fOut=context.openFileOutput(filename,Context.MODE_WORLD_WRITEABLE);
//			fOut = new FileOutputStream("/data/data/"+packagename+"/"+filename);
			if(fOut!=null){
			fOut.write(data);
			fOut.flush();}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if(fOut!=null){
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		}
	}
	
	public static byte[] ReadSettings(Context context,String filename) {
		FileInputStream fIn = null;
		byte[] data = null;
		try {
			fIn=context.openFileInput(filename);
//			fIn = new FileInputStream("/data/data/"+packagename+"/"+filename);
			if(fIn!=null){
			int len=fIn.available();
			data=new byte[len];
			fIn.read(data);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if(fIn!=null){
			try {
				fIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		}
		return data;
	}
    private Hashtable values;
    private byte compress;
//    public long usid;
//    public short cmdid;
    public CMessage() {
        values = new Hashtable(512);
    }
    public int size()
    {
    	return values.size();
    }
    public void print() {
//        Enumeration keys = values.keys();
//        Enumeration items = values.elements();
//        while (items.hasMoreElements()) {
//            String key = (String) keys.nextElement();
//            Value value = (Value) items.nextElement();
//            System.out.println("key = " + key);
//            System.out.print("value = ");
//            printValue(value);
//        }
    }

    private static void printValue(Value value) {
        switch (value.type) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
        case STR:
            System.out.println(value.val);
            break;
        case BYTEARR:
            byte[] bobjs = (byte[]) value.val;
            for (int i = 0; i < bobjs.length; i++) {
                System.out.print(bobjs[i]);
                System.out.print("  ");
            }
            System.out.println();
            break;
        case SHORTARR:
            short[] sobjs = (short[]) value.val;
            for (int i = 0; i < sobjs.length; i++) {
                System.out.print(sobjs[i]);
                System.out.print("  ");
            }
            System.out.println();
            break;
        case INTARR:
            int[] iobjs = (int[]) value.val;
            for (int i = 0; i < iobjs.length; i++) {
                System.out.print(iobjs[i]);
                System.out.print("  ");
            }
            System.out.println();
            break;
        case LONGARR:
            long[] lobjs = (long[]) value.val;
            for (int i = 0; i < lobjs.length; i++) {
                System.out.print(lobjs[i]);
                System.out.print("  ");
            }
            System.out.println();
            break;
        case STRARR:
            String[] strobjs = (String[]) value.val;
            for (int i = 0; i < strobjs.length; i++) {
                System.out.print(strobjs[i]);
                System.out.print("  ");
            }
            System.out.println();
            break;
        }
    }

    private static Value readValue(DataInputStream dis) throws
            IOException {
        Value value = new Value();
        byte type = dis.readByte();
        value.type = type;
        switch (type) {
        case BYTE:
            byte bval = dis.readByte();
            value.val = new Byte(bval);
            break;
        case SHORT:
            short sval = dis.readShort();
            value.val = new Short(sval);
            break;
        case INT:
            int ival = dis.readInt();
            value.val = new Integer(ival);
            break;
        case LONG:
            long lval = dis.readLong();
            value.val = new Long(lval);
            break;
        case STR:
            value.val = dis.readUTF();
            break;
        case BYTEARR:
            byte[] barr = new byte[dis.readInt()];
            dis.read(barr);
            value.val = barr;
            break;
        case SHORTARR:
            short[] sarr = new short[dis.readInt()];
            for (int i = 0; i < sarr.length; i++) {
                sarr[i] = dis.readShort();
            }
            value.val = sarr;
            break;
        case INTARR:
            int[] iarr = new int[dis.readInt()];
            for (int i = 0; i < iarr.length; i++) {
                iarr[i] = dis.readInt();
            }
            value.val = iarr;
            break;
        case LONGARR:
            long[] larr = new long[dis.readInt()];
            for (int i = 0; i < larr.length; i++) {
                larr[i] = dis.readLong();
            }
            value.val = larr;
            break;
        case STRARR:
            String[] strarr = new String[dis.readInt()];
            for (int i = 0; i < strarr.length; i++) {
                strarr[i] = dis.readUTF();
            }
            value.val = strarr;
            break;
        }
        return value;
    }

    private static void writeValue(DataOutputStream dos, Value value) throws
            IOException {
        byte type = value.type;
        Object val = value.val;
        dos.writeByte(type); //写入item的类型
        switch (type) {
        case BYTE:
            dos.writeByte(((Byte) val).byteValue());
            break;
        case SHORT:
            dos.writeShort(((Short) val).shortValue());
            break;
        case INT:
            dos.writeInt(((Integer) val).intValue());
            break;
        case LONG:
            dos.writeLong(((Long) val).longValue());
            break;
        case STR:
            dos.writeUTF((String) val);
            break;
        case BYTEARR:
            byte[] barr = (byte[]) val;
            dos.writeInt(barr.length);
            dos.write(barr);
            break;
        case SHORTARR:
            short[] sarr = (short[]) val;
            dos.writeInt(sarr.length);
            for (int i = 0; i < sarr.length; i++) {
                dos.writeShort(sarr[i]);
            }
            break;
        case INTARR:
            int[] iarr = (int[]) val;
            dos.writeInt(iarr.length);
            for (int i = 0; i < iarr.length; i++) {
                dos.writeInt(iarr[i]);
            }
            break;
        case LONGARR:
            long[] larr = (long[]) val;
            dos.writeInt(larr.length);
            for (int i = 0; i < larr.length; i++) {
                dos.writeLong(larr[i]);
            }
            break;
        case STRARR:
            String[] strarr = (String[]) val;
            dos.writeInt(strarr.length);
            for (int i = 0; i < strarr.length; i++) {
                dos.writeUTF(strarr[i]);
            }
            break;
        }
    }

    //序列化为字节流
    public byte[] Serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        DataOutputStream dos = new DataOutputStream(baos);
        Serialize(dos);
        byte[] data = baos.toByteArray();
        byte[] out = null; //经压缩流程处理过的数据
//        if (compress == 1) {
//            out = TOOLS.jzlib_compress(data, 9);
//        } else {
            out = data;
//        }
        byte[] ret = new byte[out.length + 5];
        int val = out.length;
        ret[0] = (byte) ((val & 0xff000000) >> 24);
        ret[1] = (byte) ((val & 0xff0000) >> 16);
        ret[2] = (byte) ((val & 0xff00) >> 8);
        ret[3] = (byte) (val & 0xff);
        ret[4] = compress;
        System.arraycopy(out, 0, ret, 5, out.length);
        return ret;
    }

    //反序列化字节流
    public void Deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        int len = dis.readInt();
        byte compress = dis.readByte();
        byte[] out = null; //经解压缩流程处理过的数据
//        if (compress == 1) {
//            byte[] cdata = new byte[len];
//            dis.read(cdata);
//            out = TOOLS.jzlib_decompress(cdata);
//        } else {
            byte[] cdata = new byte[len];
            dis.read(cdata);
            out = cdata;
//        }
        this.compress = compress;
        Deserialize(new DataInputStream(new ByteArrayInputStream(out)));
    }

    //将自己序列化
    public void Serialize(DataOutputStream dos) throws IOException {
        Enumeration keys = values.keys();
        Enumeration items = values.elements();
        //写入value数量
        dos.writeInt(values.size());
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Value value = (Value) items.nextElement();
            dos.writeUTF(key); //写入key
            writeValue(dos, value); //写入value
        }
    }

    //反序列化
    public void Deserialize(DataInputStream dis) throws IOException {
        //读取value的个数
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            String key = dis.readUTF(); //读取key
            Value value = readValue(dis); //读取value
            values.put(key, value);
        }
    }

    public void putByte(String key, long val) {
        key = key.toLowerCase();
        Value bv = new Value(BYTE, new Byte((byte) val));
        values.put(key, bv);
    }

    public byte getByte(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return 0;
        }
        if (val.type == BYTE) {
            return ((Byte) val.val).byteValue();
        } else {
            return 0;
        }
    }

    public void putShort(String key, long val) {
        key = key.toLowerCase();
        Value sv = new Value(SHORT, new Short((short) val));
        values.put(key, sv);
    }

    public short getShort(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return 0;
        }
        if (val.type == SHORT) {
            return ((Short) val.val).shortValue();
        } else {
            return 0;
        }
    }

    public void putInt(String key, long val) {
        key = key.toLowerCase();
        Value iv = new Value(INT, new Integer((int) val));
        values.put(key, iv);
    }

    public int getInt(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return 0;
        }
        if (val.type == INT) {
            return ((Integer) val.val).intValue();
        } else {
            return 0;
        }
    }
    public void putLong(String key, long val) {
        key = key.toLowerCase();
        Value lv = new Value(LONG, new Long(val));
        values.put(key, lv);
    }
    
    public long getLong(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return 0;
        }
        if (val.type == LONG) {
            return ((Long) val.val).longValue();
        } else {
            return 0;
        }
    }

    public void putStr(String key, String val) {
        key = key.toLowerCase();
        Value iv;
        if (val == null) {
            iv = new Value(STR, STRNULL);
        } else {
            iv = new Value(STR, new String(val));
        }
        values.put(key, iv);
    }

    public String getStr(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return STRNULL;
        }
        if (val.type == STR) {
            return (String) val.val;
        } else {
            return STRNULL;
        }
    }

    public Object arraycopy(Object array) {
        Object tmp = null;
        if (array instanceof byte[]) {
            byte[] arr = (byte[]) array;
            tmp = new byte[arr.length];
            System.arraycopy(array, 0, tmp, 0, arr.length);
        } else if (array instanceof short[]) {
            short[] arr = (short[]) array;
            tmp = new short[arr.length];
            System.arraycopy(array, 0, tmp, 0, arr.length);
        } else if (array instanceof int[]) {
            int[] arr = (int[]) array;
            tmp = new int[arr.length];
            System.arraycopy(array, 0, tmp, 0, arr.length);
        } else if (array instanceof long[]) {
            long[] arr = (long[]) array;
            tmp = new long[arr.length];
            System.arraycopy(array, 0, tmp, 0, arr.length);
        } else if (array instanceof String[]) {
            String[] arr = (String[]) array;
            tmp = new String[arr.length];
            System.arraycopy(array, 0, tmp, 0, arr.length);
        }
        return tmp;
    }

    public void putByteArray(String key, byte[] array) {
        key = key.toLowerCase();
        Value v;
        if (array == null) {
            v = new Value(BYTEARR, BYTEARRNULL);
        } else {
            v = new Value(BYTEARR, arraycopy(array));
        }
        values.put(key, v);
    }

    public byte[] getByteArray(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return BYTEARRNULL;
        }
        if (val.type == BYTEARR) {
            return (byte[]) val.val;
        } else {
            return BYTEARRNULL;
        }
    }

    public void putShortArray(String key, short[] array) {
        key = key.toLowerCase();
        Value v;
        if (array == null) {
            v = new Value(SHORTARR, SHORTARRNULL);
        } else {
            v = new Value(SHORTARR, arraycopy(array));
        }
        values.put(key, v);
    }

    public short[] getShortArray(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return SHORTARRNULL;
        }
        if (val.type == SHORTARR) {
            return (short[]) val.val;
        } else {
            return SHORTARRNULL;
        }
    }

    public void putIntArray(String key, int[] array) {
        key = key.toLowerCase();
        Value v;
        if (array == null) {
            v = new Value(INTARR, INTARRNULL);
        } else {
            v = new Value(INTARR, arraycopy(array));
        }
        values.put(key, v);
    }

    public int[] getIntArray(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return INTARRNULL;
        }
        if (val.type == INTARR) {
            return (int[]) val.val;
        } else {
            return INTARRNULL;
        }
    }

    public void putLongArray(String key, long[] array) {
        key = key.toLowerCase();
        Value v;
        if (array == null) {
            v = new Value(LONGARR, LONGARRNULL);
        } else {
            v = new Value(LONGARR, arraycopy(array));
        }
        values.put(key, v);
    }

    public long[] getLongArray(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return LONGARRNULL;
        }
        if (val.type == LONGARR) {
            return (long[]) val.val;
        } else {
            return LONGARRNULL;
        }
    }

    public void putStrArray(String key, String[] array) {
        key = key.toLowerCase();
        Value v;
        if (array == null) {
            v = new Value(STRARR, STRARRNULL);
        } else {
            v = new Value(STRARR, arraycopy(array));
        }
        values.put(key, v);
    }


    public String[] getStrArray(String key) {
        key = key.toLowerCase();
        Value val = (Value) values.get(key);
        if (val == null) {
            return STRARRNULL;
        }
        if (val.type == STRARR) {
            return (String[]) val.val;
        } else {
            return STRARRNULL;
        }
    }
//    public void putVal(String key, long val, byte type) {
//        Value v;
//        if (type == BYTE) {
//            v = new Value(BYTE, new Byte((byte) val));
//            values.put(key, v);
//        } else if (type == SHORT) {
//            v = new Value(SHORT, new Short((short) val));
//            values.put(key, v);
//        } else if (type == INT) {
//            v = new Value(INT, new Integer((int) val));
//            values.put(key, v);
//        } else if (type == LONG) {
//            v = new Value(LONG, new Long(val));
//            values.put(key, v);
//        } else {
//            System.out.println("type Error");
//        }
//    }
//
//    public long getVal(String key) {
//        Value val = (Value) values.get(key);
//        if (val.type == BYTE) {
//            return ((Byte) val.val).byteValue();
//        } else if (val.type == SHORT) {
//            return ((Short) val.val).shortValue();
//        } else if (val.type == INT) {
//            return ((Integer) val.val).intValue();
//        } else if (val.type == LONG) {
//            return ((Long) val.val).longValue();
//        } else {
//            return 0;
//        }
//    }
}
class Value {
    public byte type;
    public Object val;
    public Value() {
    }

    public Value(byte type, Object val) {
        this.type = type;
        this.val = val;
    }

}
