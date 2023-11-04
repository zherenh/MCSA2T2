package mobile.ui.dashboard;

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
import mobile.ui.dashboard.ListData;

import java.util.ArrayList;
public class ListAdapter extends ArrayAdapter<mobile.ui.dashboard.ListData> {
    public ListAdapter(@NonNull Context context, ArrayList<mobile.ui.dashboard.ListData> dataArrayList) {
        super(context, R.layout.property_list, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.property_list, parent,
                    false);
        }

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listAddress = view.findViewById(R.id.listAddress);
        //       TextView listSize = view.findViewById(R.id.listSize);
        //      listImage.setImageResource(listData.image);
        listImage.setImageBitmap(listData.img);
//        listImage.setImageResource(listData.image);
        listAddress.setText(listData.address);
//        listSize.setText(listData.size);

        return view;
    }
}
