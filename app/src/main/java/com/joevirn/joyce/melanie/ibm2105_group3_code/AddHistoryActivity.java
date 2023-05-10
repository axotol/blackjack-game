package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class AddHistoryActivity extends AppCompatActivity {
    private static final String TAG = AddHistoryActivity.class.getSimpleName();

    private static final int NO_ID = -99;
    private static final String NO_WORD = "";

    private EditText mEtUserName;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;

    int mId = MainActivity.HISTORY_ADD;
    public static final String EXTRA_UN = "USERNAME";
    public static final String EXTRA_PW = "PASSWORD";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtUserName = (EditText) findViewById(R.id.etUsername_register);
        mEtPassword = (EditText) findViewById(R.id.etPassword_register);
        mEtConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword_register);

        Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            int id = extras.getInt(HistoryListAdapter.EXTRA_ID, NO_ID);
            String word = extras.getString(HistoryListAdapter.EXTRA_WORD, NO_WORD);
            if (id != NO_ID && word != NO_WORD) {
                mId = id;
                mEtUserName.setText(word);
            }
        } // Otherwise, start with empty fields.
    }
}
