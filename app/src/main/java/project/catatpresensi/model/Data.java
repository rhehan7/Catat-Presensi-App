package project.catatpresensi.model;

import com.google.gson.annotations.SerializedName;

public class Data {

	@SerializedName("token")
	private String token;
	@SerializedName("company_name")
	private String companyName;
	@SerializedName("name")
	private String username;
	@SerializedName("identity")
	private String nomorInduk;
	@SerializedName("nomorHandphone")
	private String nomorHandphone;

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getNomorInduk(){
		return nomorInduk;
	}
	public void setNomorInduk(String nomorInduk){
		this.nomorInduk = nomorInduk;
	}

	public String getNomorHandphone() {
		return nomorHandphone;
	}
	public void setNomorHandphone(String nomorHandphone) {
		this.nomorHandphone = nomorHandphone;
	}


}