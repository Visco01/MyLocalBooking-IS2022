package uni.project.mylocalbooking.models;

import android.os.Parcel;

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
