package com.fuentesfernandez.dropsy.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuentesfernandez.dropsy.Model.RobotInfo;
import com.fuentesfernandez.dropsy.R;
import com.fuentesfernandez.dropsy.Service.RobotManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ServerInfoFragment extends Fragment implements Observer{

    private RobotManager robotManager;
    private OnFragmentInteractionListener mListener;
    private RobotListAdapter robotListAdapter;
    private String url;
    private boolean connected = false;
    public ServerInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotManager = RobotManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView robotListView = (ListView) getView().findViewById(R.id.robot_list);
        robotListAdapter = new RobotListAdapter(getContext(),0);
        robotListView.setAdapter(robotListAdapter);
        robotManager.addObserver(this);
        Button refreshButton = (Button) getView().findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotManager.connect();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        connected = robotManager.isConnected();
        updateConnectionStatus(connected);
        robotManager.connect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable, Object data) {
        String string = (String) data;
        if (getActivity() != null) {

            if (string.equals("connection")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConnectionStatus(true);
                    }
                });
            }
            if (string.equals("disconnection")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConnectionStatus(false);
                    }
                });
            }
        }
    }

    private void updateConnectionStatus(final Boolean isConnected){
        TextView connection_status = (TextView) getView().findViewById(R.id.connection_status);
        if (isConnected) {
            connection_status.setText("Conectado");
            connection_status.setTextColor(Color.GREEN);
        } else {
            connection_status.setText("Desconectado");
            connection_status.setTextColor(Color.RED);
        }
        connected = isConnected;
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
        void onFragmentInteraction(Uri uri);
    }

    public class RobotListAdapter extends ArrayAdapter implements Observer {
        private List<RobotInfo> robots;
        private Context context;

        public RobotListAdapter(Context context, int resource) {
            super(context, resource);
            this.robots = new ArrayList<>();
            this.robots.addAll(robotManager.getRobots());
            this.context = context;
            robotManager.addObserver(this);
        }

        @Override
        public int getCount() {
            return robots.size();
        }

        @Override
        public Object getItem(int position) {
            return robots.get(position);
        }

        @Override
        public long getItemId(int position) {
            return robots.get(position).getRobot_id();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View robotListItem = getActivity().getLayoutInflater().inflate(R.layout.robot_list_item,null);
            TextView robot_id = (TextView) robotListItem.findViewById(R.id.id);
            robot_id.setText(String.valueOf(robots.get(position).getRobot_id()));
            TextView robot_model = (TextView) robotListItem.findViewById(R.id.model);
            robot_model.setText(robots.get(position).getRobot_model());
            return robotListItem;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void update(Observable observable, Object data) {
            String string = (String) data;
            if (string.equals("robots")){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        robots.clear();
                        robots.addAll(robotManager.getRobots());
                        robotListAdapter.notifyDataSetChanged();
                    }
                });
            }

        }
    }
}
