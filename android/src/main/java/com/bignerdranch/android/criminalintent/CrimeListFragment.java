package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* set the title of the fragment's host activity;
           this title will be displayed on the action bar (or title bar on older devices) */
        getActivity().setTitle(R.string.crimes_title);

        /* get the CrimeLab singleton and then get the list of crimes */
        mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

        ArrayAdapter<Crime> adapter =
                new ArrayAdapter<Crime>(getActivity(),
                                        android.R.layout.simple_list_item_1,
                                        mCrimes);

        setListAdapter(adapter);
    }
}
