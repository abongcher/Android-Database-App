package in.abongcher.tbec;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class dataAdapter extends ArrayAdapter<Contributor>{

    Context context;
    ArrayList<Contributor> mcontact;
    List<Contributor> searchList;
    String names;


    public dataAdapter(Context context, ArrayList<Contributor> contact){
        super(context, R.layout.row, contact);
        this.context=context;
        this.mcontact=contact;

        this.searchList = new ArrayList<>();
        this.searchList.addAll(contact);
    }

    public  class  Holder{
        TextView nameFV;
        ImageView pic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Contributor data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        Holder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {


            viewHolder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row, parent, false);

            viewHolder.pic = (ImageView) convertView.findViewById(R.id.imageView_thumbnail);
            viewHolder.nameFV = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }

        viewHolder.pic.setImageBitmap(Utility.convertToBitmap(data.getImage()));
        viewHolder.nameFV.setText(data.getName());


        // Return the completed view to render on screen
        return convertView;
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mcontact.clear();
        if (charText.length() == 0) {
            mcontact.addAll(searchList);
        } else {
            for (Contributor c : searchList) {
                if (c.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mcontact.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }


    public List<Contributor> getData() {
        return mcontact;
    }

}
