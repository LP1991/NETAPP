package com.cloudvision.tanzhenv2.order.httpservice;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpDownFileSync
{
	static final int LOG_LEVEL = Log.WARN;
	static final String TAG = "HttpDownFileSync";

	static private void copyStream(InputStream is, OutputStream os)
	{
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for (;;)
			{
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch (Exception ex)
		{
		}
	}

	static public long writeToFile(String strFileName, InputStream inputStream)
	{
		OutputStream outputStream = null;
		File file = null;
		try
		{
			file = new File(strFileName);
			outputStream = new FileOutputStream(file);
			copyStream(inputStream, outputStream);
			return file.length();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (file != null)
		{
			file.delete();
		}
		return 0;
	}

	static public String downloadFile(String url, String strFileName)
	{
		final HttpClient httpClient = AndroidHttpClient.newInstance(TAG);
		final HttpGet getRequest = new HttpGet(url);

		try
		{
			//链接超时
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3*1000);
			//读取超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10*1000);
			
			HttpResponse httpResponse = httpClient.execute(getRequest);
			final int statusCode = httpResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK)
			{
				Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
				return "code404";
			}

			final HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
			{
				InputStream inputStream = null;
				try
				{
					long filelen = httpEntity.getContentLength();
					if(strFileName == null || strFileName.equals(""))
						return String.valueOf(filelen);
					
					inputStream = httpEntity.getContent();

					// save file to file
					long recvlen = writeToFile(strFileName, inputStream);
					// end of save

					if (recvlen != filelen)
					{
						Log.w(TAG, "down " + url + " failed: expect " + filelen + ", received " + recvlen);
						return "download failed";
					}
					return String.valueOf(recvlen);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (inputStream != null)
					{
						inputStream.close();
					}
					httpEntity.consumeContent();
				}
			}
		}
		catch (IOException e)
		{
			getRequest.abort();
			Log.w(TAG, "I/O error while retrieving bitmap from " + url + e);
		}
		catch (IllegalStateException e)
		{
			getRequest.abort();
			Log.w(TAG, "Incorrect URL: " + url);
		}
		catch (Exception e)
		{
			getRequest.abort();
			Log.w(TAG, "Error while retrieving bitmap from " + url + e);
		}
		finally
		{
			if ((httpClient instanceof AndroidHttpClient))
			{
				((AndroidHttpClient) httpClient).close();
			}
		}

		Log.println(LOG_LEVEL, TAG, "failed url=" + url);
		return url;
	}

}
