<<<<<<< Updated upstream:MyLocalBooking/app/src/main/java/com/example/mylocalbooking/mockdata/User.java
package uni.project.mylocalbooking.mockdata;


import java.math.BigInteger;

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
=======
package uni.project.mylocalbooking.mirco.mockdata;

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
>>>>>>> Stashed changes:MyLocalBooking/app/src/main/java/com/example/mylocalbooking/mirco/mockdata/User.java
