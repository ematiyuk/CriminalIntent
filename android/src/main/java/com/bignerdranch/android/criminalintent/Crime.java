package com.bignerdranch.android.criminalintent;

import java.text.DateFormat;
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

    @Override
    public String toString() {
        return mTitle;
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

    public String getDateString() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
        return  dateFormat.format(mDate);
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
