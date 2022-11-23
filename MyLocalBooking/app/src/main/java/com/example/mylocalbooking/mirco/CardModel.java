package uni.project.mylocalbooking.mirco;

public class CardModel {
    private String establishmentName;
    private String owner;
    private String address;
    private Double review;
    private Double price;

    public CardModel(String name, String owner, String add, Double rev, Double price){
        this.establishmentName = name;
        this.owner = owner;
        this.address = add;
        this.review = rev;
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public Double getPrice() {
        return price;
    }

    public Double getReview() {
        return review;
    }

    public String getEstablishmentName() {
        return establishmentName;
    }

    public String getOwner() {
        return owner;
    }
}
