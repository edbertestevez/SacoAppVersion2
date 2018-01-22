package com.example.sacoappversion2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

public class SampleJSONValues extends AppCompatActivity {
    TextView tvData;
    String userid;
    public String jsonObject, strResult;
    DBController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_jsonvalues);

        tvData = (TextView) findViewById(R.id.tvData);

        Bundle extras = getIntent().getExtras();
        userid = extras.getString("USERID");

        if(isNetworkAvailable()) {
            // Run AsyncTask LoginParser
            new SampleJSONValues.RetrieveDbParser().execute("http://192.168.0.101/saco/login/user_db.php?userid="+userid);
        }
    }

    class RetrieveDbParser extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SampleJSONValues.this);
            dialog.setMessage("Syncing Data....");
            //dialog.setTitle("Retrieving data...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected void onPostExecute(String result){
            dialog.cancel();
            //tvData.setText(strResult);

            Intent intent = new Intent(SampleJSONValues.this, MainActivity.class);
            intent.putExtra("USERID", userid);
            startActivity(intent);
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
                strResult="";
                Log.i("JSon Object: "," " + jsonObject);
                JSONObject jsono = new JSONObject(jsonObject);

                //HANDLE SAVINGS
                JSONArray jarray = jsono.getJSONArray("savings");
                String result="";
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);
                    insertSyncSavingsFunction(object.getString("id"), Double.parseDouble(object.getString("amount")), object.getString("date_added"),object.getString("time_added"));
                    //Log.i("List check: "," " + result);
                }

                //EXPENSES
                JSONArray expArray = jsono.getJSONArray("expense");
                for (int i = 0; i < expArray.length(); i++) {
                    JSONObject object = expArray.getJSONObject(i);
                    insertSyncExpenseFunction(object.getString("id"), Double.parseDouble(object.getString("amount")), object.getString("date_added"),object.getString("time_added"), object.getString("cat_id"));
                    //Log.i("List check: "," " + result);
                }

                //BORROW
                JSONArray borArray = jsono.getJSONArray("borrow");
                for (int i = 0; i < borArray.length(); i++) {
                    JSONObject object = borArray.getJSONObject(i);
                    insertSyncBorrowFunction(object.getString("id"), object.getString("bor_name"), object.getString("category"),object.getString("item_name"), Double.parseDouble(object.getString("quantity")));
                    //Log.i("List check: "," " + result);
                }

                //RETURN
                JSONArray retArray = jsono.getJSONArray("return");
                for (int i = 0; i < retArray.length(); i++) {
                    JSONObject object = retArray.getJSONObject(i);
                    insertSyncReturnFunction(object.getString("id"), object.getString("lender_name"), object.getString("category"),object.getString("item_name"), Double.parseDouble(object.getString("quantity")));
                    //Log.i("List check: "," " + result);
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

    private void insertSyncSavingsFunction(String id, double amount, String date, String time) throws ParseException {
        SavingsData savingsData = new SavingsData();
        savingsData.amount = amount;
        savingsData._id = id;
        savingsData.date = date;
        savingsData.time = time;
        dbController = DBController.getInstance(getApplicationContext());
        dbController.insertSyncSavings(savingsData);
    }

    private void insertSyncExpenseFunction(String id, double amount, String date, String time, String category) throws ParseException {
        ExpenseData expenseData = new ExpenseData();
        expenseData.amount = amount;
        expenseData._id = id;
        expenseData.date = date;
        expenseData.time = time;
        expenseData.category = category;
        dbController = DBController.getInstance(getApplicationContext());
        dbController.insertSyncExpense(expenseData);
    }
    private void insertSyncBorrowFunction(String id, String borname, String category, String note, double qty) throws ParseException {
        BorrowData borrowData = new BorrowData();
        borrowData._id = id;
        borrowData.borname = borname;
        borrowData.category = category;
        borrowData.note = note;
        borrowData.amount = qty;
        dbController = DBController.getInstance(getApplicationContext());
        dbController.insertContentBorrow(borrowData);
    }

    private void insertSyncReturnFunction(String id, String retname, String category, String note, double qty) throws ParseException {
        ReturnData returnData = new ReturnData();
        returnData._id = id;
        returnData.retname = retname;
        returnData.category = category;
        returnData.note = note;
        returnData.amount = qty;
        dbController = DBController.getInstance(getApplicationContext());
        dbController.insertContentReturn(returnData);
    }
}
