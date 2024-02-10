package com.example.lastprojectandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {

    private EditText edtLoginId, edtLoginPassword;
    boolean theme; // 1 is dark and 0 is light
    SharedPreferences themePref;
    private CheckBox chk;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static final String NAME = "NAME";
    public static final String PASS = "PASS";
    public static final String FLAG = "FLAG";
    private boolean flag = false;
    SharedPreferences.Editor themePrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_log_in);
        setupSharedPrefs();
        setupfunction();
        check();

        themePref = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        theme = themePref.getBoolean("nightMode", false);

        if(theme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }
    private void setupfunction() {
        edtLoginId = findViewById(R.id.txtLoginId);
        chk=findViewById (R.id.chk);
        edtLoginPassword = findViewById(R.id.txtLoginPassword);
    }
    private void setupSharedPrefs() {
        prefs= PreferenceManager.getDefaultSharedPreferences (this);
        editor = prefs.edit();
    }

    public void btnLogin(View view) {
        String loginId = edtLoginId.getText().toString();
        String password = edtLoginPassword.getText().toString();

        loginUser(loginId, password);
    }
    public void sginInLogin(View view) {
        Intent intent = new Intent (LogIn.this , SignUp.class);
        startActivity (intent);
    }

    public void resetPasswordbtn(View view){
        Intent intent = new Intent (LogIn.this , VerfyPerson.class);
        startActivity (intent);
    }

    private void loginUser(String loginId, String password) {
        String url = "http://10.0.2.2:5000/student/" + loginId;

        RequestQueue queue = Volley.newRequestQueue(LogIn.this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = response;

                            String result = jsonArray.getString(0);

                            if (!result.equals(null)) {
                                String serverPassword = jsonArray.getString(4);

                                if (password.equals(serverPassword)) {
                                    Intent intent = new Intent(LogIn.this, home.class);
                                    intent.putExtra("id",jsonArray.getString(1));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LogIn.this, "كلمة السر خاطئه", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LogIn.this, "الرقم الحامعي غير مستعمل ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError", error.toString());
                    }
                }
        );
        queue.add(request);
    }

    private void check(){
        flag = prefs.getBoolean (FLAG , false);
        if(flag){
            String name = prefs.getString (NAME,"");
            String password = prefs.getString (PASS ,"");
            edtLoginId.setText (name);
            edtLoginPassword.setText (password);
            chk.setChecked (true);
        }

    }


    public void btnLoginOnClick(View view) {
        String name = edtLoginId.getText().toString();
        String password = edtLoginPassword.getText().toString();

        if(chk.isChecked() ) {
            if (!flag) {
                editor.putString (NAME, name);
                editor.putString (PASS, password);
                editor.putBoolean (FLAG, true);
                editor.commit ();
            }
        }

    }
}