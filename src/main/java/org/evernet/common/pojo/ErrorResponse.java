package org.evernet.common.pojo;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

    private Boolean success;

    private String message;


    public ErrorResponse() {

    }

    public ErrorResponse(String message) {
        this.success = false;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
