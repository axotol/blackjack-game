package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    private HistoryListOpenHelper mDb;
    EditText mEtUsername_register;
    EditText mEtPassword_register;
    EditText mEtConfirmPassword_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtUsername_register = findViewById(R.id.etUsername_register);
        mEtPassword_register = findViewById(R.id.etPassword_register);
        mEtConfirmPassword_register = findViewById(R.id.etConfirmPassword_register);

        mDb = new HistoryListOpenHelper(this);
        SQLiteDatabase d = mDb.getReadableDatabase();
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean checkEmptyFields() {
        if (isEmpty(mEtUsername_register)) {
            mEtUsername_register.setError("Username is required!");
            mEtUsername_register.requestFocus();
            return false;
        }
        else if (isEmpty(mEtPassword_register)) {
            mEtPassword_register.setError("Password is required!");
            mEtPassword_register.requestFocus();
            return false;
        }
        else if (isEmpty(mEtConfirmPassword_register)) {
            mEtConfirmPassword_register.setError("Confirm Password is required!");
            mEtConfirmPassword_register.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    boolean validatePassword() {
        String val = mEtPassword_register.getText().toString();
        String passwordVal = "^" +
                "(?=.*[0-9])" +   //at least 1 digit
                "(?=.*[a-z])" +   //at least 1 lower case letter
                "(?=.*[A-Z])" +   //at least 1 upper case letter
                ".{5,}" +         //at least 5 characters
                "$";

        if (val.isEmpty()) {
            mEtPassword_register.setError("Password cannot be empty!");
            mEtPassword_register.requestFocus();
            return false;
        }
        else if (!val.matches(passwordVal)) {
            mEtPassword_register.setError("Password must be at least 5 characters with minimum 1 digit, 1 lowercase & 1 uppercase!");
            mEtPassword_register.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    boolean validateConfirmPassword() {
        String confirmPasswordVal = mEtConfirmPassword_register.getText().toString();
        String passwordVal = mEtPassword_register.getText().toString();

        if (!confirmPasswordVal.equals(passwordVal)) {
            mEtConfirmPassword_register.setError("Password do not match!");
            mEtConfirmPassword_register.requestFocus();
            return false;
        }
        else {
            return true;
        }

    }

    boolean validateUsername(){
        String userName = mEtUsername_register.getText().toString();

        boolean checkResult = mDb.check(userName);
        return checkResult;
    }

    public void btnRegister_clicked(View view) {

        //try {
            boolean test = validateUsername();
            /*if (test == false) {
                Toast.makeText(this, "Fail to register", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            //Log.d("Query Exception: ", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();


        }*/
        int historyID = (int)mDb.count()+1;
        String username = mEtUsername_register.getText().toString();
        String password = mEtPassword_register.getText().toString();

        if(checkEmptyFields() && validatePassword() && validateConfirmPassword() && !test) {
            //open Login Activity
            Intent intent_registerSuccess = new Intent(this, Login.class);
            startActivity(intent_registerSuccess);
            mDb.insertTableUser(historyID, mEtUsername_register.getText().toString(), mEtPassword_register.getText().toString());
            Toast.makeText(this, "Registration successful!\nYou may login now!", Toast.LENGTH_SHORT).show();

            //close current activity
            this.finish();
        }else{
            Toast.makeText(this, "Fail to register", Toast.LENGTH_SHORT).show();
        }
    }

    public void tvLogin_clicked(View view) {
        Intent intent_login = new Intent(this, Login.class);
        startActivity(intent_login);
    }
}