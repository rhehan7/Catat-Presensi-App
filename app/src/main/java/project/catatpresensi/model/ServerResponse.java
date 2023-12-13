package project.catatpresensi.model;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {

	@SerializedName("success")
	private boolean success;
	@SerializedName("message")
	private String message;
	@SerializedName("data")
	private Data data;


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

	public void setData(Data data){
		this.data = data;
	}
	public Data getData(){
		return data;
	}
}