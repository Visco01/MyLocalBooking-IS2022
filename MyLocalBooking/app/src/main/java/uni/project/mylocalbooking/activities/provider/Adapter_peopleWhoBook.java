package uni.project.mylocalbooking.activities.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.project.mylocalbooking.R;

public class Adapter_peopleWhoBook extends RecyclerView.Adapter<Adapter_peopleWhoBook.ViewHolder6> {

    List<ModelClass_peopleWhoBook> peopleWhoBooks;

    public Adapter_peopleWhoBook(List<ModelClass_peopleWhoBook> peopleWhoBooks) {
        this.peopleWhoBooks = peopleWhoBooks;
    }

    @NonNull
    @Override
    public Adapter_peopleWhoBook.ViewHolder6 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_who_book_row, parent, false);
        return new ViewHolder6(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_peopleWhoBook.ViewHolder6 holder, int position) {
        String name = peopleWhoBooks.get(position).getName();
        String email = peopleWhoBooks.get(position).getEmail();
        holder.setData(name, email);
    }

    @Override
    public int getItemCount() {
        return peopleWhoBooks.size();
    }

    public static class ViewHolder6 extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView emailView;

        public ViewHolder6(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.namePastPrenotation);
            emailView = itemView.findViewById(R.id.emailPastPrenotation);
        }

        public void setData(String name, String email) {
            this.nameView.setText(name);
            this.emailView.setText(email);

        }

    }

}
