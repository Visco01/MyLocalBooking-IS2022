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
import uni.project.mylocalbooking.models.SelectableMapLocation;

public class AlternativeLocationListAdapter extends BaseAdapter {
    public interface IOptionClickedListener {
        void onOptionClicked(SelectableMapLocation option);
    }

    private final IOptionClickedListener optionClickedListener;
    private List<SelectableMapLocation> options = new ArrayList<>();

    public AlternativeLocationListAdapter(IOptionClickedListener listener) {
        this.optionClickedListener = listener;
    }
    public void onListUpdated(List<SelectableMapLocation> list) {
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


        SelectableMapLocation location = options.get(i);
        ((TextView) viewRoot.findViewById(R.id.address)).setText(location.address);
        ((AppCompatButton) viewRoot.findViewById(R.id.location_option_button)).setOnClickListener(v -> {
            optionClickedListener.onOptionClicked(location);
        });
        return viewRoot;
    }
}
