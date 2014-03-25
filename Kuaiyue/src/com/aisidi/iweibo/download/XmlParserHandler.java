package com.aisidi.iweibo.download;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XmlParserHandler extends DefaultHandler{
	private static final String ROOT = "joyread";
	private static final String VERSIONCODE = "VersionCode";
	private static final String DESCRIPTION = "VersionDescription";
	private static final String DOWNLOADURL = "DownloadURL";
	private static final String VERSIONNAME = "VersionName";
	private static final String APKSIZE = "DownloadSize";
	
	private List<JoyreadBean> jBeanList = new ArrayList<JoyreadBean>();
	private JoyreadBean jBean = null;
	private String currentTag = null;
	
	public List<JoyreadBean> getListFromXML()
	{
		return jBeanList;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub

		if(ROOT.equals(localName))
		{
			jBean = new JoyreadBean();
			
			
		}
		currentTag = localName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		if(currentTag!=null)
		{
			String data = new String(ch, start, length);
			if (currentTag.equals(VERSIONCODE)) {
				jBean.setVersionCode(Integer.parseInt(data));
			}else if (currentTag.equals(DESCRIPTION)) {
				jBean.setVersionDescription(data);
			}else if (currentTag.equals(DOWNLOADURL)) {
				jBean.setDownloadURL(data);
			}else if (currentTag.equals(VERSIONNAME)) {
				jBean.setVersionName(data);
			}else if (currentTag.equals(APKSIZE)) {
				jBean.setApkSize(Integer.parseInt(data));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		currentTag = null;
		if(ROOT.endsWith(localName))
		{
			jBeanList.add(jBean);
			jBean = null;
		}
	}

}
