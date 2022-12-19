package uni.project.mylocalbooking.activities.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.models.MapLocationOption;

public class MapLocationOptionListAdapter extends BaseAdapter {
    public interface IOptionClickedListener {
        void onOptionClicked(MapLocationOption option);
    }

    private final IOptionClickedListener optionClickedListener;
    private List<MapLocationOption> options = new ArrayList<>();

    public MapLocationOptionListAdapter(IOptionClickedListener listener) {
        this.optionClickedListener = listener;
    }
    public void onListUpdated(List<MapLocationOption> list) {
        options = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int i) {
        return options.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewRoot = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.map_location_option, viewGroup, false);


        ((TextView) viewRoot.findViewById(R.id.address)).setText(options.get(i).address);
        ((AppCompatButton) viewRoot.findViewById(R.id.location_option_button)).setOnClickListener(v -> {
            optionClickedListener.onOptionClicked(options.get(i));
        });
        return viewRoot;
    }
}
