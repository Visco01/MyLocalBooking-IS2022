package uni.project.mylocalbooking.models;

import java.time.LocalDate;
import java.util.HashMap;

public class Provider extends AppUser implements IDatabaseSubclassModel {
    private Long id;
    public final boolean verified;
    public final int maxStrikes;
    public final String companyName;

    public HashMap<Client, Integer> strikedUsers;

    public Provider(Long id, boolean verified, String companyName, Integer maxStrikes, HashMap<Client, Integer> strikedUsers, Long app_user_id, String cellphone, String email, String firstname, String lastname, LocalDate dob) {
        super(app_user_id, cellphone, email, firstname, lastname, dob);
        this.id = id;
        this.verified = verified;
        this.maxStrikes = maxStrikes;
        this.companyName = companyName;

        this.strikedUsers = strikedUsers;
    }

    public Provider(boolean verified, String companyName, Integer maxStrikes, HashMap<Client, Integer> strikedUsers, String cellphone, String email, String firstname, String lastname, LocalDate dob) {
        this(null, verified, companyName, maxStrikes, strikedUsers, null, cellphone, email, firstname, lastname, dob);
    }

    @Override
    public Long getSubclassId() {
        return id;
    }

    @Override
    public void setSubclassId(Long id) {
        this.id = id;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }
}
