package com.cloudvision.tanzhenv2.order.httpservice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 描述：线程池,程序中只????
 */

public class AsyncTaskPool
{
	/** The tag. */
	private static String TAG = "AbTaskPool";

	/** 单例对象 The http pool. */
	private static AsyncTaskPool mAbTaskPool = null;

	/** 固定五个线程来执行任?? */
	private static int nThreads = 5;

	/** The executor service. */
	private static ExecutorService executorService = null;

	/** 下载完成后的消息句柄. */
	private static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			AsyncTaskItem item = (AsyncTaskItem) msg.obj;
			item.callback.update(item.param);
		}
	};

	/**
	 * 初始化线程池
	 */
	static
	{
		nThreads = 2;
		mAbTaskPool = new AsyncTaskPool(nThreads * 15);
	}

	/**
	 * 构??线程??
	 * 
	 * @param nThreads
	 *            初始的线程数
	 */
	protected AsyncTaskPool(int nThreads)
	{
		executorService = Executors.newFixedThreadPool(nThreads);
	}

	/**
	 * 单例构??图片下载??
	 * 
	 * @return single instance of AbHttpPool
	 */
	public static AsyncTaskPool getInstance()
	{
		return mAbTaskPool;
	}

	/**
	 * 执行任务.
	 * 
	 * @param item
	 *            the item
	 */
	public void execute(final AsyncTaskItem item)
	{
		executorService.submit(new Runnable()
		{
			public void run()
			{
				try
				{
					// 定义了回??
					if (item.callback != null)
					{
						item.callback.get(item.param);
						// 交由UI线程处理
						Message msg = handler.obtainMessage();
						msg.obj = item;
						handler.sendMessage(msg);
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});

	}

	/**
	 * 
	 * 描述：获取线程池的执行器
	 * 
	 * @return executorService
	 * @throws
	 */
	public static ExecutorService getExecutorService()
	{
		return executorService;
	}

	/**
	 * 描述：立即关??
	 */
	public void shutdownNow()
	{
		if (!executorService.isTerminated())
		{
			executorService.shutdownNow();
			listenShutdown();
		}

	}

	/**
	 * 描述：平滑关??
	 */
	public void shutdown()
	{
		if (!executorService.isTerminated())
		{
			executorService.shutdown();
			listenShutdown();
		}
	}

	/**
	 * 描述：关闭监??
	 */
	public void listenShutdown()
	{
		try
		{
			while (!executorService.awaitTermination(1, TimeUnit.MILLISECONDS))
			{
				Log.e(TAG, "线程池关闭失败");
			}
			// Log.d(TAG, "线程池已关闭");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
