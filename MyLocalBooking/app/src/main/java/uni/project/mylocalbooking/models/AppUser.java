package uni.project.mylocalbooking.models;

import java.util.Date;

public abstract class AppUser implements IDatabaseModel {
    private final Long id;
    public final String cellphone;
    public final String email;
    public final String firstname;
    public final String lastname;
    public final Date dob;

    public AppUser(Long id, String cellphone, String email, String firstname, String lastname, Date dob) {
        this.id = id;
        this.cellphone = cellphone;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
    }

    public AppUser(String cellphone, String email, String firstname, String lastname, Date dob) {
        this(null, cellphone, email, firstname, lastname, dob);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
