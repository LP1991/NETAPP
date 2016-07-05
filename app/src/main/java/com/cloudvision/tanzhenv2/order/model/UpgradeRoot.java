package com.cloudvision.tanzhenv2.order.model;

/**
 * 探针升级信息类
 *
 * Created by 谭智文
 */
public class UpgradeRoot {

	private long createDate;

    private String des;

    private String fileName;

    private String filePath;

    private boolean isDefault;

    private String modelName;

    private long modifyDate;

    private String operUser;

    private String returnCode;

    private String returnMsg;

    private String versionName;

    public long getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }

    public String getDes() {
        return this.des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public long getModifyDate() {
        return this.modifyDate;
    }

    public void setModifyDate(int modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getOperUser() {
        return this.operUser;
    }

    public void setOperUser(String operUser) {
        this.operUser = operUser;
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

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
