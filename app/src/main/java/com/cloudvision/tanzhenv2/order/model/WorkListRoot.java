package com.cloudvision.tanzhenv2.order.model;

import java.util.List;

/**
 * 完整工单类
 *
 * Created by 谭智文
 */
public class WorkListRoot {

    private List<WorkListJson> jsons;

    private String returnCode;

    private String returnMsg;

    public List<WorkListJson> getJson() {
        return this.jsons;
    }

    public void setJson(List<WorkListJson> Json) {
        this.jsons = Json;
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
}

