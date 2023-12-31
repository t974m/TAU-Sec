package qu.elec499.tau;

public class Violation {
    private String np, speed, imageURL;

    public Violation(String np, String speed, String imageURL) {
        this.np = np;
        this.speed = speed;
        this.imageURL = imageURL;
    }

    public Violation() {
    }

    public String getNp() {
        return np;
    }

    public void setNp(String np) {
        this.np = np;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
