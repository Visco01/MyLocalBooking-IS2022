package uni.project.mylocalbooking.models;

import java.util.Date;


public class Provider extends AppUser {
    private final Long id;
    public final boolean verified;
    public final int maxStrikes;
    public final String companyName;

    public Provider(Long id, boolean verified, Integer maxStrikes, String companyName, Long app_user_id, String cellphone, String email, String firstname, String lastname, Date dob) {
        super(app_user_id, cellphone, email, firstname, lastname, dob);
        this.id = id;
        this.verified = verified;
        this.maxStrikes = maxStrikes;
        this.companyName = companyName;
    }

    public Provider(boolean verified, Integer maxStrikes, String companyName, String cellphone, String email, String firstname, String lastname, Date dob) {
        this(null, verified, maxStrikes, companyName, null, cellphone, email, firstname, lastname, dob);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
