package com.example.sacoappversion2;


import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class brBorrowFragment extends Fragment {

    View view;
    ListView brList;
    TextView tvFilter, tvBR, tvTotal;
    DBController dbController;
    String selected_posid;
    FloatingActionButton btnFloatAdd;
    Spinner spnCategory;
    EditText etName, etAmount, etNote;
    Button btnSave;
    String borName, strAmount,category,note;
    double amount;

    public brBorrowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_borrow_return, container, false);

        tvFilter = (TextView) view.findViewById(R.id.tvFilter);
        tvFilter.setText("Total Borrowed Record");

        dbController = DBController.getInstance(getActivity().getApplicationContext());

        tvTotal = (TextView) view.findViewById(R.id.tvTotal);
        tvTotal.setText(""+dbController.totalBorrowRecord());

        Cursor cursor = dbController.fetchBorrowContents();

        String[] columns = new String[]{
                DBController.BOR_NAME,
                DBController.AMOUNT,
                DBController.NOTE,
                DBController.CATEGORY,
        };
        int[] to = new int[]{
                R.id.tvName,
                R.id.tvAmount,
                R.id.tvNote,
                R.id.tvCategory,
        };

        SimpleCursorAdapter brAdapter = new SimpleCursorAdapter(
                getActivity(), R.layout.brlist_layout,
                cursor,
                columns,
                to,0);

        brList = (ListView) view.findViewById(R.id.brlist);
        brList .setAdapter(brAdapter);

        brList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter)brList.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                selected_posid = cursor.getString(cursor.getColumnIndex("_id"));
                return false;
            }
        });
        this.registerForContextMenu(brList);

        brList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter)brList.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                selected_posid = cursor.getString(cursor.getColumnIndex("_id"));
                Toast.makeText(getActivity().getApplicationContext(), "POSID: "+selected_posid, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), BorrowInfo.class);
                intent.putExtra("POSID", selected_posid);
                startActivityForResult(intent,0);
            }
        });
        btnFloatAdd = (FloatingActionButton) view.findViewById(R.id.btnFloatAdd);
        btnFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_addborrowreturn, null);

                tvBR = (TextView) mView.findViewById(R.id.tvBR);
                spnCategory = (Spinner) mView.findViewById(R.id.spnCategory);
                etName = (EditText) mView.findViewById(R.id.etName);
                etAmount = (EditText) mView.findViewById(R.id.etAmount);
                etNote = (EditText) mView.findViewById(R.id.etNote);
                btnSave = (Button) mView.findViewById(R.id.btnSave);

                tvBR.setText("Borrower Name");
                Resources res = getResources();
                String[] list = res.getStringArray(R.array.br_category);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        list);
                spnCategory.setAdapter(adapter);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        borName = etName.getText().toString();
                        strAmount = etAmount.getText().toString();
                        category = spnCategory.getSelectedItem().toString();
                        note = etNote.getText().toString();

                        if(borName.isEmpty() || borName.length() == 0 || borName.equals("") || borName == null){
                            etName.setError("Please enter borrower name");
                        } else if(strAmount.isEmpty() || strAmount.length() == 0 || strAmount.equals("") || strAmount == null){
                            etAmount.setError("Please enter amount/quantity");
                        }else {
                            if (category.equals("Item")) {
                                if (note.isEmpty() || note.length() == 0 || note.equals("") || note == null) {
                                    etNote.setError("Please enter item name");
                                } else {
                                    amount = Double.parseDouble(etAmount.getText().toString());
                                    insertRecordFunction(borName,amount,category,note);
                                    dialog.dismiss();
                                    getActivity().finish();
                                    startActivity(getActivity().getIntent());
                                }
                            } else {
                                amount = Double.parseDouble(etAmount.getText().toString());
                                insertRecordFunction(borName,amount,category,note);
                                dialog.dismiss();
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
                            }
                        }
                    }
                });
            }
        });
        return view;
    }

    private void insertRecordFunction(String borName, double amount, String category, String note){
        BorrowData borrowData = new BorrowData();
        borrowData.borname = borName;
        borrowData.amount = amount;
        borrowData.category = category;
        borrowData.note = note;

        dbController = DBController.getInstance(getActivity().getApplicationContext());
        dbController.insertContentBorrow(borrowData);
        Toast.makeText(getActivity().getApplicationContext(), "Saved Borrow Record", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.brlist) {
            getActivity().getMenuInflater().inflate(R.menu.menu_remove,menu);
            menu.setHeaderTitle("Option");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniYes:
                dbController.deleteBorrowRecord(selected_posid);
                Intent intent_remove = new Intent(getActivity().getApplicationContext(), BorrowReturnActivity.class);
                startActivity(intent_remove);
                Toast.makeText(getActivity().getApplicationContext(), "Successfully removed from list",Toast.LENGTH_LONG).show();
                break;
            case R.id.mniNo:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onContextItemSelected(item);
    }
}
