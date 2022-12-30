package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;

public class Client extends AppUser implements IDatabaseSubclassModel {
    public static final Parcelable.Creator<Client> CREATOR
            = new Parcelable.Creator<Client>() {
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    private Long id;
    public final Coordinates position;

    public Client(Long id, Coordinates position, Long app_user_id, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        super(app_user_id, cellphone, email, firstname, lastname, dob, password);
        this.id = id;
        this.position = position;
    }

    public Client(Coordinates position, String cellphone, String email, String firstname, String lastname, LocalDate dob, String password) {
        this(null, position, null, cellphone, email, firstname, lastname, dob, password);
    }

    protected Client(Parcel in) {
        super(in);
        id = in.readLong();
        position = in.readParcelable(Coordinates.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(id);
        parcel.writeParcelable(position, i);
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
