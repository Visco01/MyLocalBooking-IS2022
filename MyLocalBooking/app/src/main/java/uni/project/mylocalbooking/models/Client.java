package uni.project.mylocalbooking.models;

import java.util.Date;

import uni.project.mylocalbooking.IMyLocalBookingAPI;

public class Client extends AppUser {
    private final Long id;
    public final Coordinates position;

    public Client(Long id, Coordinates position, Long app_user_id, String cellphone, String email, String firstname, String lastname, Date dob) {
        super(app_user_id, cellphone, email, firstname, lastname, dob);
        this.id = id;
        this.position = position;
    }

    public Client(Coordinates position, String cellphone, String email, String firstname, String lastname, Date dob) {
        this(null, position, null, cellphone, email, firstname, lastname, dob);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
