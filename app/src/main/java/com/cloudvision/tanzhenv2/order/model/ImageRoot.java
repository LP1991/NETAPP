package com.cloudvision.tanzhenv2.order.model;

import java.util.List;

public class ImageRoot {
	
	private List<ImageJson> imgs ;

	public void setImgs(List<ImageJson> imgs){
		this.imgs = imgs;
	}
	public List<ImageJson> getImgs(){
		return this.imgs;
	}

}
