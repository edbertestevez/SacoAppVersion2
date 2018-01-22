package com.example.sacoappversion2;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavingsFragmentToday extends Fragment {

    SimpleCursorAdapter savingsAdapter;
    DBController dbController;
    ListView savingsList;
    String selected_posid;
    double totalSavings;
    View view;
    TextView tvTotalSavings, tvPicker;

    public SavingsFragmentToday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_savings, container, false);
        dbController = DBController.getInstance(getActivity().getApplicationContext());
        tvTotalSavings = (TextView) view.findViewById(R.id.tvTotalSavings);
        tvPicker = (TextView) view.findViewById(R.id.tvPicker);
        tvPicker.setText("Savings for Today");
        totalSavings = dbController.totalTodaySavings();
        if(totalSavings%1==0){
            tvTotalSavings.setText("P"+String.format("%.0f",totalSavings));
        } else{
            tvTotalSavings.setText("P"+String.format("%.2f",totalSavings));
        }
        Cursor cursor = dbController.getTodaySavings();

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
