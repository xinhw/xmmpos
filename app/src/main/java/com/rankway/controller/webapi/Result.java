package com.rankway.controller.webapi;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class Result {
    int code;
    String message;
    long timestamp;
    SiteDish result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public SiteDish getResult() {
        return result;
    }

    public void setResult(SiteDish result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", result=" + result +
                '}';
    }
}
