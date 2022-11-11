package uni.project.mylocalbooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;

public class SlotListViewModel extends ViewModel {
    private final MutableLiveData<LocalDate> startOfWeek = new MutableLiveData<>();
    private final MutableLiveData<DayOfWeek> currentDay = new MutableLiveData<>();

    public LiveData<LocalDate> getStartOfWeek() {
        return startOfWeek;
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
