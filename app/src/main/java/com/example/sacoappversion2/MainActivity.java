package com.example.sacoappversion2;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent i_savings, i_expenses, i_br, i_goals;
    Fragment fragment;
    DBController dbController;
    String userid;
    public String jsonObject, strResult;
    String prename="myspref";
    SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the SharedPreferences object
        sharedpref = getSharedPreferences(prename, MODE_PRIVATE);
        // Retrieve the saved values

        userid = sharedpref.getString("USERID", null);

        if(isNetworkAvailable()) {
            // Run AsyncTask LoginParser
            new MainActivity.RetrieveDbParser().execute("http://192.168.43.103/saco/login/user_db.php?userid="+userid);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("Saco App");
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();

        insertCategoryFunction("Clothing");
        insertCategoryFunction("Entertainment");
        insertCategoryFunction("Bills");
        insertCategoryFunction("Food and Dining");
        insertCategoryFunction("Transportation");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle("Saco App");
            HomeFragment homeFragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
        }
        else if (id == R.id.nav_savings) {
            Intent intent = new Intent(MainActivity.this, SavingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_expenses) {
            Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_borrow_return) {
            Intent intent = new Intent(MainActivity.this, BorrowReturnActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            if(isNetworkAvailable()) {
                dbController.clearBorrow();
                dbController.clearExpense();
                dbController.clearReturn();
                dbController.clearSavings();
                sharedpref = getSharedPreferences(prename, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpref.edit();
                editor.putString("USERNAME", null);
                editor.putString("USERID", null);
                editor.putBoolean("LOGGED", false);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "INTERNET CONNECTION NEEDED", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void insertCategoryFunction(String catname){
        dbController = DBController.getInstance(getApplicationContext());
        CategoryData categoryData = new CategoryData();
        categoryData.catname = catname;
        if(!dbController.ifCategoryExists(catname)==true || dbController.getCategoryCount()==0) {
            dbController.insertCategory(categoryData);
        }
    }

    private void insertRecordFunction(String borName, double amount, String category, String note){
        BorrowData borrowData = new BorrowData();
        borrowData.borname = borName;
        borrowData.amount = amount;
        borrowData.category = category;
        borrowData.note = note;

        dbController = DBController.getInstance(getApplicationContext());
        dbController.insertContentBorrow(borrowData);
    }

    class RetrieveDbParser extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Syncing Data....");
            //dialog.setTitle("Retrieving data...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected void onPostExecute(String result){
            dialog.cancel();
            //tvData.setText(strResult);
            Toast.makeText(MainActivity.this, "Sync Successful", Toast.LENGTH_SHORT).show();
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
