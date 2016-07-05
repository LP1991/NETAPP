package com.cloudvision.tanzhenv2.order.model;

public class UpdateJson {

	private long appType;

	private long createDate;

	private String description;

	private String fileName;

	private String filePath;

	private long id;

	private long identifier;

	private long modifyDate;

	private boolean persisted;

	private String versionNum;
	
	private String md5Check;

	public void setAppType(int appType){
	this.appType = appType;
	}
	public long getAppType(){
	return this.appType;
	}
	public void setCreateDate(int createDate){
	this.createDate = createDate;
	}
	public long getCreateDate(){
	return this.createDate;
	}
	public void setDescription(String description){
	this.description = description;
	}
	public String getDescription(){
	return this.description;
	}
	public void setFileName(String fileName){
	this.fileName = fileName;
	}
	public String getFileName(){
	return this.fileName;
	}
	public void setFilePath(String filePath){
	this.filePath = filePath;
	}
	public String getFilePath(){
	return this.filePath;
	}
	public void setId(int id){
	this.id = id;
	}
	public long getId(){
	return this.id;
	}
	public void setIdentifier(int identifier){
	this.identifier = identifier;
	}
	public long getIdentifier(){
	return this.identifier;
	}
	public void setModifyDate(int modifyDate){
	this.modifyDate = modifyDate;
	}
	public long getModifyDate(){
	return this.modifyDate;
	}
	public void setPersisted(boolean persisted){
	this.persisted = persisted;
	}
	public boolean getPersisted(){
	return this.persisted;
	}
	public void setVersionNum(String versionNum){
	this.versionNum = versionNum;
	}
	public String getVersionNum(){
	return this.versionNum;
	}
	
	public void setMd5(String md5Check){
	this.md5Check = md5Check;
	}
	public String getMd5(){
	return this.md5Check;
	}
}
