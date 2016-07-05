package com.cloudvision.tanzhenv2.order.model;

/**
 * 登录数据类
 *
 * Created by 谭智文
 */
public class LoginRoot {

    private String downloadurl;

    private String returnCode;

    private String returnMsg;

    private String upgradeurl;

    private String uploadurl;

    private String userName;

    public String getDownloadurl() {
        return this.downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getReturnCode() {
        return this.returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return this.returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getUpgradeurl() {
        return this.upgradeurl;
    }

    public void setUpgradeurl(String upgradeurl) {
        this.upgradeurl = upgradeurl;
    }

    public String getUploadurl() {
        return this.uploadurl;
    }

    public void setUploadurl(String uploadurl) {
        this.uploadurl = uploadurl;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
