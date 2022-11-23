package uni.project.mylocalbooking.models;

import java.time.LocalDate;

public abstract class AppUser extends DatabaseModel {
    public final String cellphone;
    public final String email;
    public final String firstname;
    public final String lastname;
    public final LocalDate dob;
    public final String password;

    public AppUser(Long id, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        super(id);
        this.cellphone = cellphone;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
    }

    public AppUser(String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        this(null, cellphone, email, firstname, lastname, dob, password);
    }
}
