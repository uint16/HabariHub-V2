package damagination.com.habarihub.rss;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import damagination.com.habarihub.R;

/**
 * Created by Newton Bujiku on 1/13/15.
 */
public class SourceAdapter extends ArrayAdapter<Source> {
 /*
	 * Adapts news sources from an array list to a list view*/

    private Context context;
    private ArrayList<Source> sourceList;

    public SourceAdapter(Context context, int resourceId, ArrayList<Source> sourceList) {

        super(context, resourceId, sourceList);
        this.context = context;
        this.sourceList=sourceList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        ViewHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();

            holder.sourceName = (TextView)row.findViewById(android.R.id.text1);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();

        }

        Source source = sourceList.get(position);
        holder.sourceName.setText(source.getDisplayName());
        return row;
    }

    private static class ViewHolder{

        /*
         * A ViewHolder class to enable views recycling.
         * Improves scrolling performance by avoiding findViewById(int t)
         * each time the view goes away from the screen
         * */
        TextView sourceName;

    }
}
