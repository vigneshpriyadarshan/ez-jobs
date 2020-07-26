package com.example.jobportal.employer;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jobportal.R;
import com.example.jobportal.helper.ExpandableListData;

public class PostedJobsListFragment extends ListFragment {

    OnJobSelectedListener jobSelectedListener;
    static String key;

    public interface OnJobSelectedListener {
        public void onJobSelected(int jobId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            jobSelectedListener = (OnJobSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String s = (String) (l.getItemAtPosition(position));
        String jobId = s.split("-")[0].replace(" ", "");
        ApplicantsList.jobId = jobId;
        key = jobId;
        ExpandableListData.getData();
        // Notify the parent activity of selected item
        jobSelectedListener.onJobSelected(Integer.parseInt(jobId));

        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layout = R.layout.list_view_custom;
        if (JobsPosted.postedJobs == null) {
            setListAdapter(new ArrayAdapter<String>(getActivity(), layout, new String[0]));
        } else {
            setListAdapter(new ArrayAdapter<String>(getActivity(), layout, JobsPosted.postedJobs));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
