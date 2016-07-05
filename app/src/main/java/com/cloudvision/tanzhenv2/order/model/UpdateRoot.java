package com.cloudvision.tanzhenv2.order.model;

public class UpdateRoot {

	private UpdateJson appVersion;
	
	private UpdateJson softVersion;
	
	private UpdateJson probeConfig;

	private String curVersion;

	private boolean needUpdate;
	
	public void setAppVersion(UpdateJson appVersion){
	this.appVersion = appVersion;
	}
	public UpdateJson getAppVersion(){
	return this.appVersion;
	}
	
	public void setSoftVersion(UpdateJson softVersion){
		this.softVersion = softVersion;
	}
	public UpdateJson getSoftVersion(){
		return this.softVersion;
	}
	
	public void setProbeConfig(UpdateJson probeConfig){
		this.probeConfig = probeConfig;
	}
	public UpdateJson getProbeConfig(){
		return this.probeConfig;
	}
	
	public void setCurVersion(String curVersion){
	this.curVersion = curVersion;
	}
	public String getCurVersion(){
	return this.curVersion;
	}
	public void setNeedUpdate(boolean needUpdate){
	this.needUpdate = needUpdate;
	}
	public boolean getNeedUpdate(){
	return this.needUpdate;
	}
}
