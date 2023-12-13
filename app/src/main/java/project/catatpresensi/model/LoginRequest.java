package project.catatpresensi.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("company_code")
    private String companyCode;

    @SerializedName("identity")
    private String identity;

    @SerializedName("pin")
    private String pin;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
