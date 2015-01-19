package damagination.com.habarihub.radio;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import damagination.com.habarihub.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListenFragment extends android.support.v4.app.Fragment implements IMediaPlayerServiceClient {

    private StatefulMediaPlayer mMediaPlayer;
    private StreamStation mSelectedStream = RadioList.DEFAULT_STREAM_STATION;
    private MediaPlayerService mService;
    private boolean mBound;
    private ProgressDialog mProgressDialog;
    private OnFragmentInteractionListener mListener;
    View rootView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListenFragment newInstance(String param1, String param2) {
        ListenFragment fragment = new ListenFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public ListenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        //getActivity().setContentView(R.layout.fragment_listen);
        bindToService();
        mProgressDialog = new ProgressDialog(getActivity().getBaseContext());

        //initializeButtons();
        //setupStationPicker();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_listen, container, false);
        initializeButtons();
        setupStationPicker();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * Binds to the instance of MediaPlayerService. If no instance of
     * MediaPlayerService exists, it first starts a new instance of the service.
     */
    public void bindToService() {
        Intent intent = new Intent(this.getActivity().getBaseContext(), MediaPlayerService.class);

        if (mediaPlayerServiceRunning()) {
            // Bind to Service
            getActivity().getBaseContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        // no instance of service
        else {
            // start service and bind to it
            getActivity().getBaseContext().startService(intent);
            getActivity().getBaseContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }

    }

    /**
     * Sets up the stationPicker spinner
     */
    public void setupStationPicker() {
        Spinner stationPicker = (Spinner) rootView.findViewById(R.id.stationPicker);
        StreamStationSpinnerAdapter adapter = new StreamStationSpinnerAdapter(
                getActivity().getBaseContext(), android.R.layout.simple_spinner_item);

        // populate adapter with stations
        for (StreamStation st : RadioList.ALl_STATIONS) {
            adapter.add(st);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationPicker.setAdapter(adapter);
        stationPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                StreamStation selectedStreamStation = (StreamStation) parent
                        .getItemAtPosition(pos);

                if (selectedStreamStation != mSelectedStream) {
                    mService.stopMediaPlayer();
                    mSelectedStream = selectedStreamStation;
                    mService.initializePlayer(mSelectedStream);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }

        });
    }

    /**
     * Initializes buttons by setting even handlers and listeners, etc.
     */
    private void initializeButtons() {
        // PLAY/PAUSE BUTTON
        final ToggleButton playPauseButton = (ToggleButton) rootView.findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mBound) {
                    mMediaPlayer = mService.getMediaPlayer();

                    // pressed pause ->pause
                    if (!playPauseButton.isChecked()) {

                        if (mMediaPlayer.isStarted()) {
                            mService.pauseMediaPlayer();
                        }

                    }

                    // pressed play
                    else if (playPauseButton.isChecked()) {
                        // STOPPED, CREATED, EMPTY, -> initialize
                        if (mMediaPlayer.isStopped()
                                || mMediaPlayer.isCreated()
                                || mMediaPlayer.isEmpty()) {
                            mService.initializePlayer(mSelectedStream);
                        }

                        // prepared, paused -> resume play
                        else if (mMediaPlayer.isPrepared()
                                || mMediaPlayer.isPaused()) {
                            mService.startMediaPlayer();
                        }

                    }
                }
            }

        });
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder serviceBinder) {
            Log.d("MainActivity", "service connected");

            // bound with Service. get Service instance
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) serviceBinder;
            mService = binder.getService();

            // send this instance to the service, so it can make callbacks on
            // this instance as a client
            mService.setClient(ListenFragment.this);
            mBound = true;

            // Set play/pause button to reflect state of the service's contained
            // player
            final ToggleButton playPauseButton = (ToggleButton) rootView.findViewById(R.id.playPauseButton);
            playPauseButton.setChecked(mService.getMediaPlayer().isPlaying());

            // Set station Picker to show currently set stream station
            Spinner stationPicker = (Spinner) rootView.findViewById(R.id.stationPicker);
            if (mService.getMediaPlayer() != null
                    && mService.getMediaPlayer().getStreamStation() != null) {
                for (int i = 0; i < RadioList.ALl_STATIONS.length; i++) {
                    if (mService.getMediaPlayer().getStreamStation()
                            .equals(RadioList.ALl_STATIONS[i])) {
                        stationPicker.setSelection(i);
                        mSelectedStream = (StreamStation) stationPicker
                                .getItemAtPosition(i);
                    }

                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService = null;
        }
    };

    /**
     * Determines if the MediaPlayerService is already running.
     *
     * @return true if the service is running, false otherwise.
     */
    private boolean mediaPlayerServiceRunning() {

        ActivityManager manager = (ActivityManager) getActivity().getBaseContext().getSystemService(getActivity().ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if ("com.damagination.habarihub.mediaplayer.MediaPlayerService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public void onInitializePlayerSuccess() {
        mProgressDialog.dismiss();

        final ToggleButton playPauseButton = (ToggleButton) rootView.findViewById(R.id.playPauseButton);
        playPauseButton.setChecked(true);
    }

    public void onInitializePlayerStart() {
        mProgressDialog = ProgressDialog.show(getActivity(), "Habari Hub Radio",
                "Connecting...", true);
        mProgressDialog.getWindow().setGravity(Gravity.TOP);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                ListenFragment.this.mService.resetMediaPlaer();
                final ToggleButton playPauseButton = (ToggleButton) rootView.findViewById(R.id.playPauseButton);
                playPauseButton.setChecked(false);
            }

        });

    }

    @Override
    public void onError() {
        mProgressDialog.cancel();
    }

    /**
     * Closes unbinds from service, stops the service, and calls finish()
     */
    public void shutdownActivity() {

        if (mBound) {
            mService.stopMediaPlayer();
            // Detach existing connection.
            getActivity().unbindService(mConnection);
            mBound = false;
        }

        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        getActivity().stopService(intent);

        getActivity().finish();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            mService.unRegister();
            getActivity().getBaseContext().unbindService(mConnection);
            mBound = false;
        }
    }

    public void onBackPressed(){
        super.getActivity().onBackPressed();
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
        public void onFragmentInteraction(Uri uri);
    }

}
