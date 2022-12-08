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

public class Adapter_search_establishment extends RecyclerView.Adapter<Adapter_search_establishment.ViewHolder> {

    private List<ModelClass_search_establishment> userList;

    public Adapter_search_establishment(List<ModelClass_search_establishment> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public Adapter_search_establishment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchestablishment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_search_establishment.ViewHolder holder, int position) {

        int resource = userList.get(position).getImageView1();
        String tittle = userList.get(position).getTittle();
        String location = userList.get(position).getLocation();

        holder.setData(resource, tittle, location);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textViewTittle;
        private TextView textViewLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView1_se);
            textViewTittle = itemView.findViewById(R.id.tittle_se);
            textViewLocation = itemView.findViewById(R.id.location_se);

        }

        public void setData(int resource, String tittle, String location) {
            imageView.setImageResource(resource);
            textViewTittle.setText(tittle);
            textViewLocation.setText(location);
        }
    }
}
