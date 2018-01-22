package com.example.sacoappversion2;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragmentAll extends Fragment{

    SimpleCursorAdapter expenseAdapter;
    DBController dbController;
    ListView expenseList;
    String selected_posid, selected_catname, selected_total, strAmount;
    double totalExpense;
    View view;
    TextView tvTotalExpenses, tvPicker;
    FloatingActionButton btnFloatAdd;
    EditText etAmount;
    Button btnSave;
    Double expensesRecord;
    Cursor cursorCat;
    Spinner spnCategory;
    String posid, catname;
    //PARA SA DISPLAY

    //PARA SA LISTVIEW SAMPLE
    Toolbar mToolbar;

    public ExpenseFragmentAll() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expense, container, false);
        tvTotalExpenses = (TextView) view.findViewById(R.id.tvTotalExpenses);
        tvPicker = (TextView) view.findViewById(R.id.tvPicker);
        tvPicker.setText("Total Expenses");
        dbController = DBController.getInstance(getActivity().getApplicationContext());
        totalExpense = dbController.totalAllExpense();
        if(totalExpense%1==0){
            tvTotalExpenses.setText("P"+String.format("%.0f",totalExpense));
        } else{
            tvTotalExpenses.setText("P"+String.format("%.2f",totalExpense));
        }

        expenseList = (ListView) view.findViewById(R.id.expenselist);

        Cursor cursor = dbController.getAllCategoryExpense();

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
                intent.putExtra("FILTER", "All");
                startActivityForResult(intent,0);
            }
        });



        btnFloatAdd = (FloatingActionButton) getActivity().findViewById(R.id.btnFloatAdd);
        btnFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        return view;
    }

}
