package mobile.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ListData {
    String address;
    int image;
    Bitmap img;


    public ListData(String address, int image){
        this.address =address;
        this.image=image;
    }

    public ListData(String address, Bitmap image){
        this.address =address;
        this.img=image;
    }
}
