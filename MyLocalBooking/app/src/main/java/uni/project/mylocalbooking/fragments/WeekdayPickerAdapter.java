package uni.project.mylocalbooking.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;

import uni.project.mylocalbooking.R;

public class WeekdayPickerAdapter extends RecyclerView.Adapter<WeekdayPickerAdapter.ViewHolder> {

    private final IWeekdayPickerListener listener;
    private final LocalDate initialWeek;
    private LocalDate initialDate;
    private final HashMap<LocalDate, View> selectedDates = new HashMap<>();

    public WeekdayPickerAdapter(IWeekdayPickerListener listener, LocalDate initialWeek, LocalDate initialDate) {
        this.listener = listener;
        this.initialWeek = initialWeek;
        this.initialDate = initialDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate startOfWeek = initialWeek.plusWeeks(position);
        holder.setStartOfWeek(startOfWeek);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }


    public interface IWeekdayPickerListener {
        void onDaySelected(DayOfWeek dow);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout weekRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weekRoot = (LinearLayout) itemView.getRootView();

            for(int i = 0; i < weekRoot.getChildCount(); i++) {
                View dayView = weekRoot.getChildAt(i);

                final DayOfWeek dow = DayOfWeek.of(i + 1);
                TextView dayText = dayView.findViewById(R.id.weekday_name);
                dayText.setText(dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            }
        }

        public void setStartOfWeek(LocalDate startOfWeek) {
            for(int i = 0; i < weekRoot.getChildCount(); i++) {
                View dayView = weekRoot.getChildAt(i);
                int dayOfMonth = startOfWeek.plusDays(i + 1).getDayOfMonth();
                ((TextView) dayView.findViewById(R.id.weekday_number)).setText(Integer.toString(dayOfMonth));
                AppCompatButton button = (AppCompatButton) dayView.findViewById(R.id.weekday_button);

                LocalDate date = startOfWeek.plusDays(i);
                if(date.equals(initialDate) || selectedDates.containsKey(date)) {
                    selectedDates.put(date, dayView); // overwrite view which may be old
                    setViewActive(dayView, true);
                    initialDate = null;
                }
                else
                    setViewActive(dayView, false);

                DayOfWeek dow = DayOfWeek.of(i + 1);
                button.setOnClickListener(view -> {
                    toggleView(dayView, date);
                    listener.onDaySelected(dow);
                });
            }
        }

        private void toggleView(View view, LocalDate date) {
            if(!selectedDates.containsKey(date)) { // view is not active, make active
                selectedDates.values().forEach(v -> setViewActive(v, false));
                selectedDates.clear();
                setViewActive(view, true);
                selectedDates.put(date, view);
            }
        }

        private void setViewActive(View view, boolean active) {
            TextView dayText = view.findViewById(R.id.weekday_name);
            TextView numText = view.findViewById(R.id.weekday_number);
            AppCompatButton button = (AppCompatButton) view.findViewById(R.id.weekday_button);

            if(active) {
                button.setBackgroundResource(R.drawable.circle);
                dayText.setTextColor(0xFF000000);
                numText.setTextColor(0xFF000000);
            } else {
                button.setBackgroundColor(0x00000000);
                dayText.setTextColor(0xFFFFFFFF);
                numText.setTextColor(0xFFFFFFFF);
            }
        }
    }
}
