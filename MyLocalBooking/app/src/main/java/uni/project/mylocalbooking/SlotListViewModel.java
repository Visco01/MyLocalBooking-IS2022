package uni.project.mylocalbooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelStoreOwner;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;

public class SlotListViewModel extends ViewModel {
    public final MutableLiveData<LocalDate> minStartOfWeek = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> currentDay = new MutableLiveData<>();

    public static LocalDate GetFirstDayOfWeek(LocalDate date) {
        int current_dow = date.getDayOfWeek().getValue();
        int monday_dow = DayOfWeek.MONDAY.getValue();

        if (current_dow > monday_dow)
            return date.minusDays(current_dow - monday_dow);

        return date;
    }

    public void setMinStartOfWeek(LocalDate minStart) {
        LocalDate today = SlotListViewModel.GetFirstDayOfWeek(LocalDate.now());
        if(minStart == null)
            minStartOfWeek.setValue(today);

        this.startOfWeek.setValue(today);
    }

    public LiveData<LocalDate> getStartOfWeek() {
        return startOfWeek;
    }

    public LiveData<LocalDate> getMinStartOfWeek() {
        return minStartOfWeek;
    }

    public void setStartOfWeek(LocalDate date) {
        startOfWeek.setValue(date);
    }

    public LiveData<DayOfWeek> getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(DayOfWeek dow) {
        currentDay.setValue(dow);
    }
}
