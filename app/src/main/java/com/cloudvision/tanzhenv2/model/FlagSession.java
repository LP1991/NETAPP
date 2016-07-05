package com.cloudvision.tanzhenv2.model;


public class FlagSession {
	
	private static FlagSession uniqueRI = null; 
	
	public	int nGetRouterStaticInfoFlag; 
	public	int	nGetRouterDynamicInfoFlag;
	public int nGetEocStaticInfoFlag;
	public int nGetEocStaticInfoFlagV2;
	public int nSetEocStaticInfoFlagV2;
	public int nGetEocTopology;
	public int nGetEocDetail;
	public int nCarrierInfoFlag;
	public int nGetEocNetInfo;
	public int nSetTanZhenAp;
	public int nSetTanZhenClient;
	public int nGetAnyInfo;
	public int nGetDigInfo;
	public int nGetNetSpeed;
	public int nGetStaticFlag;
	public int nLoginEoc;
	public int GetDnsInfo;
	public int nMcuUpgradeFlag;
	public int nGetDeviceVersionFlag;
	
	//
	
	public	int nGetCatvStaticInfoFlag; 
	public	int nGetCatvDynamicInfoFlag; 
	
	public	int	nGetDhcpInfoFlag; 
	public	int nDHCPSucceedFlag; 
	
	public	int nGetPPPoEInfoFlag; 
	public	int nPPPoESucceedFlag; 
	
	public	int nGetEthInfoFlag;
	public  int nGetEthInfoSucceedFlag;
	
	public	int nGetCableInfoFlag;
	public 	int nGetCableInfoSucceedFlag;
	
	public String strIpGetWay; 
	public int nIpGetWayFlag = 0; 
	
	public int nDNSFlag; 
	public int nPingFlag; 
	public int nWebPageFlag; 
	public int nWebPageSucceedFlag;
	public int nVodFlag; 
	public int nFtpFlag; 
	public int nVlanFlag; 
	public int nBandWidth; 
	
	public int nRouterUpgradeFlag; 
	public int nRouterUpgradeSucceedCode;
	public int nCatvUpgradeFlag; 
	public int nCatvUpgradeSucceedCode;
	
	public int nNvmUpgradeFlag; 
	public int nNvmUpgradeSucceedCode;
	
	public int nPibUpgradeFlag; 
	public int nPibUpgradeSucceedCode;
	
	
	
	public int nRouterHeartBeatFlag; 
	public int nCatvHeartBeatFlag;
	
	public int nStartUpCatvFlag;
	public int nStartUpCatvSucceedFlag; 
	public int nShutDownCatvFlag; 
	public int nShutDownCatvSucceedFlag;
	
	public int nScore;
	
	public int catvWakeupFlag;
	
	public FlagSession()
	{ 
		this.nGetRouterStaticInfoFlag = 0;
		this.nGetRouterDynamicInfoFlag = 0;
		this.nGetCatvStaticInfoFlag = 0;
		this.nGetCatvDynamicInfoFlag = 0;
		this.nGetDhcpInfoFlag = 0;
		this.nGetPPPoEInfoFlag = 0;
		this.nGetEocStaticInfoFlag = 0;
		
		this.nRouterUpgradeFlag = 0;
		this.nRouterUpgradeSucceedCode = 0;
		
		this.nCatvUpgradeFlag = 0;
		this.nCatvUpgradeSucceedCode = 0;
		
		this.nNvmUpgradeFlag = 0;
		this.nNvmUpgradeSucceedCode = 0;
		
		this.nPibUpgradeFlag = 0;
		this.nPibUpgradeSucceedCode = 0;
		
		this.nRouterHeartBeatFlag = 0;
		this.nCatvHeartBeatFlag = 0;
		
		this.nStartUpCatvFlag = 0;
		this.nShutDownCatvSucceedFlag = 0;
		this.nShutDownCatvFlag = 0;
		this.nShutDownCatvSucceedFlag = 0;
		
		this.nScore = 0;
		this.catvWakeupFlag = 0;

		this.nGetEocStaticInfoFlagV2 = 0;
		this.nSetEocStaticInfoFlagV2 = 0;
		this.nGetEocTopology = 0;
		this.nGetEocDetail = 0;
		this.nCarrierInfoFlag = 0;
		this.nGetEocNetInfo = 0;
		this.nSetTanZhenAp = 0;
		this.nSetTanZhenClient = 0;
		this.nGetAnyInfo = 0;
		this.nGetDigInfo = 0;
		this.nGetNetSpeed = 0;
		this.nGetStaticFlag = 0;
		this.nLoginEoc = 0;
		this.GetDnsInfo = 0;
		this.nMcuUpgradeFlag = 0;
		this.nGetDeviceVersionFlag = 0;
		
	} 
	
	public static FlagSession getInstance()
	{ 
		if ( uniqueRI == null )
			uniqueRI = new FlagSession();
		return uniqueRI;
	} 
	
}
