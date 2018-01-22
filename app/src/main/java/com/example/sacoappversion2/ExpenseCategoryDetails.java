package com.example.sacoappversion2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseCategoryDetails extends AppCompatActivity {

    DBController dbController;
    ListView catExpenseList;
    String posid, catname, filter, total;
    TextView tvCategory, tvTotal;
    String selected_posid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_category_details);

        Bundle extras = getIntent().getExtras();
        posid = extras.getString("POSID");
        catname = extras.getString("CATNAME");
        filter = extras.getString("FILTER");
        total = extras.getString("TOTAL_SUM");

        tvCategory = (TextView) findViewById(R.id.tvName);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvCategory.setText(catname+"("+filter+")");
        tvTotal.setText("P"+total);

        dbController = DBController.getInstance(getApplicationContext());

        Cursor cursor = dbController.getAllCategorySpecific(posid);

        String[] columns = new String[]{
                DBController.AMOUNT,
                DBController.DATE,
        };
        int[] to = new int[]{
                R.id.moneyName,
                R.id.moneyDate,
        };

        SimpleCursorAdapter catExpenseAdapter = new SimpleCursorAdapter(
                this, R.layout.moneylist_layout,
                cursor,
                columns,
                to,0);

        catExpenseList = (ListView) findViewById(R.id.catexpenselist);
        catExpenseList .setAdapter(catExpenseAdapter);
        this.registerForContextMenu(catExpenseList);


        catExpenseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter)catExpenseList.getAdapter()).getCursor();
                cursor.moveToPosition(position);

                selected_posid = cursor.getString(cursor.getColumnIndex("_id"));
                Toast.makeText(getApplicationContext(), "POSID: "+selected_posid, Toast.LENGTH_SHORT).show();
                Log.i("POSITION", selected_posid);
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.catexpenselist) {
            getMenuInflater().inflate(R.menu.menu_remove,menu);
            menu.setHeaderTitle("Delete Expense Record?");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniYes:
                dbController.deleteExpenseRecord(selected_posid);
                Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Expense Successfully Deleted",Toast.LENGTH_LONG).show();
                break;
            case R.id.mniNo:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onContextItemSelected(item);
    }

}
