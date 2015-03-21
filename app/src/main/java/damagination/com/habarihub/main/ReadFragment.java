package damagination.com.habarihub.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import damagination.com.habarihub.R;
import damagination.com.habarihub.database.SourcesDatabaseOpenHelper;
import damagination.com.habarihub.rss.ItemsAdapter;
import damagination.com.habarihub.rss.LoadNews;
import damagination.com.habarihub.rss.RSSItem;
import damagination.com.habarihub.rss.RSSReader;
import damagination.com.habarihub.rss.Source;
import damagination.com.habarihub.rss.SourceAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ReadFragment extends android.support.v4.app.Fragment implements
        AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    private final String LOG_TAG = ReadFragment.class.getSimpleName();
    private ArrayList<Source> newsSource;
    private ArrayList<RSSItem> itms = new ArrayList<RSSItem>();

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Runnable() {

            @Override
            public void run() {

                newsSource = new ArrayList<Source>();
                SourcesDatabaseOpenHelper dbHelper = new SourcesDatabaseOpenHelper(getActivity());
                newsSource = dbHelper.getAllRead();

            }
        }.run();



        LoadNews test = new LoadNews(newsSource);
        test.execute();
       // mAdapter = new ItemsAdapter(getActivity(),
       //         android.R.layout.simple_list_item_1, itms);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        //((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mAdapter = new ItemsAdapter(getActivity(), android.R.layout.simple_list_item_1, itms);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
           // mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        final Source item = newsSource.get(position);
        final int itemId = item.get_id();
        final SourcesDatabaseOpenHelper dbHelper = new SourcesDatabaseOpenHelper(getActivity());

        String[] tmp = {"Share", "Favorite"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Action")
                .setItems(tmp, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){

                        } else if(which == 1){

                        }
                    }
                });
        builder.create();
        builder.show();
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Runnable() {

            @Override
            public void run() {

                newsSource = new ArrayList<Source>();
                SourcesDatabaseOpenHelper dbHelper = new SourcesDatabaseOpenHelper(getActivity());
                newsSource = dbHelper.getAllRead();

            }
        }.run();

        mAdapter = new ItemsAdapter(getActivity(), android.R.layout.simple_list_item_1, itms);
        mListView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public class LoadNews extends AsyncTask<ArrayList<RSSItem>, Void, ArrayList<RSSItem>> {

        ArrayList<Source> sources;
        ArrayList<RSSItem> items;

        public LoadNews(ArrayList<Source> _sources) {
            sources = _sources;
            items = new ArrayList<RSSItem>();
        }

        @Override
        protected ArrayList<RSSItem> doInBackground(ArrayList<RSSItem>... params) {

            RSSReader reader = null;

            for (int i = 0; i < sources.size(); i++) {
                Log.i(LOG_TAG, "URL is :" + sources.get(i).getUrl());
                reader = new RSSReader(sources.get(i).getUrl());
                ArrayList<RSSItem> temp = reader.getRSSFeedItems();
                for (int j = 0; j < temp.size(); j++) {
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
        protected void onPostExecute(ArrayList<RSSItem> _items) {
            super.onPostExecute(_items);
            if (_items.size() != 0) {
                itms = _items;
                mAdapter = new ItemsAdapter(getActivity(), android.R.layout.simple_list_item_1, _items);
                mListView.setAdapter(mAdapter);
            }

        }

        @Override
        protected void onCancelled(ArrayList<RSSItem> strings) {
            super.onCancelled(strings);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }
}
