package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by ankita_mehta on 11/28/16.
 */

public class GridAdapter extends ArrayAdapter<Bitmap> {

        private List<Bitmap> images;
        private Context context;

        public GridAdapter(Context context,
                         List<Bitmap> images) {
            super(context,0, images);
            this.images = images;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image=(ImageView)convertView;
            if (image == null) {
                image = new ImageView(context);
            }
            image.setImageBitmap(images.get(position));
            return image;
        }

        public void addImage(Bitmap image) {
            images.add(images.size()-1,image);
            this.notifyDataSetChanged();
        }
}
