package jp.ksksue.tutorial.TWE_Control;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sugimura on 2015/10/10.
 */
public class CustomAdapter extends ArrayAdapter<CustomData> {
    private LayoutInflater layoutInflater_;

    public CustomAdapter(Context context, int textViewResourceId, List<CustomData> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        CustomData item = (CustomData) getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.custom_layout, null);
        }

        // CustomDataのデータをViewの各Widgetにセットする
        ImageView imageView;
        imageView = (ImageView) convertView.findViewById(R.id.custom_image);
        imageView.setImageBitmap(item.getImageData());

        TextView textView;
        textView = (TextView) convertView.findViewById(R.id.custom_text);
        textView.setText(item.getTextData());

        TextView textTime;
        textTime = (TextView) convertView.findViewById(R.id.custom_time);
        textTime.setText(item.getTextTime());

        if(item.getTextColor()) {
            // Black
            textView.setTextColor(Color.rgb(0x0,0x0,0x0));
            textTime.setTextColor(Color.rgb(0x0,0x0,0x0));
        } else {
            // Gray
            textView.setTextColor(Color.rgb(0x80,0x80,0x80));
            textTime.setTextColor(Color.rgb(0x80,0x80,0x80));
        }



        return convertView;
    }
}
