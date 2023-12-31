package qu.elec499.tau;

import java.io.Serializable;

public class Resident implements Serializable {

    private int id;
    private String name;
    private String phone;
    private String NP;
    private String status;
    private String photoURI;
    private String resumeURI;

    Resident() {
    }

    Resident(String name, String phone, String NP, String status, String photoURI, String resumeURI) {
        this.name = name;
        this.phone = phone;
        this.NP = NP;
        this.status = status;
        this.photoURI = photoURI;
        this.resumeURI = resumeURI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNP() {
        return NP;
    }

    public void setNP(String NP) {
        this.NP = NP;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getResumeURI() {
        return resumeURI;
    }

    public void setResumeURI(String resumeURI) {
        this.resumeURI = resumeURI;
    }
}
