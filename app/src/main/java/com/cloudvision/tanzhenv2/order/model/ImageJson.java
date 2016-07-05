package com.cloudvision.tanzhenv2.order.model;

public class ImageJson {

	private long createDate;

	private String creator;

	private String description;

	private long id;

	private long identifier;

	private long modifyDate;

	private String name;

	private boolean persisted;

	private long type;

	private String url;

	public void setCreateDate(int createDate){
		this.createDate = createDate;
	}
	public long getCreateDate(){
		return this.createDate;
	}
	public void setCreator(String creator){
		this.creator = creator;
	}
	public String getCreator(){
		return this.creator;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public String getDescription(){
		return this.description;
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
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public void setPersisted(boolean persisted){
		this.persisted = persisted;
	}
	public boolean getPersisted(){
		return this.persisted;
	}
	public void setType(int type){
		this.type = type;
	}
	public long getType(){
		return this.type;
	}
	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return this.url;
	}
}
