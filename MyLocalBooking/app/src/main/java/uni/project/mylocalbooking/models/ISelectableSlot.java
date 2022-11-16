package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.util.Collection;

public interface ISelectableSlot extends ITimeFrame {
    boolean isPasswordProtected();
    Collection<Client> getAttending();
    Integer getReservationLimit();
}
