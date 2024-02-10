package com.example.lastprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class ResetPass extends AppCompatActivity {

    private Student student;
    private String bzuID;
    private TextView txtNewPass,txtConfirmNewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        bzuID=getIntent().getStringExtra("id");
        readUser(bzuID);

        txtNewPass=findViewById(R.id.txtNewPass);
        txtConfirmNewPass=findViewById(R.id.txtConfirmNewPass);
    }
    public void BackbtnChangPasswod(View view){
        Intent intent = new Intent(this, CodeVerf.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
    }
    public void changePassbtn(View view){
        int check =CheckPassword(txtNewPass.getText().toString());

            if(!(txtNewPass.getText().toString().equals(txtConfirmNewPass.getText().toString()))){
                Toast.makeText(ResetPass.this, "Passwords Miss Match", Toast.LENGTH_SHORT).show();
            }  else if(check == 1){
                Toast.makeText(this, "يجب ان تكون كلمة السر اكبر من 8 خانات", Toast.LENGTH_SHORT).show();
            }else if(check == 2){
                Toast.makeText(this, "يجب ان تحتوي كلمة السر على احرف صغيره", Toast.LENGTH_SHORT).show();
            }else if(check == 3){
                Toast.makeText(this, "يجب ان تحتوي كلمة السر على اخرف كبيره  ", Toast.LENGTH_SHORT).show();
            }else if(check == 4){
                Toast.makeText(this, "يجب ان تحتوي كلمة السر على ارقام", Toast.LENGTH_SHORT).show();
            }else if(check == 5){
                Toast.makeText(this, "يجب ان تحتوي كلمة السر على احرف مميزه (@،$،&،...)", Toast.LENGTH_SHORT).show();
            }
            else {
                updatePassword(bzuID, txtNewPass.getText().toString());
            }

    }
    private int CheckPassword(String password) {
        if(password.length() < 8){
            return 1;
        }
        if(!password.matches(".*[a-z].*")) {
            return 2;
        }

        if(!password.matches(".*[A-Z].*")) {
            return 3;
        }

        if(!password.matches(".*\\d.*")) {
            return 4;
        }

        if(!password.matches(".*[@#$%^&+=!].*")) {
            return 5;
        }
        return 6;
    }
    private void readUser(String loginId) {
        String url = "http://10.0.2.2:5000/student/" + loginId;

        RequestQueue queue = Volley.newRequestQueue(ResetPass.this);

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
                                student=new Student(Integer.parseInt(jsonArray.getString(0)),Integer.parseInt(jsonArray.getString(1)),jsonArray.getString(2),jsonArray.getString(3),jsonArray.getString(4),jsonArray.getString(5));
                            } else {
                                Toast.makeText(ResetPass.this, "invalid ID", Toast.LENGTH_SHORT).show();
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

    private void updatePassword(String loginId, String newPassword) {
        String updatePasswordUrl = "http://10.0.2.2:5000/update_password";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", loginId);
            jsonBody.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(ResetPass.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updatePasswordUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ResetPass.this, "تم تحديث كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPass.this, LogIn.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                        Toast.makeText(ResetPass.this, "فشل تحديث كلمة المرور", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
}