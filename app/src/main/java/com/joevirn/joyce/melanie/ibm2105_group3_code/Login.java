package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private HistoryListOpenHelper mDb;

    EditText mEtUsername_login;
    EditText mEtPassword_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtUsername_login = findViewById(R.id.etUsername_login);
        mEtPassword_login = findViewById(R.id.etPassword_login);
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public void btnLogin_clicked(View view) {
        //check empty field for username
        if (isEmpty(mEtUsername_login)) {
            mEtUsername_login.setError("Username is required!");
            mEtUsername_login.requestFocus();
        }
        //check empty field for password
        else if (isEmpty(mEtPassword_login)) {
            mEtPassword_login.setError("Password is required!");
            mEtPassword_login.requestFocus();
        }
        //validate username and password
        else {
            try {
                String username = mEtUsername_login.getText().toString();
                String password = mEtPassword_login.getText().toString();

                //Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

                //correct credentials
                //if (username.equals("admin") && password.equals("admin")) {
                if (mDb.checkPassword(username, password)) {
                    //open MainActivity
                    Intent intent_loginSuccess = new Intent(this, MainActivity.class);
                    startActivity(intent_loginSuccess);
                    Toast.makeText(this, "Login Successful!\nWelcome!", Toast.LENGTH_SHORT).show();

                    //close current activity
                    this.finish();
                }
                //invalid credentials
                else {
                    Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                }

        }catch (Exception e) {
                //Log.d("Query Exception: ", e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();



            }
        }
    }

    public void tvRegister_clicked(View view) {
        Intent intent_register = new Intent(this, Register.class);
        startActivity(intent_register);
    }
}
