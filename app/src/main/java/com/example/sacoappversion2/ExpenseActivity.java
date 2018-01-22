package com.example.sacoappversion2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ExpenseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    DBController dbController;
    String prename="myspref";
    SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("My Expenses");

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

    private void setupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExpenseFragmentAll(),"All");
        adapter.addFragment(new ExpenseFragmentToday(),"Today");
        adapter.addFragment(new ExpenseFragmentMonth(),"Month");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle("Saco App");
            Intent intent = new Intent(ExpenseActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_savings) {
            Intent intent = new Intent(ExpenseActivity.this, SavingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_expenses) {
            Intent intent = new Intent(ExpenseActivity.this, ExpenseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_borrow_return) {
            Intent intent = new Intent(ExpenseActivity.this, BorrowReturnActivity.class);
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
                Intent intent = new Intent(ExpenseActivity.this, LoginActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "INTERNET CONNECTION NEEDED", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
