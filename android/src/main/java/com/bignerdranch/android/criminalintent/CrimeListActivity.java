package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            // start an instance of CrimePagerActivity
            Intent intent = new Intent(this, CrimePagerActivity.class);
            intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);

            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            if (crime != null) {
                Fragment newDetail = CrimeFragment.newInstance(crime.getId());
                ft.add(R.id.detailFragmentContainer, newDetail);
            }
            ft.commit();
        }
    }

    @Override
    public void onCrimeCreated(Crime crime) {
        Intent intent = new Intent(this, CrimeActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
        startActivity(intent);
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeLab.getInstance(this).updateCrime(crime);

        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment)
                fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment currentDetail = fm.findFragmentById(R.id.detailFragmentContainer);

        if (currentDetail != null) {
            ft.remove(currentDetail);
        }

        ft.commit();
    }
}
