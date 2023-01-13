package uni.project.mylocalbooking.models;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public abstract class AppUser extends DatabaseSubclassModel {
    public static AppUser fromJson(JSONObject object) throws JSONException {
        String clientType = object.getString("type");
        if(clientType.equals("client"))
            return new Client(object);

        if (clientType.equals("provider"))
            return new Provider(object);

        throw new IllegalArgumentException();
    }

    public final String cellphone;
    public final String email;
    public final String firstname;
    public final String lastname;
    public final LocalDate dob;
    public final String password;

    public AppUser(Long subclassId, Long superclassId, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        super(subclassId, superclassId);
        this.cellphone = cellphone;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
    }

    public AppUser(String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        this(null, null, cellphone, email, firstname, lastname, dob, password);
    }

    protected AppUser(JSONObject object) throws JSONException {
        super(object);

        cellphone = object.getString("cellphone");
        password = object.getString("password_digest");
        firstname = object.getString("firstname");
        lastname = object.getString("lastname");
        dob = LocalDate.parse(object.getString("dob"));

        if(object.has("email"))
            email = object.isNull("email") ? null : object.getString("email");
        else
            email = null;
    }

    protected AppUser(Parcel in) {
        super(in);
        cellphone = in.readString();
        email = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        dob = (LocalDate) in.readSerializable();
        password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(cellphone);
        parcel.writeString(email);
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeSerializable(dob);
        parcel.writeString(password);
    }
}
