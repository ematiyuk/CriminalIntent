package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends ListFragment {

    private CrimeAdapter mAdapter;

    private boolean mSubtitleVisible;

    private Button mNewCrimeButton;

    private Crime mSelectedCrime;
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
        void onCrimeCreated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* explicitly tell the FragmentManager that our fragment should receive a call to
           onCreateOptionsMenu(...) */
        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mNewCrimeButton = (Button) view.findViewById(R.id.new_crime_button);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCrime();
            }
        });

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                // required, but not used in this implementation
            }

            // ActionMode.Callback methods
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.crime_list_item_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
                // required, but not used in this implementation
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_delete_crime:
                        final int totalItemsNumber = mAdapter.getCount();
                        int selectedItemsNumber = 0;
                        for (int i = totalItemsNumber - 1; i >= 0; i--) {
                            if (getListView().isItemChecked(i))
                                selectedItemsNumber++;
                        }
                        String message = getResources().getQuantityString(
                                R.plurals.dialog_msg_plural, selectedItemsNumber);
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.delete_crime)
                                .setMessage(message)
                                .setNeutralButton(android.R.string.cancel, null)
                                .setPositiveButton(R.string.delete_crime, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        for (int i = totalItemsNumber - 1; i >= 0; i--) {
                                            if (getListView().isItemChecked(i)) {
                                                CrimeLab.getInstance(getActivity())
                                                        .deleteCrime(mAdapter.getItem(i));
                                            }
                                        }
                                        actionMode.finish();

                                        if (null != getActivity()
                                                .findViewById(R.id.detailFragmentContainer)) {
                                            // this check is for tablet version of code only
                                            if (mSelectedCrime != null) {
                                                // .getCrime() returns null if there is no
                                                // current selected crime in db
                                                mCallbacks.onCrimeSelected(CrimeLab
                                                        .getInstance(getActivity())
                                                        .getCrime(mSelectedCrime.getId()));
                                            } else {
                                                mCallbacks.onCrimeSelected(null);
                                            }
                                        }

                                        updateUI();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                // required, but not used in this implementation
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                addNewCrime();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimelab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimelab.getCrimes().size();
        String subtitle = getResources().getQuantityString(
                R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        getActivity().getActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // get the Crime from the adapter
        Crime crime = ((CrimeAdapter) getListAdapter()).getItem(position);
        mSelectedCrime = crime;

        mCallbacks.onCrimeSelected(crime);
    }

    private void addNewCrime() {
        Crime crime = new Crime();
        CrimeLab.getInstance(getActivity()).addCrime(crime);

        updateUI();

        if (getActivity().findViewById(R.id.detailFragmentContainer) != null) {
            mCallbacks.onCrimeSelected(crime);
        } else {
            mCallbacks.onCrimeCreated(crime);
        }
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            setListAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
        }

        updateSubtitle();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(List<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime, null);
            }

            // configure the view for this crime
            Crime crime = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(crime.getTitle());
            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(DateTimeFormat.getDateTimeString(getActivity(), crime.getDate()));
            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(crime.isSolved());

            return convertView;
        }

        public void setCrimes(List<Crime> crimes) {
            this.clear();
            this.addAll(crimes);
        }
    }
}
