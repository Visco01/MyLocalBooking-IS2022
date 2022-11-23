package uni.project.mylocalbooking.mirco;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.R;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<CardModel> cardList;

    public Adapter(List<CardModel> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String name = cardList.get(position).getEstablishmentName();
        String owner = cardList.get(position).getOwner();
        String address = cardList.get(position).getAddress();
        Double review = cardList.get(position).getReview();
        Double price = cardList.get(position).getPrice();
        holder.setData(name/*,owner,address,review,price*/);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView establishmentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //establishmentName = itemView.findViewById(R.id.establishmentName);
            // ERRORE è NULL
            //System.out.println(establishmentName);

        }

        public void setData(String name) {
            //establishmentName.setText(name);
        }
    }
}