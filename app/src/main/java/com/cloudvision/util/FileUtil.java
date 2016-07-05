package com.cloudvision.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * 文件工具类
 * @author zhangyun
 *
 */
public class FileUtil {
	
	/**
	 * 拷贝文件到sd卡
	 * 在进行测试的时候发现在资源文件很大时，例如200M左右，可能会报错。
	 * 但可以保证的是100M以下的没问题。
	 * @param context   context对象
	 * @param strAssetsName   需要拷贝的文件名    如"xx.xml"
	 * @param strOutFileName  需要拷贝到的sd卡路径    "/mnt/sdcard/test/xx.xml"
	 * @throws IOException    
	 */
	public static void copyBigDataToSD(Context context,String strAssetsName,String strOutFileName) throws IOException 
    {  
        InputStream myInput;  
        OutputStream myOutput = new FileOutputStream(strOutFileName);  
        myInput = context.getAssets().open(strAssetsName);  
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        myOutput.flush();  
        myInput.close();  
        myOutput.close();        
    }  
	
	
	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) 
			{ // 判断是否是文件
				file.delete(); //
			}
			else if(file.isDirectory())
			{
				File[] flist = file.listFiles();
				if (flist != null) 
				{
					if (flist.length > 0) 
					{
						for (int i = 0; i < flist.length; i++) 
						{
								deleteFile(flist[i]);
						}
					}
				}
			}
		
		}
	}

	public  long getFileSize(File f) throws Exception {
		long size = 0;
		if (f.isDirectory()) 
		{
			File[] flist = f.listFiles();
			if (flist != null) 
			{
				if (flist.length > 0) 
				{
					for (int i = 0; i < flist.length; i++) 
					{
						if (flist[i].isDirectory()) 
						{
							size = size + getFileSize(flist[i]);
						} else 
						{
							size = size + getFileSizes(flist[i]);
						}
					}
				}
			}
		} else 
		{
			size = getFileSizes(f);
		}
		return size;
	}

	public  long getFileSizes(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}
	
	public  String formetFileSize(long fileS)
	 {// 转换文件大小
	  DecimalFormat df = new DecimalFormat("#.00");
	  String fileSizeString = "";
	  if (fileS < 1024)
	  {
	   fileSizeString = df.format((double) fileS) + "B";
	  }
	  else if (fileS < 1048576)
	   {
	    fileSizeString = df.format((double) fileS / 1024) + "K";
	   }
	   else if (fileS < 1073741824)
	   {
	    fileSizeString = df.format((double) fileS / 1048576) + "M";
	   }
	   else
	   {
	    fileSizeString = df.format((double) fileS / 1073741824) + "G";
	   }
	  return fileSizeString;
	 }
	
	
	
	/**
	 * 读取sd卡中指定路径的文件
	 * @param fileName    文件路径
	 * @return			文本内容  出错的话返回null
	 */
	public static String readSDFile(String fileName) {
		try
		{
			  File file=new File(fileName);
			  FileReader fr=new FileReader(file);
			  BufferedReader br=new BufferedReader(fr);
			  String temp=null;
			  String s="";
			  while((temp=br.readLine())!=null){
			   s+=temp;
			  }
			  br.close();
			  fr.close();
			  return s;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
}
