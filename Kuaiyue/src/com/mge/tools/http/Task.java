package com.mge.tools.http;

import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

import android.app.Notification;

public class Task {
	public String downloading_url;// ����·��ITEM�ṹ�е�downloading_url
	public int filelen;// �ļ�����ITEM�ṹ�е�size
	
//	public String id;// ITEM�ṹ�е�id
//	public int downloading_id;// ����·��ITEM�ṹ�е�downloading_url
//	public String icon_url;// ͼ��·��ITEM�ṹ�е�icon_url
	public String appName;// ������
//
//	public String package_name="";//�����ɷ������ṩ
//	public String version="";//�汾���ɷ������ṩ
//	public String dev; //��������Ϣ
//	public int versionCode;
	
	// ���³�Ա��Ϊ�����в���
	public String filename; // �ļ������ش洢���ļ���
	public boolean DOWNLOADFINAL; // �Ƿ�������ϴ����ع���
	public byte TaskType; // ��������
	public byte[] TYPES; // ����ϸ��
	public boolean TaskState;// ����״̬ false����״̬ true��ͣ״̬
	public HttpWorkThreadManager thread;
	public int downsize = 0;
	public int part;// ����ʱ����
	public int nid;
	public Notification notification;
	public Task(String name,String downloading_url,int filelen) {
//		this.id = id;
		this.downloading_url = downloading_url;
//		this.icon_url = icon_url;
		this.filelen = filelen;
		this.appName = name;
//		this.downloading_id=downid;
		this.filename = getFileName(downloading_url);
	}

	public String getFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

	public String toString() {
		return filename + "-" + filelen + "-" + TaskType + "-" + DOWNLOADFINAL;
	}

	public Task(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		try {
//			package_name = dis.readUTF();
//			version=dis.readUTF();
//			versionCode=dis.readInt();
//			id = dis.readUTF();
			nid=dis.readInt();
//			downloading_id=dis.readInt();
			downloading_url = dis.readUTF();
//			icon_url = dis.readUTF();
			appName = dis.readUTF();
//			dev=dis.readUTF();
			filename = dis.readUTF();
			filelen = dis.readInt();
			DOWNLOADFINAL = dis.readBoolean();
			TaskType = dis.readByte();
			TaskState = dis.readBoolean();
			downsize = dis.readInt();
			int size = dis.readInt();
			if (size != 0) {
				TYPES = new byte[size];
				dis.read(TYPES);
			}
			if (DOWNLOADFINAL == false && TYPES != null) {
				for (int i = 0; i < TYPES.length; i++) {
//					System.out.print(" _ " + TYPES[i]);
				}
//				System.out.println("");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				dis.close();
			} catch (IOException ex2) {
			}
			try {
				bais.close();
			} catch (IOException ex1) {
			}
		}
	}

	public byte[] getBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
//			dos.writeUTF(package_name);
//			dos.writeUTF(version);
//			dos.writeInt(versionCode);
//			dos.writeUTF(id);
			dos.writeInt(nid);
//			dos.writeInt(downloading_id);
			dos.writeUTF(downloading_url);
//			dos.writeUTF(icon_url);
			dos.writeUTF(appName);
//			dos.writeUTF(dev);
			dos.writeUTF(filename);
			dos.writeInt(filelen);
			dos.writeBoolean(DOWNLOADFINAL);
			dos.writeByte(TaskType);
			dos.writeBoolean(TaskState);
			dos.writeInt(downsize);
			if (TYPES == null) {
				dos.writeInt(0);
			} else {
				dos.writeInt(TYPES.length);
				dos.write(TYPES);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (Exception ex) {
			}
			try {
				baos.close();
			} catch (IOException ex1) {
			}
		}
		return baos.toByteArray();
	}

}
