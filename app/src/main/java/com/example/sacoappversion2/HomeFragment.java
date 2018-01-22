package com.example.sacoappversion2;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    TextView txtName, tvBalance;
    Button btnBR, btnAdd, btnSavings,  btnSubtract;
    Intent i_savings, i_expenses, i_br, i_add, i_subtract;
    DBController dbController;
    double totalBalance;
    View view;
    EditText etAmount;
    Button btnExpense, btnBorrowReturn, btnSave;
    Double savingsRecord, expensesRecord;
    String strAmount;
    String prename="myspref";
    SharedPreferences sharedpref;
    Spinner spnCategory;
    Cursor cursorCat;
    String posid, catname;
    String jsonObject;
    public String userid;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        dbController = DBController.getInstance(getActivity().getApplicationContext());
        tvBalance = (TextView) view.findViewById(R.id.tvBalance);
        totalBalance = dbController.getTotalBalance();
        if(totalBalance%1==0){
            tvBalance.setText("P"+String.format("%.0f",totalBalance));
        } else{
            tvBalance.setText("P"+String.format("%.2f",totalBalance));
        }

        sharedpref = getActivity().getSharedPreferences(prename, MODE_PRIVATE);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtName.setText(sharedpref.getString("USERNAME", null));
        userid = sharedpref.getString("USERID", null);
        Toast.makeText(getActivity(), "USERID: "+userid, Toast.LENGTH_SHORT).show();
        btnSavings = (Button) view.findViewById(R.id.btnSavings);
        btnExpense = (Button) view.findViewById(R.id.btnExpense);
        btnBorrowReturn = (Button) view.findViewById(R.id.btnBR);
        btnSubtract = (Button) view.findViewById(R.id.btnSubtract);
        btnSavings.setOnClickListener(this);
        btnExpense.setOnClickListener(this);
        btnBorrowReturn.setOnClickListener(this);
        btnSubtract.setOnClickListener(this);
        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:{
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_addsavings, null);
                etAmount = (EditText) mView.findViewById(R.id.etAmount);
                btnSave = (Button) mView.findViewById(R.id.btnSave);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        strAmount = etAmount.getText().toString().trim();

                        if (strAmount.isEmpty() || strAmount.length() == 0 || strAmount.equals("") || strAmount == null) {
                            etAmount.setError("Please enter amount");
                        } else {
                            savingsRecord = Double.parseDouble(strAmount);
                            if (savingsRecord != 0) {
                                SavingsData savingsData = new SavingsData();
                                savingsData.amount = savingsRecord;
                                dbController = DBController.getInstance(getActivity().getApplicationContext());
                                    if(isNetworkAvailable()){
                                        dbController.insertSavings(savingsData);
                                        double amt = savingsRecord;
                                        int intUser = Integer.parseInt(sharedpref.getString("USERID", null));
                                        Toast.makeText(getActivity().getApplicationContext(), "Savings successfully added", Toast.LENGTH_SHORT).show();
                                        new ActionParser().execute("http://192.168.43.103/saco/login/user_action.php?action=addsavings&amt="+savingsRecord+"&userid="+intUser);
                                        dialog.dismiss();
                                        getActivity().finish();
                                        startActivity(getActivity().getIntent());
                                    }else{
                                            dbController.insertSavings(savingsData);
                                            dialog.dismiss();
                                            getActivity().finish();
                                            startActivity(getActivity().getIntent());
                                            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }

                            }
                            else{
                                etAmount.setError("Invalid Amount");
                            }
                        }
                    }
                });
                break;
            }

            case R.id.btnSubtract:{
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_addexpense, null);
                etAmount = (EditText) mView.findViewById(R.id.etAmount);
                btnSave = (Button) mView.findViewById(R.id.btnSave);
                spnCategory = (Spinner) mView.findViewById(R.id.spnCategory);
                cursorCat = dbController.fetchCategoryContents();

                String[] columns = new String[]{DBController.CATNAME,};
                int[] to = new int[]{R.id.txt,};

                SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(getActivity(), R.layout.spinner_layout, cursorCat, columns, to,0);

                spnCategory.setAdapter(categoryAdapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        posid = cursorCat.getString(cursorCat.getColumnIndex("_id"));
                        catname = cursorCat.getString(cursorCat.getColumnIndex("catname"));
                        Toast.makeText(getActivity().getApplicationContext(), "POSID: "+posid, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        strAmount = etAmount.getText().toString().trim();

                        if (strAmount.isEmpty() || strAmount.length() == 0 || strAmount.equals("") || strAmount == null) {
                            etAmount.setError("Please enter amount");
                        } else {
                            expensesRecord = Double.parseDouble(strAmount);
                            if (expensesRecord != 0) {
                                ExpenseData expenseData = new ExpenseData();
                                expenseData.amount = expensesRecord;
                                expenseData.category = posid;
                                dbController = DBController.getInstance(getActivity().getApplicationContext());
                                dbController.insertExpense(expenseData);
                                Toast.makeText(getActivity().getApplicationContext(), "Expense successfully added", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
                            }
                            else{
                                etAmount.setError("Invalid Amount");
                            }
                        }
                    }
                });
                break;
            }
            case R.id.btnSavings:{
                Intent intent = new Intent(getActivity(), SavingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnExpense:{
                Intent intent = new Intent(getActivity(), ExpenseActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnBR:{
                Intent intent = new Intent(getActivity(), BorrowReturnActivity.class);
                startActivity(intent);
                break;
            }

        }
    }

    //ADD RECORD  //////////////////////////////////////////////////////////////////////////////////////////
    class ActionParser extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Adding record....");
            //dialog.setTitle("Retrieving data...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected void onPostExecute(String result){
            dialog.cancel();
            //tvData.setText(strResult);
            Toast.makeText(getActivity(), "Sucessfully added", Toast.LENGTH_SHORT).show();
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
                String result = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (is, "UTF-8") );
                while ((data = reader.readLine()) != null){
                    jsonObject += data + "\n";
                }
                return result;
            }catch(Exception e){

            }
            return null;
        }
    }
}
