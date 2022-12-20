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

public class Adapter_myEstablishment extends RecyclerView.Adapter<Adapter_myEstablishment.ViewHolder3> {

    List<ModelClass_myEstablishment> providerEstablishment;

    public Adapter_myEstablishment(List<ModelClass_myEstablishment> providerEstablishment) {
        this.providerEstablishment = providerEstablishment;
    }

    @NonNull
    @Override
    public Adapter_myEstablishment.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_establishment_row, parent, false);
        return new ViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_myEstablishment.ViewHolder3 holder, int position) {
        int resource = providerEstablishment.get(position).getImageView();
        String title = providerEstablishment.get(position).getTitle();
        String location = providerEstablishment.get(position).getLocation();
        holder.setData(resource, title, location);
    }

    @Override
    public int getItemCount() {
        return providerEstablishment.size();
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewLocation;

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.myEstablishment_imageview1);
            textViewTitle = itemView.findViewById(R.id.myEstablishment_textView1);
            textViewLocation = itemView.findViewById(R.id.myEstablishment_textView2);

        }

        public void setData(int resource, String title, String location) {
            imageView.setImageResource(resource);
            textViewTitle.setText(title);
            textViewLocation.setText(location);
        }
    }

}