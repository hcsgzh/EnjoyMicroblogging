package com.aisidi.iweibo.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class SaxXmlTest {

	public static List<JoyreadBean> getXmlList(InputStream is) throws ParserConfigurationException, SAXException, IOException
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		
		XmlParserHandler handler = new XmlParserHandler();
		
		saxParser.parse(is, handler);
		
		return handler.getListFromXML();
	}
}
