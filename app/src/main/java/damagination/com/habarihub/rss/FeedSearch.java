/**
 * Created by Damas
 */

package damagination.com.habarihub.rss;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import damagination.com.habarihub.R;
import damagination.com.habarihub.database.SourcesDatabaseOpenHelper;


public class FeedSearch extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_search);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FeedSearchFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Intent settings = new Intent(this, SettingsActivity.class);
            //startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class FeedSearchFragment extends Fragment {

        private final String LOG_TAG = FeedSearchFragment.class.getSimpleName();

        ArrayAdapter<String> adapter = null;
        ArrayList<String> feedEntry = new ArrayList<String>();

        public FeedSearchFragment() {

        }

        @Override
        public void onStart() {
            super.onStart();
            Intent intent = getActivity().getIntent();
            String searchString = intent.getStringExtra(Intent.EXTRA_TEXT);
            runSearch(searchString);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed_search, container, false);

            adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_feed_result, R.id.list_item_feed_textview, new ArrayList<String>());
            ListView feed_list = (ListView) rootView.findViewById(R.id.listview_feed);
            feed_list.setAdapter(adapter);

            feed_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String source = feedEntry.get(position);
                    String[] temp = source.split("%");
                    String displayName = Html.fromHtml(new SpannableString(temp[1]).toString()).toString();
                    String url = temp[0];
                    Source news = null;
                    try {
                      news = new Source(displayName, url);

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "News Source Errror: ", e);
                    }

                    final Source tempSource = news;
                    String[] tmp = {"Read","Watch"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Add To:")
                            .setItems(tmp, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == 0){
                                        SourcesDatabaseOpenHelper nsdo = new SourcesDatabaseOpenHelper(getActivity());
                                        nsdo.ifSourceExists(tempSource, "Read");
                                    } else if(which == 1){
                                        SourcesDatabaseOpenHelper nsdo = new SourcesDatabaseOpenHelper(getActivity());
                                        nsdo.ifSourceExists(tempSource, "Watch");
                                    }
                                }
                            });
                    builder.create();
                    builder.show();


                    //  Toast.makeText(getActivity(), temp[0], Toast.LENGTH_LONG).show();
                }
            });

            return rootView;
        }

        public void runSearch(String search) {
            FetchFeedTask task = new FetchFeedTask();
            if (!search.isEmpty()) {
                task.execute(search);
            } else {
                Toast.makeText(getActivity(), "Please Enter something to Search", Toast.LENGTH_LONG).show();
            }
        }

        public class FetchFeedTask extends AsyncTask<String, Void, String[]> {

            @Override
            protected String[] doInBackground(String... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String feedsJSON = null;

                try {
                    final String BASE_URL = "http://ajax.googleapis.com/ajax/services/feed/find?v=1.0";
                    final String QUERY = "q";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY, params[0])
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "Feed URL: " + url);

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream is = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (is == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    feedsJSON = buffer.toString();
                    Log.v(LOG_TAG, "Search Result JSON: " + feedsJSON);
                    return getFeedSearchResults(feedsJSON);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null; //if parsing fails
            }

            public String[] getFeedSearchResults(String feedsJSON) throws JSONException {
                final String ENTRIES = "entries";
                final String URL = "url";
                final String TITLE = "title";
                final String RESPONSE_DATA = "responseData";
                final String RESPONSE_STATUS = "responseStatus";
                final String SEPARATOR = "%";
                JSONObject feedObject;
                JSONArray feedResults;
                String[] res = null;


                feedObject = new JSONObject(feedsJSON);

                if (feedObject.getString(RESPONSE_STATUS).equals("404")) {
                    //do nothing for now, Invalid result data
                } else {
                    feedObject = feedObject.getJSONObject(RESPONSE_DATA);
                    feedResults = feedObject.getJSONArray(ENTRIES);
                    res = new String[feedResults.length()];

                    for (int i = 0; i < feedResults.length(); i++) {
                        JSONObject obj = feedResults.getJSONObject(i);
                        String url = obj.getString(URL);
                        String title = obj.getString(TITLE);

                        res[i] = title;
                        feedEntry.add(url + SEPARATOR + title);
                    }
                }
                return res;
            }

            @Override
            protected void onPostExecute(String[] results) {
                if (results != null) {
                    adapter.clear();
                    for (String feed : results) {
                        SpannableString content = new SpannableString(feed);
                        adapter.add(Html.fromHtml(content.toString()).toString());
                    }
                } else {
                    Toast.makeText(getActivity(), "Couldn't get results now!! :(", Toast.LENGTH_LONG).show();
                    return;
                }

                ArrayList<String> feeds = new ArrayList<String>(Arrays.asList(results));
                adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_feed_result, R.id.list_item_feed_textview, feeds);
            }
        }
    }
}
