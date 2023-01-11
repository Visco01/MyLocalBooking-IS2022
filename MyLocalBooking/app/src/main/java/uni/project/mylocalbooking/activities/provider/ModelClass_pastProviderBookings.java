package uni.project.mylocalbooking.activities.provider;

public class ModelClass_pastProviderBookings {
    private String title;
    private String whoBooked;
    private String date;

    public ModelClass_pastProviderBookings(String title, String whoBooked, String date) {
        this.title = title;
        this.whoBooked = whoBooked;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhoBooked() {
        return whoBooked;
    }

    public void setWhoBooked(String whoBooked) {
        this.whoBooked = whoBooked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}