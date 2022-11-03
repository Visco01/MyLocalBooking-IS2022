package uni.project.mylocalbooking.models;

import java.util.Date;
import java.util.HashMap;

public class Provider extends AppUser {
    private final Long id;
    public final boolean verified;
    public final int maxStrikes;
    public final String companyName;

    public HashMap<Client, Integer> strikedUsers;

    public Provider(Long id, boolean verified, Integer maxStrikes, String companyName, HashMap<Client, Integer> strikedUsers, Long app_user_id, String cellphone, String email, String firstname, String lastname, Date dob) {
        super(app_user_id, cellphone, email, firstname, lastname, dob);
        this.id = id;
        this.verified = verified;
        this.maxStrikes = maxStrikes;
        this.companyName = companyName;

        this.strikedUsers = strikedUsers;
    }

    public Provider(boolean verified, Integer maxStrikes, String companyName, HashMap<Client, Integer> strikedUsers, String cellphone, String email, String firstname, String lastname, Date dob) {
        this(null, verified, maxStrikes, companyName, strikedUsers, null, cellphone, email, firstname, lastname, dob);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
