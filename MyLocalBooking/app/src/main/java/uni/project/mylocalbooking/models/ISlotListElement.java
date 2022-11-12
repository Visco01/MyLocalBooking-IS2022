package uni.project.mylocalbooking.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public interface ISlotListElement {
    LocalTime getFromTime();
    LocalTime getToTime();
    boolean isInstance();
    Boolean isPasswordProtected();
    Collection<Client> getAttending();
    Integer getReservationLimit();
}
