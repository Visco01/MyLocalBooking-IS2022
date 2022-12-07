package uni.project.mylocalbooking.activities.client;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.api.IMyLocalBookingAPI;
import uni.project.mylocalbooking.api.StatusCode;
import uni.project.mylocalbooking.models.ISelectableSlot;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Slot;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class SlotListViewModelFactory implements ViewModelProvider.Factory {
    public interface IReservationErrorListener {
        void onError(Slot slot, StatusCode statusCode);
    }

    private final IReservationErrorListener reservationErrorListener;
    public SlotListViewModelFactory(IReservationErrorListener reservationErrorListener) {
        this.reservationErrorListener = reservationErrorListener;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SlotListViewModel(reservationErrorListener);
    }

    public class SlotListViewModel extends ViewModel {
        private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
        private final MutableLiveData<DayOfWeek> currentDayOfWeek = new MutableLiveData<>();
        private final MutableLiveData<LocalDate> currentDay = new MutableLiveData<>();
        private final MutableLiveData<Collection<SlotBlueprint>> blueprints = new MutableLiveData<>();

        private final SlotListViewModelFactory.IReservationErrorListener reservationErrorListener;

        private SlotListViewModel(SlotListViewModelFactory.IReservationErrorListener reservationErrorListener) {
            this.reservationErrorListener = reservationErrorListener;
        }

        public void setBlueprints(Collection<SlotBlueprint> blueprints) {
            this.blueprints.setValue(blueprints);
        }

        public List<SlotBlueprint> getBlueprints(LocalDate date) {
            return blueprints.getValue().stream().filter(b ->
                    b.fromDate.compareTo(date) <= 0 && b.toDate.compareTo(date) > 0 &&
                            b.weekdays.contains(date.getDayOfWeek())
            ).collect(Collectors.toList());
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

        public void makeReservation(ManualSlotBlueprint blueprint, LocalTime fromTime, LocalTime toTime, String password) {
            makeReservation((Slot) new ManualSlot(fromTime, toTime, currentDay.getValue(), null, blueprint), password);
        }

        public void makeReservation(ISelectableSlot selectable, String password) {
            if(selectable instanceof Slot)
                makeReservation((Slot) selectable, password);
            else
                makeReservation((PeriodicSlotBlueprint) selectable, password);
        }

        private void makeReservation(Slot slot, String password) {
            if(slot instanceof PeriodicSlot)
                slot.owner = slot.blueprint.establishment.provider;
            else
                slot.owner = MyLocalBooking.getCurrentUser();

            IMyLocalBookingAPI.getApiInstance().addReservation(slot, password, s -> {
                        currentDay.setValue(currentDay.getValue());
                    },
                    code -> {
                        slot.blueprint.slots.remove(slot);
                        reservationErrorListener.onError(slot, code);
                    });
        }

        private void makeReservation(PeriodicSlotBlueprint blueprint, String password) {
            makeReservation((Slot) new PeriodicSlot(currentDay.getValue(), null, blueprint), password);
        }
    }
}
