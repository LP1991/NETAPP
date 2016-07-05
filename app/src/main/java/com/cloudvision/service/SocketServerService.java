package com.cloudvision.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cloudvision.tanzhenv2.application.ContextUtil;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServerService extends Service {

	private String TAG = "SocketServerService";
	private static ArrayList<Socket> socketList = new ArrayList<Socket>();
	private ServerSocket serverSocket;
	private ContextUtil contextUtil;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		MyLog.e("SocketServerService", "service started");
		contextUtil = ContextUtil.getInstance_o();
		contextUtil.connecrService = true;
			
		try{
			new Thread(new ServerThread()).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			serverSocket.close();
			contextUtil.connecrService = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("SocketServerService", "onDestroy");
	}
	
	private void connectRouter()
	{
		Log.e("connectRouterWithApMode", "router接收线程开启");
	}
	
	private Boolean checkIfRouterConnected(String buffer)
	{
		if(buffer.contains("A1000"))
		{
			String []arrA1000 = buffer.split("&");
			if(arrA1000.length == 3)
			{
				String appIp = arrA1000[1];
				if(appIp.length()>0)
				{
					SPUtils.put(this, "appIp", appIp);
					MyLog.e(TAG, "手机AP的地址"+appIp);
				}
				String routerIp = arrA1000[2];
				if(routerIp.length()>0)
				{
					SPUtils.put(this, "routerIp", routerIp);
					MyLog.e(TAG, "路由的地址"+routerIp);
				}
				return true;
			}
		}
		return false;
	}
	
	public class ServerThread implements Runnable{
		
		public ServerThread(){

		}		
		
		public void run(){	
			try{
				serverSocket = new ServerSocket(6790);
				while(true){
					Socket acceptSocket = serverSocket.accept();
					InputStream inputStream = acceptSocket.getInputStream();
					byte data[] = new byte[1024];
					int i = 0;
					while((i = inputStream.read(data))!=-1){
						String buffer = null;
						buffer = new String(data,"gbk");
//						buffer = buffer.trim() + "\n";
						buffer = buffer.trim();
						MyLog.e("SocketServerService", buffer);
					}
					if(acceptSocket!=null)
					{
						inputStream.close();
						acceptSocket.close();
//						MyLog.e("acceptSocket", "close");
					}
				}
					
			}catch(IOException e){
				e.printStackTrace();
			}
		}

	}
	
	
//	public class ServerThread implements Runnable{
//
//		private Socket socket = null;
//		
//		public ServerThread(){
//
//		}		
//		
//		public void run(){			
//		
//			try{
//				serverSocket = new ServerSocket(6790);
//				while(true){
//					socket = serverSocket.accept();
////					socketList.add(socket);
//					new Thread(new HandleInputMessageThread(socket)).start();
//				}
//				
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//
//		}
//		public class HandleInputMessageThread implements Runnable{
//			private Socket socket = null;
//			
//			public HandleInputMessageThread(Socket sock){
//				this.socket = sock;
//			}
//			
//			public void run(){
//				while(true){
//					InputStream inputStream = null;
//					try{
//						inputStream = socket.getInputStream();
//						byte data[] = new byte[1024];
//						int i = 0;
//						while((i = inputStream.read(data))!=-1){
//							String buffer = null;
//							buffer = new String(data,"gbk");
////							buffer = buffer.trim() + "\n";
//							buffer = buffer.trim();
//							Log.e("SocketServerService", buffer);
//							if(!myProbe.ssm.bSockRouterUsable)
//							{
//								if(checkIfRouterConnected(buffer))
//								{
//									connectRouter();
//								}
//							}
////							for(Socket sock : socketList){
////								buffer = buffer.trim() + "\n";
////								outputStream = sock.getOutputStream();
////								outputStream.write(buffer.getBytes("gbk"));
////								outputStream.flush();
////							}
//							break;
//						}
//					}catch(IOException e){
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
	

}
