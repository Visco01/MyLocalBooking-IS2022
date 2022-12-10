package uni.project.mylocalbooking.activities.client;

public class ModelClass_search_establishment {
    private int imageView1;
    private String tittle;
    private String location;

    public ModelClass_search_establishment(int imageView1, String tittle, String location) {
        this.imageView1 = imageView1;
        this.tittle = tittle;
        this.location = location;
    }

    public int getImageView1() {
        return imageView1;
    }

    public String getTittle() {
        return tittle;
    }

    public String getLocation() {
        return location;
    }

    public void setImageView1(int imageView1) {
        this.imageView1 = imageView1;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
