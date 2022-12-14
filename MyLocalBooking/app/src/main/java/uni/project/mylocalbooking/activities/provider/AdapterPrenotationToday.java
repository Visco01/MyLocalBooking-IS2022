package uni.project.mylocalbooking.activities.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.R;

public class AdapterPrenotationToday extends RecyclerView.Adapter<AdapterPrenotationToday.ViewHolder4> {

    List<ModelPrenotationToday> prenotationTodayList;

    public AdapterPrenotationToday(List<ModelPrenotationToday> prenotationTodayList) {
        this.prenotationTodayList = prenotationTodayList;
    }

    @NonNull
    @Override
    public AdapterPrenotationToday.ViewHolder4 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prenotation_today_row, parent, false);
        return new ViewHolder4(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPrenotationToday.ViewHolder4 holder, int position) {
        String title = prenotationTodayList.get(position).getTitle();
        String nameClient = prenotationTodayList.get(position).getClientName();
        String fromHour = prenotationTodayList.get(position).getFromHour();
        String toHour = prenotationTodayList.get(position).getToHour();
        String numberPrenotation = prenotationTodayList.get(position).getNumberPrenotation();
        holder.setData(title, nameClient, fromHour, toHour, numberPrenotation);
    }

    @Override
    public int getItemCount() {
        return prenotationTodayList.size();
    }

    public class ViewHolder4 extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView nameClient;
        private TextView fromHour;
        private TextView toHour;
        private TextView numberPrenotation;

        public ViewHolder4(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_prenotation);
            nameClient = itemView.findViewById(R.id.name_client_prenotation);
            fromHour = itemView.findViewById(R.id.from_hour_prenotation);
            toHour = itemView.findViewById(R.id.to_hour_prenotation);
            numberPrenotation = itemView.findViewById(R.id.number_prenotation);

        }

        public void setData(String title, String nameClient, String fromHour, String toHour, String numberPrenotation) {
            this.title.setText(title);
            this.nameClient.setText(nameClient);
            this.fromHour.setText(fromHour);
            this.toHour.setText(toHour);
            this.numberPrenotation.setText(numberPrenotation);
        }
    }

}
