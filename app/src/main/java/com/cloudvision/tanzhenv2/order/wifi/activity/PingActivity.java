package com.cloudvision.tanzhenv2.order.wifi.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.BaseActivity;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.OnlyToast;
import com.cloudvision.tanzhenv2.order.wifi.ping.IPingCompletedEventHandler;
import com.cloudvision.tanzhenv2.order.wifi.ping.IPingResult;
import com.cloudvision.tanzhenv2.order.wifi.ping.IPingService;
import com.cloudvision.tanzhenv2.order.wifi.ping.IPingSessionStartedEventHandler;
import com.cloudvision.tanzhenv2.order.wifi.ping.NativePingCommand;
import com.cloudvision.tanzhenv2.order.wifi.ping.PingService;
import com.cloudvision.util.MyLog;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ping测试
 * 
 * Created by 谭智文
 */
public class PingActivity extends BaseActivity {
	
	private static final String TAG = "PingActivity";
	
	private static final String[] items = new String[]{"www.baidu.com", "www.sina.com.cn","手动输入"};
	private static final String DEFAULT_ADDRESS = items[0];
	private static final int PING_WAIT_TIME = 500;
    
    private LinearLayout inputView;
    private TextView ipAddressTextView;
    private TextView sendPackageTextView;
    private TextView lostPackageTextView;
    private TextView minRttTextView;
    private TextView maxRttTextView;
    private TextView avgRttTextView;
    private TextView RttMeanDeviationTextView;
    private TextView inputViewTv;

    private InetAddress targetAddress = null;
    private boolean isManual = false;

    private UUID currentSessionId;
    public UUID getCurrentSessionId() {
        return currentSessionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        //禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // setup UI
        setContentView(R.layout.activity_wifi_ping);
        
        initView();

  		OnlyToast.getInstance(PingActivity.this);
        
        setupIpTextInput();
        startPingService();
        getIpAddress(DEFAULT_ADDRESS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pingServiceBounded) {
            unbindService(pingServiceConnection);
            pingServiceBounded = false;
        }
        pingService.stopPing();
        
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Ping Service definition
    protected boolean pingServiceBounded = false;
    protected IPingService pingService = null;
    protected ServiceConnection pingServiceConnection = null;
    protected IPingCompletedEventHandler[] pingCompletedObservers;
    protected IPingSessionStartedEventHandler[] pingSessionStartedObservers = new IPingSessionStartedEventHandler[] { };

    private void registerPingServiceHandlers() {
        for (IPingCompletedEventHandler observer : pingCompletedObservers) {
            pingService.registerPingCompletedEventHandler(observer);
        }
        for (IPingSessionStartedEventHandler observer : pingSessionStartedObservers) {
            pingService.registerPingSessionStartedEventHandler(observer);
        }
    }

    private void unregisterPingServiceHandlers() {
        for (IPingCompletedEventHandler observer : pingCompletedObservers) {
            pingService.unregisterPingCompletedEventHandler(observer);
        }
        for (IPingSessionStartedEventHandler observer : pingSessionStartedObservers) {
            pingService.unregisterPingSessionStartedEventHandler(observer);
        }
    }

    private void startPingService() {
        MyLog.i(TAG, "Starting Ping Service");

        // event handler definition
        PlotHandler handler = new PlotHandler();
        pingCompletedObservers = new IPingCompletedEventHandler[] {
                new PersistenceHandler(), handler
        };
        pingSessionStartedObservers = new IPingSessionStartedEventHandler[] {
                handler
        };

        // define connection to service
        pingServiceConnection = new ServiceConnection() {

            public void onServiceDisconnected(ComponentName name) {
                // Ping Service Disabled
                MyLog.i(TAG, "Ping Service Disconnected");

                // unregister Ping Service Handlers
                unregisterPingServiceHandlers();

                pingServiceBounded = false;
                pingService = null;
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                // Ping Service Ready
                // register Ping Service
                MyLog.i(TAG, "Ping Service Connected");
                pingServiceBounded = true;
                pingService = (IPingService) service;
                if(!pingService.isPinging()) {
                    pingService.init(new NativePingCommand(), PING_WAIT_TIME);
                }

                // register Ping Service Handlers
                registerPingServiceHandlers();
            }
        };

        // start service
        Intent i = new Intent(this, PingService.class);
        bindService(i, pingServiceConnection, BIND_AUTO_CREATE);
        startService(i);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UI Handling
    //  Ping Button
    public void onPingButtonClicked(View view) {
        Button button = (Button) view;
        EditText ipTextInput = (EditText) findViewById(R.id.target_ip_input);
        String ipString = ipTextInput.getText().toString();
        if (!CommonUtils.CheckNetworkState(PingActivity.this)) {
        	return;
		}

        if(pingService == null) {
            OnlyToast.showToast("Ping Service 未准备就绪，请稍后再试。");
            return;
        }
        if (ipString.equals("")) {
        	OnlyToast.showToast("请输入域名或IP");
        	return;
		}
        if(!isIpValid(ipString) && targetAddress == null) {
            OnlyToast.showToast("无法解析域名，可尝试输入IP");
            return;
        }

        if(pingService.isPinging()) {
            // stop probing
            pingService.stopPing();
            
            stopView();

            // show start probing label
            button.setText(R.string.action_probe__start);

            // enable input
            if (isManual) {
            	enableIpInput();
            }
    
        } else {
            // generate ping session Id
            currentSessionId = UUID.randomUUID();

            // start probing;
            if (null == targetAddress || "".equals(targetAddress) || "localhost/::1".equals(targetAddress.toString())) {
				
	            OnlyToast.showToast("无法解析域名，可尝试输入IP");
	            
			}else {
				
				pingService.startPing(targetAddress);
				
				// show stop probing label
	            button.setText(R.string.action_probe__stop);

	            // disable input
	            disableIpInput();
			}

        }
    }

    //  IP Text Input
    protected void disableIpInput() {
    	
        EditText ipInput = (EditText) findViewById(R.id.target_ip_input);
        ipInput.setBackgroundColor(getResources().getColor(R.color.no_background));
        ipInput.setEnabled(false);
    }

    protected void enableIpInput() {
    	
        EditText ipInput = (EditText) findViewById(R.id.target_ip_input);
        ipInput.setEnabled(true);
    }
    
    private void enableText(String text){
    	
    	//替换edittext为textview
        inputViewTv.setText(text);
        inputViewTv.setVisibility(View.VISIBLE);
        EditText ipInput = (EditText) findViewById(R.id.target_ip_input);
        ipInput.setVisibility(View.GONE);
    }
    
    private void disableText(){
    	
    	inputViewTv.setVisibility(View.GONE);
    	EditText ipInput = (EditText) findViewById(R.id.target_ip_input);
        ipInput.setVisibility(View.VISIBLE);
    }

    private void setupIpTextInput() {
        // get Target Ip EditText element
        final EditText ipTextInput = (EditText) findViewById(R.id.target_ip_input);

        //给定ping的初始值
        ipTextInput.setText(DEFAULT_ADDRESS);
        disableIpInput();
        enableText(items[0]);
        
        // set Input Watcher to sanitize input
        ipTextInput.addTextChangedListener(new TextWatcher() {
        	
        	String tmp = "";  
            String digits = "1234567890.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Patterns.IP_ADDRESS.matcher(s).matches()) {
//                    ipTextInput.setError(getResources().getString(R.string.error_invalid_target_ip));
//                    ipTextInput.setBackgroundColor(getResources().getColor(R.color.error_background));
                    
					String newString = s.subSequence(start, start+count).toString();
                    if (isCN(newString)) {
                    	s = s.subSequence(start, s.length()-count);
                    }
                    
                    final CharSequence finalSequence = s;
                	new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								targetAddress = InetAddress.getByName(finalSequence.toString());
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} 
						}
					}).start();

                    return;
                }

                try {
                    targetAddress = Inet4Address.getByName(s.toString());
                    ipTextInput.setBackgroundColor(getResources().getColor(R.color.success_background));
                } catch (UnknownHostException e) {
                    // impossible if we provide only IP addresses
                    MyLog.e(TAG, "Unknown Host Exception: " + e.getStackTrace());
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            	
            	String str = s.toString();  
                if(str.equals(tmp)){  
                    return;  
                }  
                int selectionEnd = ipTextInput.getSelectionEnd(); 
                int inputType = ipTextInput.getInputType();
                StringBuffer sb = new StringBuffer();  
                for(int i = 0; i < str.length(); i++){  
                    if(digits.indexOf(str.charAt(i)) >= 0){  
                        sb.append(str.charAt(i));  
                    }  
                }  
                tmp = sb.toString();  
                ipTextInput.setText(tmp);
                int tempSelection = tmp.length(); 
                if (selectionEnd < tempSelection) {
                	tempSelection = selectionEnd;
				}
                ipTextInput.setSelection(tempSelection);
                if (inputType == 1) {
                	ipTextInput.setInputType(inputType);
				}
            }  	
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Ping Service Event Handling
    //  UI
//    private static HashMap<Integer, Integer> DATA_SET_COLORS;

    public void showError(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OnlyToast.showToast("ERROR: " + s);
            }
        });
    }

    public class PlotHandler implements IPingCompletedEventHandler, IPingSessionStartedEventHandler {

        public PlotHandler() {   
        }

        public void onPingSessionStarted(IPingService source) {
            // initialize Results UI
            MyLog.i(TAG, "PING SESSION STARTED");
            PingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	
                	runningView();

                    // init data collections
                    MyLog.i("PLOT", "Init Data Collections");
                }
            });
        }

        @Override
        public void onPingCompleted(IPingService source, final IPingResult result) {
            // append point in graph
            PingActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    MyLog.i("PLOT", "Ping Completed");
                   
                    updateCharts();
                    
                    //显示ping得到的数据
                    setResult(result);
                }
            });

        }

        private void updateCharts() {
            MyLog.i("PLOT", "Update Charts");
        }

    }

    public class PersistenceHandler implements IPingCompletedEventHandler {

        @Override
        public void onPingCompleted(IPingService source, IPingResult result) {
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private void initView() {
			
    	//title设置
  		TextView tv_title = (TextView) findViewById(R.id.top_title);
  		tv_title.setText("Ping测试");
  		TextView tv_back = (TextView) findViewById(R.id.top_back);
  		tv_back.setOnClickListener(new OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				finish();
  			}
  		});
 		
 		inputView = (LinearLayout) findViewById(R.id.input_view);
 		inputView.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				showEditDialog();
			}
		});
 		
 		inputViewTv = (TextView) findViewById(R.id.target_ip_input_tv); 
 		
 		ipAddressTextView = (TextView) findViewById(R.id.ip_address);
 		sendPackageTextView = (TextView) findViewById(R.id.send_package);
 		lostPackageTextView = (TextView) findViewById(R.id.lost_package);
 		minRttTextView = (TextView) findViewById(R.id.min_rtt);
 		maxRttTextView = (TextView) findViewById(R.id.max_rtt);
 		avgRttTextView = (TextView) findViewById(R.id.avg_rtt);
 		RttMeanDeviationTextView = (TextView) findViewById(R.id.rtt_mean);
 		
	}
    
    private void getIpAddress(final String url) {
    	
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
			        targetAddress = Inet4Address.getByName(url);
		        } catch (UnknownHostException e) {
		            MyLog.e(TAG, "Unknown Host Exception: " + e.getStackTrace());
		            e.printStackTrace();
		        }
			}
		}).start();	
	}
    
    private void setResult(IPingResult result) {
    	if (result.getTargetAddress().toString().contains("/")) {
    		String[] strings = result.getTargetAddress().toString().split("/");
    		ipAddressTextView.setText(strings[0]+"\n"+strings[1]);
		}else {
			ipAddressTextView.setText(result.getTargetAddress().toString());
		}
		sendPackageTextView.setText(result.getPacketSent()+"");
		lostPackageTextView.setText(result.getPacketsLost()+"");
		minRttTextView.setText(result.getMinRtt()+"ms");
		maxRttTextView.setText(result.getMaxRtt()+"ms");
		avgRttTextView.setText(result.getAvgRtt()+"ms");
		RttMeanDeviationTextView.setText(result.getRttMeanDeviation()+"");
        
		pingService.stopPing();
        Button button = (Button) findViewById(R.id.ping_action_button);
        button.setText(R.string.action_probe__start);
        
        if (isManual) {
			enableIpInput();
		}else {
			inputView.setEnabled(true);
		}
   	}
    
    private void runningView() {
    	
    	inputView.setEnabled(false);	//测试中禁止切换地址
    	ipAddressTextView.setText("测试中...");
		sendPackageTextView.setText("测试中...");
		lostPackageTextView.setText("测试中...");
		minRttTextView.setText("测试中...");
		maxRttTextView.setText("测试中...");
		avgRttTextView.setText("测试中...");
		RttMeanDeviationTextView.setText("测试中...");
		
    }
    
    private void stopView() {
    	
    	inputView.setEnabled(true);
    	ipAddressTextView.setText("已停止");
		sendPackageTextView.setText("已停止");
		lostPackageTextView.setText("已停止");
		minRttTextView.setText("已停止");
		maxRttTextView.setText("已停止");
		avgRttTextView.setText("已停止");
		RttMeanDeviationTextView.setText("已停止");
		
    }
    
    private void showEditDialog() {
		
    	final EditText ipInput = (EditText) findViewById(R.id.target_ip_input);
 	
        new AlertDialog.Builder(this)
            .setTitle("选择ping的地址")
            .setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	targetAddress = null;
                    switch (which) {
                        case 0:
                        	disableIpInput();
                            ipInput.setText(items[0]);
                            getIpAddress(items[0]);
                            isManual = false;
                            enableText(items[0]);
                            
                            break;
                        case 1:
                        	disableIpInput();
                        	ipInput.setText(items[1]);
                            getIpAddress(items[1]);
                            isManual = false;
                            enableText(items[1]);
                            
                            break;
                        case 2:
                        	ipInput.setText("");
                        	enableIpInput();
                        	isManual = true;
                        	disableText();
                        	inputView.setEnabled(false);
                            break;
                        default:
                        	break;
                    }
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
	}
    
    /**
     * 判断是否是IP
     *
     * @param str 待验证的字符串
     * @return 是否正确
     */
    public static boolean isIpValid(String str) {
    	if (str.length() < 7 || str.length() > 15 || "".equals(str)) {
            return false;
        }
    	String rexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";	 
        Pattern pattern = Pattern.compile(rexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();

    }
    
    /**
     * 判断是否是中文
     *
     * @param str 待验证的字符串
     */
    public boolean isCN(String str){
        try {
            byte [] bytes = str.getBytes("UTF-8");
            if(bytes.length == str.length()){
                return false;
            }else{
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
