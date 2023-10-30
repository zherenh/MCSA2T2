package mobile.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.sceneview.sample.arcursorplacement.R;

import java.util.ArrayList;
public class ListAdapter extends ArrayAdapter<ListData> {
    public ListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList) {
        super(context, R.layout.furniture_list, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.furniture_list, parent,
                    false);
        }

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listName);
        TextView listSize = view.findViewById(R.id.listSize);
//        listImage.setImageResource(listData.image);
        listImage.setImageBitmap(listData.img);
//        listImage.setImageResource(listData.image);
        listName.setText(listData.name);
        listSize.setText(listData.size);

        return view;
    }
}