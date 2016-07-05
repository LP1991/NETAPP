package com.cloudvision.tanzhenv2.order.model;

public class TemplateCriteria {
	public String criteriaName;
	public String videoLevelMin;
	public String videoLevelMax;
	public String audeoLevelMin;
	public String audeoLevelMax;
	public String avRateMin;
	public String avRateMax;
	public String cnrMin;
	public String cnrMax;
	public String merMin;
	public String merMax;
	public String channelPowerMin;
	public String channelPowerMax;
	public String errBitBeforeMin;
	public String errBitBeforeMax;
	public String errBitAfterMin;
	public String errBitAfterMax;
	
	public TemplateCriteria()
	{
		this.criteriaName = "";
		this.videoLevelMin = "";
		this.videoLevelMax = "";
		this.audeoLevelMin = "";
		this.audeoLevelMax = "";
		this.avRateMin = "";
		this.avRateMax = "";
		this.cnrMin = "";
		this.cnrMax = "";
		this.merMin = "";
		this.merMax = "";
		this.channelPowerMin = ""; 
		this.channelPowerMax = ""; 
		this.errBitBeforeMin = "";
		this.errBitBeforeMax = "";
		this.errBitAfterMin = "";
		this.errBitAfterMax = "";
	}
}
