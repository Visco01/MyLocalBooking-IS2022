package uni.project.mylocalbooking.activities.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.R;

public class Adapter_myBookings extends RecyclerView.Adapter<Adapter_myBookings.ViewHolder2> {

    List<ModelClass_myBookings> userBookings;

    public Adapter_myBookings(List<ModelClass_myBookings> userBookings) {
        this.userBookings = userBookings;
    }

    @NonNull
    @Override
    public Adapter_myBookings.ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybookings_row, parent, false);
        return new ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_myBookings.ViewHolder2 holder, int position) {
        int resource = userBookings.get(position).getImageview();
        String tittle = userBookings.get(position).getTittle();
        String location = userBookings.get(position).getLocation();
        String hour = userBookings.get(position).getHour();
        holder.setData(resource, tittle, location, hour);
    }

    @Override
    public int getItemCount() {
        return userBookings.size();
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewTittle;
        private TextView textViewLocation;
        private TextView textViewHour;


        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.my_bookings_image);
            textViewTittle = itemView.findViewById(R.id.name_location_myBookings);
            textViewLocation = itemView.findViewById(R.id.position_myBookings);
            textViewHour = itemView.findViewById(R.id.hour_myBookings);
        }

        public void setData(int resource, String tittle, String position, String hour) {
            imageView.setImageResource(resource);
            textViewTittle.setText(tittle);
            textViewLocation.setText(position);
            textViewHour.setText(hour);
        }

    }

}
