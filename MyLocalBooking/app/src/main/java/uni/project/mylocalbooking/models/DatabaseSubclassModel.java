package uni.project.mylocalbooking.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DatabaseSubclassModel extends DatabaseModel {
    public static final Parcelable.Creator<DatabaseSubclassModel> CREATOR
            = new Parcelable.Creator<DatabaseSubclassModel>() {
        public DatabaseSubclassModel createFromParcel(Parcel in) {
            return new DatabaseSubclassModel(in);
        }

        public DatabaseSubclassModel[] newArray(int size) {
            return new DatabaseSubclassModel[size];
        }
    };

    private Long id;

    public DatabaseSubclassModel(Long subclassId, Long superclassId) {
        super(superclassId);
        this.id = subclassId;
    }

    public DatabaseSubclassModel(JSONObject object) throws JSONException {
        super(object);

        id = object.getLong("subclass_id");
    }

    protected DatabaseSubclassModel(Parcel in) {
        super(in);
        id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSubclassId());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof DatabaseSubclassModel))
            return ((Object) this).equals(obj);

        DatabaseSubclassModel other = (DatabaseSubclassModel) obj;
        return getId().equals(other.getId()) && getSubclassId().equals(other.getSubclassId());
    }

    public Long getSubclassId() {
        return id;
    }

    public void setSubclassId(Long id) {
        this.id = id;
    }
}
