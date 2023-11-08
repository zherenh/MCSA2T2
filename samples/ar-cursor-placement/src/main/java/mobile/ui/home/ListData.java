package mobile.ui.home;

import android.graphics.Bitmap;

public class ListData {
    String name, size;
    int image;
    Bitmap img;

    public ListData(String name, String size, int image) {
        this.name = name;
        this.size = size;
        this.image = image;
    }

    public ListData(String name, Bitmap image,String size){
        this.name =name;
        this.img=image;
        this.size=size;
    }
}
