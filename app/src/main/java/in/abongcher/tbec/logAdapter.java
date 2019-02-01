package in.abongcher.tbec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by abongcher on 5/7/17.
 */
public class logAdapter extends ArrayAdapter<ContributorLogHolder> {

    Context context;
    ArrayList<ContributorLogHolder> mcontact;

    public logAdapter(Context context, int resource, ArrayList<ContributorLogHolder> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mcontact = objects;
    }

    public  class  Holder{
        TextView logText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        ContributorLogHolder data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        Holder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.logview, parent, false);

            viewHolder.logText = (TextView) convertView.findViewById(R.id.log_row);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }
        viewHolder.logText.setText(data.getLog());

        // Return the completed view to render on screen
        return convertView;
    }
}
