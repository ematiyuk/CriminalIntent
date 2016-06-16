package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.UUID;

public class CrimeLab {
    private static final String TAG = "CrimeLAb";
    private static final String FILENAME = "crimes.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context appContext) {
        mAppContext = appContext.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mAppContext).getWritableDatabase();

        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading crimes: ", e);
            Toast.makeText(mAppContext, R.string.error_loading_crimes_msg, Toast.LENGTH_LONG).show();
        }
    }

    public static CrimeLab getInstance(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(c);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    public void deleteCrime(Crime crime) {
        mCrimes.remove(crime);
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            Toast.makeText(mAppContext, R.string.error_saving_crimes_msg, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id))
                return crime;
        }
        return null;
    }
}
