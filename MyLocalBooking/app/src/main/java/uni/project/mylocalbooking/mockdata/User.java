package uni.project.mylocalbooking.mockdata;

public class User {
    private String name;
    private String lastname;
    private Integer age;
    private String address;
    private String longAddress;
    private String cellNumber;
    private String email;

    public User(){
        this.name = "Mario";
        this.lastname = "Rossi";
        this.age = 15;
        this.address = "Via Torino 13, Mestre (VE)";
        this.longAddress = "Gran viale Milano-Ancona-Torino 1345, Carpenedo-Mestre (VE)";
        this.cellNumber = "3669598553";
        this.email = "mario.rossi97@gmail.com";
    }

    public User(String who){
        new User();
        this.name = "Rettrice di Turno";
        this.email = "cus@unive.it";
    }

    public Integer getAge() {
        return age;
    }

    public String getLastname() {
        return lastname;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLongAddress() {
        return longAddress;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public String getEmail() {
        return email;
    }

    public String fullName(){
        return this.name + " " + this.lastname;
    }
}