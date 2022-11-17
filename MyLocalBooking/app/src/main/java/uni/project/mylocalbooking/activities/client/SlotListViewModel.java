package uni.project.mylocalbooking.activities.client;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListViewModel extends ViewModel {
    private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> currentDayOfWeek = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> currentDay = new MutableLiveData<>();
    private final MutableLiveData<List<SlotBlueprint>> blueprints = new MutableLiveData<>();

    public void setBlueprints(List<SlotBlueprint> blueprints) {
        this.blueprints.setValue(blueprints);
    }

    public List<SlotBlueprint> getBlueprints(LocalDate date) {
        return blueprints.getValue().stream().filter(b -> b.fromDate.compareTo(date) <= 0 && b.toDate.compareTo(date) > 0).collect(Collectors.toList());
    }

    public LiveData<LocalDate> getStartOfWeek() {
        return startOfWeek;
    }

    public void setStartOfWeek(LocalDate date) {
        startOfWeek.setValue(date);
    }

    public void setCurrentDayOfWeek(DayOfWeek dow) {
        currentDayOfWeek.setValue(dow);
        LocalDate weekStart = getStartOfWeek().getValue();
        LocalDate date = weekStart.plusDays(currentDayOfWeek.getValue().getValue() - 1);
        currentDay.setValue(date);
    }

    public LiveData<LocalDate> getCurrentDay() {
        return currentDay;
    }

    private void makeReservation(Slot slot, String password) {
        // TODO: make reservation via API
    }

    private void makeReservation(PeriodicSlotBlueprint blueprint, String password) {
        makeReservation((Slot) new PeriodicSlot(currentDay.getValue(), null, blueprint), password);
    }

    public void makeReservation(ManualSlotBlueprint blueprint, LocalTime fromTime, LocalTime toTime, String password) {
        makeReservation((Slot) new ManualSlot(fromTime, toTime, currentDay.getValue(), null, blueprint), password);
    }

    public void makeReservation(ISelectableSlot selectable, String password) {
        if(selectable instanceof Slot)
            makeReservation((Slot) selectable, password);
        else
            makeReservation((PeriodicSlotBlueprint) selectable, password);
    }
}
