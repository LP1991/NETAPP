package com.cloudvision.tanzhenv2.order.model;

public class SpeedTestRoot {

	private AppWifiSpeed appWifiSpeed;

	public void setAppWifiSpeed(AppWifiSpeed appWifiSpeed){
	this.appWifiSpeed = appWifiSpeed;
	}
	public AppWifiSpeed getAppWifiSpeed(){
	return this.appWifiSpeed;
	}
	
	public class AppWifiSpeed {
		private String apkSize;

		private int id;

		private int identifier;

		private String inDownUrl;

		private String inUpUrl;

		private String middleName;

		private String middleSpeed;

		private String musicSize;

		private String outDownUrl;

		private String outUpUrl;

		private boolean persisted;

		private String quickName;

		private String quickSpeed;

		private String slowName;

		private String slowSpeed;

		private String videoSize;

		public void setApkSize(String apkSize){
		this.apkSize = apkSize;
		}
		public String getApkSize(){
		return this.apkSize;
		}
		public void setId(int id){
		this.id = id;
		}
		public int getId(){
		return this.id;
		}
		public void setIdentifier(int identifier){
		this.identifier = identifier;
		}
		public int getIdentifier(){
		return this.identifier;
		}
		public void setInDownUrl(String inDownUrl){
		this.inDownUrl = inDownUrl;
		}
		public String getInDownUrl(){
		return this.inDownUrl;
		}
		public void setInUpUrl(String inUpUrl){
		this.inUpUrl = inUpUrl;
		}
		public String getInUpUrl(){
		return this.inUpUrl;
		}
		public void setMiddleName(String middleName){
		this.middleName = middleName;
		}
		public String getMiddleName(){
		return this.middleName;
		}
		public void setMiddleSpeed(String middleSpeed){
		this.middleSpeed = middleSpeed;
		}
		public String getMiddleSpeed(){
		return this.middleSpeed;
		}
		public void setMusicSize(String musicSize){
		this.musicSize = musicSize;
		}
		public String getMusicSize(){
		return this.musicSize;
		}
		public void setOutDownUrl(String outDownUrl){
		this.outDownUrl = outDownUrl;
		}
		public String getOutDownUrl(){
		return this.outDownUrl;
		}
		public void setOutUpUrl(String outUpUrl){
		this.outUpUrl = outUpUrl;
		}
		public String getOutUpUrl(){
		return this.outUpUrl;
		}
		public void setPersisted(boolean persisted){
		this.persisted = persisted;
		}
		public boolean getPersisted(){
		return this.persisted;
		}
		public void setQuickName(String quickName){
		this.quickName = quickName;
		}
		public String getQuickName(){
		return this.quickName;
		}
		public void setQuickSpeed(String quickSpeed){
		this.quickSpeed = quickSpeed;
		}
		public String getQuickSpeed(){
		return this.quickSpeed;
		}
		public void setSlowName(String slowName){
		this.slowName = slowName;
		}
		public String getSlowName(){
		return this.slowName;
		}
		public void setSlowSpeed(String slowSpeed){
		this.slowSpeed = slowSpeed;
		}
		public String getSlowSpeed(){
		return this.slowSpeed;
		}
		public void setVideoSize(String videoSize){
		this.videoSize = videoSize;
		}
		public String getVideoSize(){
		return this.videoSize;
		}

		}
}
