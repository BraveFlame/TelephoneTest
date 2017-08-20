package cn.com.telecomphone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 17-8-18.
 */

public class PersonAdapter extends ArrayAdapter<Person> {
    private int resourceId;

    public PersonAdapter(Context context, int viewResourceId, List<Person> objects) {
        super(context, viewResourceId, objects);
        resourceId = viewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person person = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(!SetPhoneBlack.isChoose){
            viewHolder.checkBox.setVisibility(View.GONE);
        }else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        }
        viewHolder.textView.setText((position+1) + ":" + person.getPhoneNumber());
        viewHolder.checkBox.setChecked(person.isCheck());
        return view;
    }

    class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }
}
