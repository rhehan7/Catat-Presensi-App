package project.catatpresensi.data;

public class PresensiItem {
    private String id;
    private String photo;
    private String type;
    private String time;
    private String date_number;
    private String bulan;
    private String tahun;

    public PresensiItem(String photo, String type, String time, String date_number, String bulan) {
        this.photo = photo;
        this.type = type;
        this.time = time;
        this.date_number = date_number;
        this.bulan = bulan;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getDate_number() {
        return date_number;
    }
    public void setDate_number(String date_number) {
        this.date_number = date_number;
    }

    public String getBulan() {
        return bulan;
    }
    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getTahun() { return tahun; }
    public void setTahun(String tahun) { this.tahun = tahun; }


} // end class PresensiItem
