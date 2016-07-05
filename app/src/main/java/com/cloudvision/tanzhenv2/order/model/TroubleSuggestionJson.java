package com.cloudvision.tanzhenv2.order.model;

/**
 * 工单建议类
 *
 * Created by 谭智文
 */
public class TroubleSuggestionJson {

    private String suggestion;

    private String suggestionID;

    public String getSuggestion() {
        return this.suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestionID() {
        return this.suggestionID;
    }

    public void setSuggestionID(String suggestionID) {
        this.suggestionID = suggestionID;
    }
}
