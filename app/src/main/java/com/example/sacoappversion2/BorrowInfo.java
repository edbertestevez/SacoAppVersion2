package com.example.sacoappversion2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BorrowInfo extends AppCompatActivity implements View.OnClickListener {

    DBController dbController;
    String posid, borName, note, strAmount, strBorName, strNote,category;
    double amount, totalBalance, intAmount, amt;
    EditText edtBorrower, edtQty, edtItem;
    Button btnSave,btnCancel;
    int btnValue=0;
    TextView txtCategory, txtItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_info);

        setTitle("Borrowed Debt Info");
        Bundle extras = getIntent().getExtras();
        posid = extras.getString("POSID");
        Log.i("POSITION LOAD",posid);

        dbController = DBController.getInstance(getApplicationContext());
        Cursor cursor = dbController.fetchSpecificBorrow(posid);
        edtBorrower = (EditText) findViewById(R.id.edtLender);
        edtItem = (EditText) findViewById(R.id.edtItem);
        edtQty = (EditText) findViewById(R.id.edtQty);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtItem = (TextView) findViewById(R.id.txtItem);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                borName = cursor.getString(cursor.getColumnIndex("borName"));
                note = cursor.getString(cursor.getColumnIndex("note"));
                amount = cursor.getInt(cursor.getColumnIndex("amount"));
                if(amount%1==0){
                    edtQty.setText(String.format("%.0f",amount));
                } else{
                    edtQty.setText(String.format("%.2f",amount));
                }
                category = cursor.getString(cursor.getColumnIndex("category"));
                cursor.moveToNext();
            }
            if(category.equals("Item")){
                txtCategory.setText("Borrowed Item");
                txtItem.setText("Item Name");
            }else if(category.equals("Money")){
                txtCategory.setText("Borrowed Money");
                txtItem.setText("Note");
            }
            loadDefaultEditText();
            totalBalance = dbController.getTotalBalance()+amount; //TUNGOD KAY I EDIT MO DAPAT I BALIK SA DAPAT NA AMOUNT :D
        }
    }

    public void loadDefaultEditText(){
        edtBorrower.setText(borName);
        edtItem.setText(note);
        edtQty.setText(""+amount);
        edtBorrower.requestFocus();
        //KEY LISTENERS PARA DI MA EDIT DANAY
        edtBorrower.setTag(edtBorrower.getKeyListener());
        edtItem.setTag(edtItem.getKeyListener());
        edtQty.setTag(edtQty.getKeyListener());
        edtBorrower.setKeyListener(null);
        edtItem.setKeyListener(null);
        edtQty.setKeyListener(null);
    }

    @Override
    public void onClick(View v) {
        strAmount = edtQty.getText().toString();
        strBorName = edtBorrower.getText().toString();
        strNote = edtItem.getText().toString();
        switch (v.getId()){
            case R.id.btnSave:{
                if(btnValue==1) {
                    if (strBorName.isEmpty() || strBorName.length() == 0 || strBorName.equals("") || strBorName == null) {
                        edtBorrower.setError("Please enter borrower name");
                    } else if (strNote.isEmpty() || strNote.length() == 0 || strNote.equals("") || strNote == null) {
                        edtItem.setError("Please enter item name");
                    } else if (strAmount.isEmpty() || strAmount.length() == 0 || strAmount.equals("") || strAmount == null) {
                        edtQty.setError("Please enter amount");
                    } else {
                        intAmount = Double.parseDouble(strAmount);
                        if (!isNotZero(intAmount)) {
                            edtQty.setError("Please enter valid amount/qty");
                        }else{
                            Intent intent = new Intent(BorrowInfo.this, BorrowReturnActivity.class);
                            dbController.editBorrowRecord(posid, strBorName, strNote, intAmount);
                            Toast.makeText(getApplicationContext(), "Record has been updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }
                }else{
                    edtBorrower.setKeyListener((KeyListener) edtBorrower.getTag());
                    edtItem.setKeyListener((KeyListener) edtItem.getTag());
                    edtQty.setKeyListener((KeyListener) edtQty.getTag());
                    btnSave.setText("SAVE CHANGES");
                    btnCancel.setText("CANCEL");
                    btnCancel.setVisibility(View.VISIBLE);
                    edtBorrower.setEnabled(true);
                    edtBorrower.requestFocus();
                    btnValue=1;
                }
            }break;
            case R.id.btnCancel:{
                if(btnValue==1){
                    loadDefaultEditText();
                    btnSave.setText("EDIT RECORD");
                    btnCancel.setText("ALREADY RETURNED");
                    btnValue=0;
                }else if(btnValue==0){
                    Intent intent = new Intent(BorrowInfo.this, BorrowReturnActivity.class);
                    dbController.deleteBorrowRecord(posid);
                    Toast.makeText(getApplicationContext(), "Item has been returned", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }break;

        }
    }

    private boolean isNotZero(double amount) {
        if (amount != 0) {
            return true;
        }
        return false;
    }
}
