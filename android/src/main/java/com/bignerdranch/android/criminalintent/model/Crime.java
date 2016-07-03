package com.bignerdranch.android.criminalintent.model;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    /** represents the date a crime occurred */
    private Date mDate;
    /** represents whether the crime has been solved */
    private boolean mSolved;
    /** holds the name of a suspect */
    private String mSuspectName;
    /** holds the phone number of a suspect */
    private String mSuspectPhoneNumber;

    public Crime() {
        this(UUID.randomUUID()); // generate unique identifier and pass it to alternative constructor
    }

    public Crime(UUID id) {
        mId = id;
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

    public boolean isSolved() {
        return mSolved;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }

    public String getSuspectName() {
        return mSuspectName;
    }

    public void setSuspectName(String suspectName) {
        this.mSuspectName = suspectName;
    }

    public String getSuspectPhoneNumber() {
        return mSuspectPhoneNumber;
    }

    public void setSuspectPhoneNumber(String suspectPhoneNumber) {
        this.mSuspectPhoneNumber = suspectPhoneNumber;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
