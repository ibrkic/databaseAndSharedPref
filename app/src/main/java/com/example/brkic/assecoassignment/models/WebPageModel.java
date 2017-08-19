package com.example.brkic.assecoassignment.models;

/**
 * Created by brka on 19.08.2017..
 */
public class WebPageModel {

    private int _id;
    private String webPageUrl;
    private String webPageHash;

    public WebPageModel(String webPageUrl, String webPageHash) {
        this.webPageUrl = webPageUrl;
        this.webPageHash = webPageHash;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getWebPageUrl() {
        return webPageUrl;
    }

    public void setWebPageUrl(String webPageUrl) {
        this.webPageUrl = webPageUrl;
    }

    public String getWebPageHash() {
        return webPageHash;
    }

    public void setWebPageHash(String webPageHash) {
        this.webPageHash = webPageHash;
    }
}
