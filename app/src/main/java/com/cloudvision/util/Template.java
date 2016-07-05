package com.cloudvision.util;

/**
* 文 件 名 :Template.java
* CopyRright:cvnchina
* 创 建 人：liweiqiang
* 日 期：2013-06-12
* 描 述：模板类，定义各个节点的模板，例如：用户终端，光节点，放大器等。
*/

import com.cloudvision.tanzhenv2.order.model.TemplateElement;

import java.util.ArrayList;
import java.util.List;


public class Template {
	public String templateVersion;
	public List<TemplateElement> lsTe;
	
	public Template()
	{ 
		this.templateVersion = ""; 
		this.lsTe = new ArrayList<TemplateElement>();
	} 
}
