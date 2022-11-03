package uni.project.mylocalbooking.models;

import java.time.LocalDate;

public class Client extends AppUser {
    private final Long id;
    public final Coordinates position;

    public Client(Long id, Coordinates position, Long app_user_id, String cellphone, String email, String firstname, String lastname, LocalDate dob) {
        super(app_user_id, cellphone, email, firstname, lastname, dob);
        this.id = id;
        this.position = position;
    }

    public Client(Coordinates position, String cellphone, String email, String firstname, String lastname, LocalDate dob) {
        this(null, position, null, cellphone, email, firstname, lastname, dob);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
