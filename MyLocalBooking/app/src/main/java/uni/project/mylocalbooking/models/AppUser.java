package uni.project.mylocalbooking.models;

import java.time.LocalDate;

public abstract class AppUser extends DatabaseModel {
    public final String cellphone;
    public final String email;
    public final String firstname;
    public final String lastname;
    public final LocalDate dob;

    public AppUser(Long id, String cellphone, String email, String firstname, String lastname, LocalDate dob) {
        super(id);
        this.cellphone = cellphone;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
    }

    public AppUser(String cellphone, String email, String firstname, String lastname, LocalDate dob) {
        this(null, cellphone, email, firstname, lastname, dob);
    }
}
