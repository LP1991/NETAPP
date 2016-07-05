package com.cloudvision.util;

import android.content.Context;
import android.util.Xml;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.tanzhenv2.model.ChannelInfo;
import com.cloudvision.tanzhenv2.order.model.TemplateElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Xmlparse{
	
	private static final String TAG = "-----XMLParse-----";
		
	private Context context = null;
	public Xmlparse(Context _context)
	{ 
		this.context = _context;
	} 
	
	public String readXMLDOMTemplateVersion(String fileName)
	{
		InputStream is = null;
		String ver = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		String path = AppConfig.CacheConfigDir+ "/"+fileName;
		
		try {
			is = new FileInputStream(path);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("versionNumber");
			int length = items.getLength();
			for ( int i = 0; i < length; i++ )
			{
				Node childNode = items.item(i);
				if ( childNode.getNodeType() == Node.ELEMENT_NODE )
				{
					Element elemNode = (Element)childNode;
					ver = elemNode.getFirstChild().getNodeValue();
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if ( is != null )
			{
				try{
				is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return ver;
	}
	
	public Template readXMLDOMTemplate(String fileName)
	{
//		List<TemplateCriteria> lsTc = new ArrayList<TemplateCriteria>();
		Template template = new Template();
		InputStream is = null; 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		String path = "";
		path = AppConfig.CacheConfigDir+ "/"+fileName;
	    
		template.templateVersion = readXMLDOMTemplateVersion("template.xml");
		
		try{
			is = new FileInputStream(path);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("criteria");
			int length1 = items.getLength();
			for ( int i = 0; i < length1; i++ )
			{
				TemplateElement te = new TemplateElement();
				Element node = (Element) items.item(i);
				
				NodeList childsNodes = node.getChildNodes();
				int length2 = 0;
				length2 = childsNodes.getLength();
				for ( int j = 0; j < length2; j++ )
				{
					Node nodeChild = (Node) childsNodes.item(j);
					
					if ( nodeChild.getNodeType() == Node.ELEMENT_NODE )
					{
						Element childNode = (Element) nodeChild;
						if ("criteriaName".equals(childNode.getNodeName()))
						{
							te.elementName = childNode.getFirstChild().getNodeValue();
						} 
						else if ("level".equals(childNode.getNodeName()))
						{
							te.elementLevel = childNode.getFirstChild().getNodeValue();
						}
						else if ("caps".equals(childNode.getNodeName()))
						{
							te.caps = childNode.getFirstChild().getNodeValue();
						}
						else if ("lower".equals(childNode.getNodeName()))
						{
							te.lower = childNode.getFirstChild().getNodeValue();
						}						
					}
				}
				template.lsTe.add(te);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if ( is != null )
			{
				try{
				is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
		return template;
	}
	
	public List<ChannelInfo> readXMLDOMChannelInfoForQuickScan(String fileName)
	{
		InputStream is = null;
		List<ChannelInfo> channelInfoList = new ArrayList<ChannelInfo>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		String path = "";
		path = AppConfig.CacheConfigDir+ "/"+fileName;
		
		try {
			is = new FileInputStream(path);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("channel");
			int length = items.getLength();
			for ( int i = 0; i < length; i++ )
			{
				ChannelInfo ci = new ChannelInfo();
				Element node = (Element)items.item(i);
				
				NodeList childsNodes = node.getChildNodes();
				int length2 = 0;
				length2 = childsNodes.getLength();
				for ( int j = 0; j < length2; j++ )
				{
					Node nodeChild = (Node) childsNodes.item(j);
					if ( nodeChild.getNodeType() == Node.ELEMENT_NODE )
					{
						Element childNode = (Element) nodeChild;
						if ("centFrequency".equals(childNode.getNodeName()))
						{
							ci.centFrequency = childNode.getFirstChild().getNodeValue();
							if ( ci.centFrequency.indexOf(".0") != -1 )
							{
								ci.centFrequency = ci.centFrequency.substring(0, ci.centFrequency.indexOf("."));
							}
							MyLog.e(fileName, ci.centFrequency);
						}if ("number".equals(childNode.getNodeName())){
							ci.number =  childNode.getFirstChild().getNodeValue();
						}if ("name".equals(childNode.getNodeName())){
							ci.channelName =  childNode.getFirstChild().getNodeValue();
						}if ("isChange".equals(childNode.getNodeName())){
							ci.isChange =  childNode.getFirstChild().getNodeValue();
						}if ("channelType".equals(childNode.getNodeName())){
							ci.channelType =  childNode.getFirstChild().getNodeValue();
						}if ("programNumber".equals(childNode.getNodeName())){
							ci.programNum =  childNode.getFirstChild().getNodeValue();
						}
					}
				}
				channelInfoList.add(ci); 
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if ( is != null )
			{
				try{
				is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return channelInfoList;
	}

	public List<ChannelInfo> readXMLDOMChannelInfo(String fileName, String isChange)
	{
		InputStream is = null;
		List<ChannelInfo> channelInfoList = new ArrayList<ChannelInfo>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		String path = "";
		path = AppConfig.CacheConfigDir+ "/"+fileName;
		
		try {
			is = new FileInputStream(path);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("channel");
			int length = items.getLength();
			for ( int i = 0; i < length; i++ )
			{
				ChannelInfo ci = new ChannelInfo();
				Element node = (Element)items.item(i);
				
				NodeList childsNodes = node.getChildNodes();
				int length2 = 0;
				length2 = childsNodes.getLength();
				for ( int j = 0; j < length2; j++ )
				{
					Node nodeChild = (Node) childsNodes.item(j);
					if ( nodeChild.getNodeType() == Node.ELEMENT_NODE )
					{
						Element childNode = (Element) nodeChild;
						if ("centFrequency".equals(childNode.getNodeName()))
						{
							ci.centFrequency = childNode.getFirstChild().getNodeValue();
							if ( ci.centFrequency.indexOf(".0") != -1 )
							{
								ci.centFrequency = ci.centFrequency.substring(0, ci.centFrequency.indexOf("."));
							}
						}if ("number".equals(childNode.getNodeName())){
							ci.number =  childNode.getFirstChild().getNodeValue();
						}if ("name".equals(childNode.getNodeName())){
							ci.channelName =  childNode.getFirstChild().getNodeValue();
						}if ("isChange".equals(childNode.getNodeName())){
							ci.isChange =  childNode.getFirstChild().getNodeValue();
						}if ("channelType".equals(childNode.getNodeName())){
							ci.channelType =  childNode.getFirstChild().getNodeValue();
						}if ("programNumber".equals(childNode.getNodeName())){
							ci.programNum =  childNode.getFirstChild().getNodeValue();
						}
					}
				}
				//判断网络类型：整转网，非整转网
				if ( isChange.equals(ci.isChange) )
				{ 
					channelInfoList.add(ci);
				} 
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if ( is != null )
			{
				try{
				is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return channelInfoList;
	}

	public String getFileVersion(String fileName)
	{
		String ver = "";
		try
		{
			InputStream xml = new FileInputStream(fileName); 
			// 由android.util.Xml创建一个XmlPullParser实例
			XmlPullParser parser = Xml.newPullParser();    
			//解析文件，设置字符集
			parser.setInput(xml, "UTF-8"); 
			// 产生第一个事件
			int eventType = parser.getEventType();       
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
		            // 判断当前事件是否为文档开始事件
		            case XmlPullParser.START_DOCUMENT:
		            	// 初始化EocTestCheck
		            	break;
	            	// 判断当前事件是否为标签元素开始事件
		            case XmlPullParser.START_TAG:
		            	if (parser.getName().equals("version")) {
		            		ver = (String)parser.nextText();
		            		return ver;
		                }
		            	break;
		            // 判断当前事件是否为标签元素结束事件
		            case XmlPullParser.END_TAG:
		            	break;
				}
				// 进入下一个元素并触发相应事件
	            eventType = parser.next();
			}
		}
		catch(Exception ex)
		{
			MyLog.e("sax error", "error");
		}
		return ver;
	}

	
}
