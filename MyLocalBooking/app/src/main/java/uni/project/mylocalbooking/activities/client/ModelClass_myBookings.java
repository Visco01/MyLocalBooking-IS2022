package uni.project.mylocalbooking.activities.client;

public class ModelClass_myBookings {
    private int imageview;
    private String tittle;
    private String location;
    private String hour;

    public ModelClass_myBookings(int imageview, String tittle, String location, String hour) {
        this.imageview = imageview;
        this.tittle = tittle;
        this.location = location;
        this.hour = hour;
    }

    public int getImageview() {
        return imageview;
    }

    public void setImageview(int imageview) {
        this.imageview = imageview;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
