package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;

public class Provider extends AppUser {
    public static final Parcelable.Creator<Provider> CREATOR
            = new Parcelable.Creator<Provider>() {
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };

    private Long id;
    public final boolean verified;
    public int maxStrikes;
    public final String companyName;

    public HashMap<Client, Integer> strikedUsers;

    public Provider(Long id, boolean verified, String companyName, Integer maxStrikes, HashMap<Client, Integer> strikedUsers, Long app_user_id, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        super(app_user_id, cellphone, email, firstname, lastname, dob, password);
        this.id = id;
        this.verified = verified;
        this.maxStrikes = maxStrikes;
        this.companyName = companyName;

        this.strikedUsers = strikedUsers;
    }

    public Provider(boolean verified, String companyName, Integer maxStrikes, HashMap<Client, Integer> strikedUsers, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        this(null, verified, companyName, maxStrikes, strikedUsers, null, cellphone, email, firstname, lastname, dob, password);
    }

    public Provider(JSONObject object) throws JSONException {
        super(object);

        id = object.getLong("subclass_id");
        verified = object.getBoolean("verified");
        maxStrikes = object.getInt("max_strikes");
        companyName = object.has("company_name") ? object.getString("company_name") : null;
    }

    protected Provider(Parcel in) {
        super(in);
        id = in.readLong();
        verified = in.readByte() != 0;
        maxStrikes = in.readInt();
        companyName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(id);
        parcel.writeByte((byte) (verified ? 1 : 0));
        parcel.writeInt(maxStrikes);
        parcel.writeString(companyName);
    }

    @Override
    public Long getSubclassId() {
        return id;
    }

    @Override
    public void setSubclassId(Long id) {
        this.id = id;
    }
}
