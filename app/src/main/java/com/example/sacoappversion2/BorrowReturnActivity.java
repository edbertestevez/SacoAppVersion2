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

public class BorrowReturnActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener{

    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    String prename="myspref";
    SharedPreferences sharedpref;
    DBController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_return);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("Borrow and Return Items");

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        dbController = DBController.getInstance(getApplicationContext());
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
        adapter.addFragment(new brBorrowFragment(),"Borrow");
        adapter.addFragment(new brReturnFragment(),"Return");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle("Saco App");
            Intent intent = new Intent(BorrowReturnActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_savings) {
            Intent intent = new Intent(BorrowReturnActivity.this, SavingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_expenses) {
            Intent intent = new Intent(BorrowReturnActivity.this, ExpenseActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_borrow_return) {
            Intent intent = new Intent(BorrowReturnActivity.this, BorrowReturnActivity.class);
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
                Intent intent = new Intent(BorrowReturnActivity.this, LoginActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_borrow_return, menu);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        Toast.makeText(this, "Value: "+tab, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
