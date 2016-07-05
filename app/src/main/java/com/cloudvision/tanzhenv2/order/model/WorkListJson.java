package com.cloudvision.tanzhenv2.order.model;

import java.util.List;

/**
 * 单一工单信息类
 *
 * Created by 谭智文
 */
public class WorkListJson {

    private long createdate;
    private String addresspath;
    private String customeraddress;
    private String customerid;
    private String customermobile;
    private String customername;
    private String devicename;
    private String devicetype;
    private long id;
    private String accepted;
    private String operuser;
    private String orderlevel;
    private String orderstatus;
    private String repairtype;
    private String servicelevel;
    private String servicetype;
    private List<TroubleSuggestionJson> troubleSuggestionList;
    private String troubledesc;
    private String troubletype;
    private long userid;
    private String username;
    private String resolveresult;

    public String getResolveresult() {
        return resolveresult;
    }

    public long getCreatedate() {
        return createdate;
    }

    public String getAddresspath() {
        return addresspath;
    }

    public String getCustomeraddress() {
        return customeraddress;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getCustomermobile() {
        return customermobile;
    }

    public String getCustomername() {
        return customername;
    }

    public String getDevicename() {
        return devicename;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public long getId() {
        return id;
    }

    public String getAccepted() {
        return accepted;
    }

    public String getOperuser() {
        return operuser;
    }

    public String getOrderlevel() {
        return orderlevel;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public String getRepairtype() {
        return repairtype;
    }

    public String getServicelevel() {
        return servicelevel;
    }

    public String getServicetype() {
        return servicetype;
    }

    public List<TroubleSuggestionJson> getTroubleSuggestionList() {
        return troubleSuggestionList;
    }

    public String getTroubledesc() {
        return troubledesc;
    }

    public String getTroubletype() {
        return troubletype;
    }

    public long getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }
}
