package uni.project.mylocalbooking.mockdata;

public class Establishment {
    public String name;
    public String address;
    public User owner;
    public String price;

    public Establishment(){
        this.name = "Polisportiva CUS Venezia";
        this.address = "Sestiere Santa Croce 2021, Venezia";
        this.owner = new User("who");
        this.price = "20";
    }
}
