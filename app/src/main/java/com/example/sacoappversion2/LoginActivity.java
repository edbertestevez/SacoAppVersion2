package com.example.sacoappversion2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    public String jsonObject;
    Button btnSubmit;
    TextView tvResult;
    EditText etEmail, etPassword;
    String strResult;
    String userid, username;
    String prename="myspref";
    SharedPreferences sharedpref;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSubmit = (Button) findViewById(R.id.email_sign_in_button);
        btnSubmit.setOnClickListener(this);
        tvResult = (TextView) findViewById(R.id.tvResult);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {
        String subEmail, subPassword;
        subEmail = etEmail.getText().toString();
        subPassword = etPassword.getText().toString();
        // Check if network is available
        if(isNetworkAvailable()) {
            // Run AsyncTask LoginParser
            new LoginActivity.LoginParser().execute("http://192.168.43.103/saco/login/userauth.php?username=" + subEmail + "&password=" + subPassword);
        }
    }

    class LoginParser extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Verifying....");
            //dialog.setTitle("Retrieving data...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected void onPostExecute(String result){
            dialog.cancel();
            if(strResult.equals("true")){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Toast.makeText(LoginActivity.this, "Result: "+strResult, Toast.LENGTH_SHORT).show();
                sharedpref = getSharedPreferences(prename, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpref.edit();
                editor.putString("USERNAME", username);
                editor.putString("USERID", userid);
                editor.putBoolean("LOGGED", true);
                editor.commit();
                startActivity(intent);
            }else if(strResult.equals("false")){
                tvResult.setText("INCORRECT PASSWORD");
            }
        }
        public void onProgressUpdate(Void... args) {

        }
        @Override
        protected String doInBackground(String... urls) {
            String data = null;
            jsonObject = "";
            try{
                String link = (String) urls[0];
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (is, "UTF-8") );
                while ((data = reader.readLine()) != null){
                    jsonObject += data + "\n";
                }
                Log.i("JSon Object: "," " + jsonObject);
                JSONObject jsono = new JSONObject(jsonObject);
                JSONArray jarray = jsono.getJSONArray("login");

                String result="";
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);
                    strResult = object.getString("auth");
                    userid = object.getString("userid");
                    username = object.getString("username");
                }
                return result;
            }catch(Exception e){

            }
            return null;
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
