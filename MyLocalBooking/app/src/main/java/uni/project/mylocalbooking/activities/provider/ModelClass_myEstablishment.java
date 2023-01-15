package uni.project.mylocalbooking.activities.provider;

public class ModelClass_myEstablishment {
    private int imageView;
    private String title;
    private String location;

    public ModelClass_myEstablishment(int imageView, String title, String location) {
        this.imageView = imageView;
        this.title = title;
        this.location = location;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
