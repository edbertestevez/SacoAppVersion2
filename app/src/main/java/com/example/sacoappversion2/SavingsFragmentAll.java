package com.example.sacoappversion2;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavingsFragmentAll extends Fragment {

    SimpleCursorAdapter savingsAdapter;
    DBController dbController;
    ListView savingsList;
    String selected_posid, strAmount;
    double totalSavings;
    View view;
    TextView tvTotalSavings, tvPicker;
    FloatingActionButton btnFloatAdd;
    EditText etAmount;
    Button btnSave;
    Double savingsRecord;

    public SavingsFragmentAll() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_savings, container, false);
        tvTotalSavings = (TextView) view.findViewById(R.id.tvTotalSavings);
        tvPicker = (TextView) view.findViewById(R.id.tvPicker);
        tvPicker.setText("Total Savings");
        dbController = DBController.getInstance(getActivity().getApplicationContext());
        totalSavings = dbController.totalAllSavings();
        if(totalSavings%1==0){
            tvTotalSavings.setText("P"+String.format("%.0f",totalSavings));
        } else{
            tvTotalSavings.setText("P"+String.format("%.2f",totalSavings));
        }

        Cursor cursor = dbController.getAllSavings();

        String[] columns = new String[]{
                DBController.AMOUNT,
                DBController.DATE,
        };
        int[] to = new int[]{
                R.id.moneyName,
                R.id.moneyDate,
        };

        savingsAdapter = new SimpleCursorAdapter(
                getActivity(), R.layout.moneylist_layout,
                cursor,
                columns,
                to,0);

        savingsList = (ListView) view.findViewById(R.id.savingslist);
        savingsList .setAdapter(savingsAdapter);

        if(totalSavings==0){
            Toast.makeText(getActivity().getApplicationContext(), "No record exists",Toast.LENGTH_SHORT).show();
        }
        this.registerForContextMenu(savingsList);

        savingsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter)savingsList.getAdapter()).getCursor();
                cursor.moveToPosition(position);

                selected_posid = cursor.getString(cursor.getColumnIndex("_id"));

                Log.i("POSITION", selected_posid);
                return false;
            }
        });

        btnFloatAdd = (FloatingActionButton) getActivity().findViewById(R.id.btnFloatAdd);
        btnFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                dbController.insertSavings(savingsData);
                                Toast.makeText(getActivity().getApplicationContext(), "Savings successfully added", Toast.LENGTH_SHORT).show();
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
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.savingslist) {
            getActivity().getMenuInflater().inflate(R.menu.menu_remove,menu);
            menu.setHeaderTitle("Delete Savings Record?");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniYes:
                dbController.deleteSavingsRecord(selected_posid);
                Intent intent = new Intent(getActivity().getApplicationContext(), SavingsActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity().getApplicationContext(), "Savings Successfully Deleted",Toast.LENGTH_LONG).show();
                break;
            case R.id.mniNo:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onContextItemSelected(item);
    }

}
