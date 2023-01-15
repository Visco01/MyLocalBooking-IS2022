package uni.project.mylocalbooking.activities.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.R;

public class AdapterPastProviderBookings extends RecyclerView.Adapter<AdapterPastProviderBookings.ViewHolder5> {

    private final RVInterface rvInterface;
    List<ModelClass_pastProviderBookings> pastProviderBookings;

    public AdapterPastProviderBookings(RVInterface rvInterface, List<ModelClass_pastProviderBookings> pastProviderBookings) {
        this.rvInterface = rvInterface;
        this.pastProviderBookings = pastProviderBookings;
    }

    @NonNull
    @Override
    public ViewHolder5 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_provider_bookings_row, parent, false);
        return new ViewHolder5(view, rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder5 holder, int position) {
        String title = pastProviderBookings.get(position).getTitle();
        String whwoBooked = pastProviderBookings.get(position).getWhoBooked();
        String date = pastProviderBookings.get(position).getDate();
        holder.setData(title, whwoBooked, date);
    }

    @Override
    public int getItemCount() {
        return pastProviderBookings.size();
    }


    public static class ViewHolder5 extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView whoBookedView;
        private TextView dateView;


        public ViewHolder5(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titlePastProviderBookings);
            whoBookedView = itemView.findViewById(R.id.whoBookProviderBookings);
            dateView = itemView.findViewById(R.id.DateProviderBookings);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rvInterface != null) {
                        int pos = getAbsoluteAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            rvInterface.onItemClick(pos);
                        }

                    }

                }
            });

        }

        public void setData(String title, String whoBooked, String date) {
            titleView.setText(title);
            whoBookedView.setText(whoBooked);
            dateView.setText(date);
        }



    }

}
