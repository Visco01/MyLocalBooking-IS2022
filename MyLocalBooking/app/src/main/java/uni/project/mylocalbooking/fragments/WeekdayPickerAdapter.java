package uni.project.mylocalbooking.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uni.project.mylocalbooking.R;

public class WeekdayPickerAdapter extends RecyclerView.Adapter<WeekdayPickerAdapter.ViewHolder> {

    private final IWeekdayPickerListener listener;
    private final LocalDate minStartOfWeek;

    public WeekdayPickerAdapter(IWeekdayPickerListener listener, LocalDate minStartOfWeek) {
        this.listener = listener;
        this.minStartOfWeek = minStartOfWeek;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week, parent, false);
        return new ViewHolder(view, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate startOfWeek = minStartOfWeek.plusWeeks(position);
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
        public ViewHolder(@NonNull View itemView, IWeekdayPickerListener listener) {
            super(itemView);
            LinearLayout weekRoot = (LinearLayout) itemView.getRootView();

            for(int i = 0; i < weekRoot.getChildCount(); i++) {
                View dayView = weekRoot.getChildAt(i);

                final DayOfWeek dow = DayOfWeek.of(i + 1);
                String name = dow.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                ((TextView) dayView.findViewById(R.id.weekday_name)).setText(name);
                ((Button) dayView.findViewById(R.id.weekday_button)).setOnClickListener(view -> {
                    listener.onDaySelected(dow);
                });
            }
        }

        public void setStartOfWeek(LocalDate startOfWeek) {
            LinearLayout weekRoot = (LinearLayout) itemView.getRootView();

            for(int i = 0; i < weekRoot.getChildCount(); i++) {
                int dayOfMonth = startOfWeek.plusDays(i + 1).getDayOfMonth();
                TextView textView = ((TextView) weekRoot.getChildAt(i).findViewById(R.id.weekday_number));
                textView.setText(Integer.toString(dayOfMonth));
            }
        }
    }
}
