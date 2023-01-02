package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class DatabaseModel implements Parcelable {
    private Long id;

    public DatabaseModel(Long id) {
        this.id = id;
    }

    protected DatabaseModel(JSONObject object) throws JSONException {
        this.id = object.getLong("id");
    }

    protected DatabaseModel(Parcel in) {
        id = in.readLong();
    }

    public final Long getId() {
        return id;
    }

    public final void setId(Long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
    }
}