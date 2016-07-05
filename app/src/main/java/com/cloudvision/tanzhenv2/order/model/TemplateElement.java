package com.cloudvision.tanzhenv2.order.model;

public class TemplateElement {
	
	public String elementName;	/*指标名称*/
	public String elementLevel; /*所述级别， 0：光站，1：放大器，2：小区*/
	public String caps;
	public String lower;
	
	public TemplateElement()
	{
		this.elementName = "";
		this.elementLevel = "";
		this.caps = "";
		this.lower = "";
	}
}
