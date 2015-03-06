package damagination.com.habarihub.rss;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damas on 2/13/15.
 */
public class LoadNews extends AsyncTask<List<RSSItem>, Void, List<RSSItem>> {

    ArrayList<String> sources;
    ArrayList<RSSItem> items;

    public LoadNews(ArrayList<String> _sources){
        sources = _sources;
        items  = new ArrayList<RSSItem>();
    }

    @Override
    protected List<RSSItem> doInBackground(List<RSSItem>... params) {

        RSSReader reader = null;

        for(int i = 0; i < sources.size(); i++){
            reader = new RSSReader(sources.get(i));
            ArrayList<RSSItem> temp = reader.getRSSFeedItems();
            for(int j = 0; j < temp.size(); j++){
                items.add(temp.get(j));
            }
        }

        return items;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<RSSItem> _items) {
        if(_items.size() != 0){

        }
        super.onPostExecute(_items);
    }

    @Override
    protected void onCancelled(List<RSSItem> strings) {
        super.onCancelled(strings);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
