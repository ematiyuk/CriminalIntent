package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;
import com.bignerdranch.android.criminalintent.service.DateTimeFormat;
import com.bignerdranch.android.criminalintent.service.PictureUtils;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

public class CrimeFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_CONTACT = 0;
    private static final int REQUEST_PHOTO = 1;

    private Crime mCrime;
    private File mPhotoFile;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;
    private Calendar mCalendar;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* retrieve crimeId argument from Fragment Bundle */
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);

        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mCrime.getDate());

        mPhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);

        setHasOptionsMenu(true); // turn on options menu handling
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // specify which fields we want the query to return values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };
            // perform the query - the contactUri is like a "where" clause here
            Cursor contactCursor = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            if (contactCursor == null) {
                return;
            }

            try {
                // double-check that we actually got results
                if (contactCursor.getCount() == 0) {
                    return;
                }

                // pull out the first column of the first row of data
                if (contactCursor.moveToFirst()) {
                    String contactId = contactCursor.getString(0); // _ID
                    String name = contactCursor.getString(1); // DISPLAY_NAME
                    String hasPhoneNumber = contactCursor.getString(2); // HAS_PHONE_NUMBER

                    // checks whether contact has at least one phone number
                    if (hasPhoneNumber.equals("1")) {
                        String phoneNumber = retrieveContactPhoneNumber(contactId);

                        if (phoneNumber != null) {
                            mCrime.setSuspectPhoneNumber(phoneNumber);
                            mCallSuspectButton.setEnabled(true);
                        }
                    } else {
                        mCrime.setSuspectPhoneNumber(null);
                        mCallSuspectButton.setEnabled(false);
                    }

                    mCrime.setSuspectName(name);
                    updateCrime();
                    mSuspectButton.setText(name);
                }
            } finally {
                contactCursor.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateCrime();
            updatePhotoView();
        }
    }

    private void updateCrime() {
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    public void updateDate() {
        mDateButton.setText(DateTimeFormat.getDateString(getActivity(), mCrime.getDate()));
    }

    public void updateTime() {
        mTimeButton.setText(DateTimeFormat.getTimeString(getActivity(), mCrime.getDate()));
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mCrime.setTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // this space intentionally left blank
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // this one too
            }
        });
        mTitleField.requestFocus();

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.newInstance(CrimeFragment.this,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show(getActivity().getFragmentManager(), DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get system default hour format
                boolean is24HourMode = DateTimeFormat.is24HourFormat(getActivity());

                TimePickerDialog.newInstance(CrimeFragment.this,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE), is24HourMode)
                        .show(getActivity().getFragmentManager(), DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // set the crime's solved property
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject,
                                getString(R.string.app_name)))
                        .setType("text/plain")
                        .getIntent();
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspectName() != null) {
            mSuspectButton.setText(mCrime.getSuspectName());
        }

        mCallSuspectButton = (Button) v.findViewById(R.id.call_crime_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callContact = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + mCrime.getSuspectPhoneNumber()));
                startActivity(callContact);
            }
        });

        if (mCrime.getSuspectPhoneNumber() == null) {
            mCallSuspectButton.setEnabled(false);
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
            mCallSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhotoView.getDrawable() != null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ImageFragment.newInstance(mPhotoFile.getAbsolutePath())
                            .show(fm, DIALOG_IMAGE);
                } else {
                    Toast.makeText(getActivity(), R.string.no_photo_text, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        updatePhotoView();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.delete_crime)
                        .setMessage(R.string.delete_crime_dialog_msg)
                        .setNeutralButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.delete_crime, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CrimeLab.getInstance(getActivity()).deleteCrime(mCrime);
                                if (null != getActivity().findViewById(R.id.detailFragmentContainer)) {
                                    mCallbacks.onCrimeUpdated(mCrime);
                                    mCallbacks.onCrimeDeleted(mCrime);
                                } else {
                                    getActivity().finish();
                                }
                            }
                        })
                        .setIcon(R.drawable.ic_dialog_alert)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(year, monthOfYear, dayOfMonth);
        mCrime.setDate(mCalendar.getTime());
        updateCrime();
        updateDate();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCrime.setDate(mCalendar.getTime());
        updateCrime();
        updateTime();
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspectName();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private String retrieveContactPhoneNumber(String contactId) {
        Cursor phoneNumberCursor = getActivity().getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactId, null, null);

        if (phoneNumberCursor == null) {
            return null;
        }

        String phoneNumber = null;
        try {
            if (phoneNumberCursor.moveToFirst()) {
                phoneNumber = phoneNumberCursor
                        .getString(phoneNumberCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } finally {
            phoneNumberCursor.close();
        }

        return phoneNumber;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
