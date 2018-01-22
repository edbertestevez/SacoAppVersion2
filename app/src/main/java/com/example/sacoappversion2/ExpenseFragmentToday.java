package com.example.sacoappversion2;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseFragmentToday extends Fragment {

    SimpleCursorAdapter expenseAdapter;
    DBController dbController;
    ListView expenseList;
    String selected_posid, selected_catname, selected_total, strAmount;
    double totalExpense;
    View view;
    TextView tvTotalExpenses, tvPicker;

    public ExpenseFragmentToday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expense, container, false);
        tvTotalExpenses = (TextView) view.findViewById(R.id.tvTotalExpenses);
        tvPicker = (TextView) view.findViewById(R.id.tvPicker);
        tvPicker.setText("Expenses for Today");
        dbController = DBController.getInstance(getActivity().getApplicationContext());
        totalExpense = dbController.totalTodayExpense();
        if(totalExpense%1==0){
            tvTotalExpenses.setText("P"+String.format("%.0f",totalExpense));
        } else{
            tvTotalExpenses.setText("P"+String.format("%.2f",totalExpense));
        }

        expenseList = (ListView) view.findViewById(R.id.expenselist);

        Cursor cursor = dbController.getTodayCategoryExpense();

        String[] columns = new String[]{
                DBController.CATNAME,
                DBController.TOTAL_SUM,
        };
        int[] to = new int[]{
                R.id.tvName,
                R.id.tvAmount,
        };

        expenseAdapter = new SimpleCursorAdapter(
                getActivity(), R.layout.expenselist_layout,
                cursor,
                columns,
                to,0);

        expenseList = (ListView) view.findViewById(R.id.expenselist);
        expenseList .setAdapter(expenseAdapter);

        if(totalExpense==0){
            Toast.makeText(getActivity().getApplicationContext(), "No record exists",Toast.LENGTH_SHORT).show();
        }
        this.registerForContextMenu(expenseList);

        expenseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter)expenseList.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                selected_posid = cursor.getString(cursor.getColumnIndex("_id"));
                selected_catname = cursor.getString(cursor.getColumnIndex("catname"));
                selected_total = cursor.getString(cursor.getColumnIndex("total_sum"));
                Intent intent = new Intent(getActivity().getApplicationContext(), ExpenseCategoryDetails.class);
                intent.putExtra("POSID", selected_posid);
                intent.putExtra("CATNAME", selected_catname);
                intent.putExtra("TOTAL_SUM", selected_total);
                intent.putExtra("FILTER", "Today");
                startActivityForResult(intent,0);
            }
        });
        return view;
    }

}
