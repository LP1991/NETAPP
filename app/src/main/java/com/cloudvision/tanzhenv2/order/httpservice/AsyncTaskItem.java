/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudvision.tanzhenv2.order.httpservice;


/**
* 描述：数据执行单??
* @version v1.0
*/
public class AsyncTaskItem { 
	
	public long timestamp;
	
	/** 记录的当前索?? */
	public int position;
	 
	/** 执行完成的回调接?? */
	public AsyncTaskCallback callback; 
	public Object param;
 
	public AsyncTaskItem setCallback(AsyncTaskCallback callback){
 	this.callback=callback;
 	return this;
	}
} 

