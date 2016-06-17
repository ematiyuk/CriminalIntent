package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context appContext) {
        mAppContext = appContext.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mAppContext).getWritableDatabase();
    }

    public static CrimeLab getInstance(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(c);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime crime) {
    }

    public void deleteCrime(Crime crime) {
    }

    public ArrayList<Crime> getCrimes() {
        return new ArrayList<Crime>();
    }

    public Crime getCrime(UUID id) {
        return null;
    }
}
