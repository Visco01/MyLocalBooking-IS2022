package uni.project.mylocalbooking.models;

import java.time.LocalTime;
import java.util.Collection;

public interface ISelectableSlot {
    LocalTime getFromTime();
    LocalTime getToTime();
    boolean isPasswordProtected();
    Collection<Client> getAttending();
    Integer getReservationLimit();
}
