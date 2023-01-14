package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class Client extends AppUser {
    public static final Parcelable.Creator<Client> CREATOR
            = new Parcelable.Creator<Client>() {
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    public final Coordinates position;

    public Client(Long id, Coordinates position, Long app_user_id, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        super(id, app_user_id, cellphone, email, firstname, lastname, dob, password);
        this.position = position;
    }

    public Client(Coordinates position, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        this(null, position, null, cellphone, email, firstname, lastname, dob, password);
    }

    public Client(JSONObject object) throws JSONException {
        super(object);
        position = object.has("coordinates") ? new Coordinates(object.getJSONObject("coordinates")) : null;
    }

    protected Client(Parcel in) {
        super(in);
        position = in.readParcelable(Coordinates.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(position, i);
    }
}
