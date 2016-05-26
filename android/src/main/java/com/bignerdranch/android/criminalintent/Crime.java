package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    /** represents the date a crime occurred */
    private Date mDate;
    /** represents whether the crime has been solved */
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID(); // generate unique identifier
        mDate = new Date(); // sets mDate to the current date (the default date for a crime)
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }
}
