package project.catatpresensi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import project.catatpresensi.data.PresensiItem;

public class ServerResponseHistory {

    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<PresensiItem> data;


    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){ return message; }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<PresensiItem> getData() {
        return data;
    }
    public void setData(List<PresensiItem> data) {
        this.data = data;
    }
}
