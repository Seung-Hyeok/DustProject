package com.example.dustproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.dustproject.common.AddLocationDialogFragment;
import com.example.dustproject.db.LocationRealmObject;
import com.example.dustproject.finddust.FineDustContract;
import com.example.dustproject.finddust.FineDustFragment;
import com.example.dustproject.util.GeoUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_CODE_FINE_COARSE_PERMISSION = 1000;


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    List<Pair<Fragment, String>> mFragmentList;

    private FusedLocationProviderClient mFusedLocationClient; // 위치정보를 얻으려면 FusedLocationProviderClient 사용해야함

    private AppBarConfiguration mAppBarConfiguration;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("아무거나");

        mRealm = Realm.getDefaultInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() { // FloatingActionButton 동작코드
            @Override
            public void onClick(View view) {
                AddLocationDialogFragment.newInstance(new AddLocationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(final String city) {
                        GeoUtil.getLocationFromName(MainActivity.this, city, new GeoUtil.GeoUtilListener() {
                            @Override
                            public void onSuccess(double lat, double lng) {
                                saveNewCity(lat, lng, city);
                                addNewFragment(lat, lng, city);
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(MainActivity.this, message,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show(getSupportFragmentManager(),"dialog");
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);

        setUpViewPager();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
    public void saveNewCity(double lat, double lng, String city){
        mRealm.beginTransaction();
        LocationRealmObject newLocationRealmObject = mRealm.createObject(LocationRealmObject.class);
        newLocationRealmObject.setName(city);
        newLocationRealmObject.setLat(lat);
        newLocationRealmObject.setLng(lng);
        mRealm.commitTransaction();
    }

    private void addNewFragment(double lat, double lng, String city){ // 현재위치 리스트에 추가하는 코드
        mFragmentList.add(new Pair<Fragment, String>(
                FineDustFragment.newInstance(lat,lng),city
        ));
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_all_delete){
            mRealm.beginTransaction();
            mRealm.where(LocationRealmObject.class).findAll().deleteAllFromRealm();
            mRealm.commitTransaction();
            setUpViewPager();
            return true;
        }else if (id == R.id.action_delete){
            if(mTabLayout.getSelectedTabPosition() - 1 < 0){
                Toast.makeText(this,"현재 위치 탭은 삭제할 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
            mRealm.beginTransaction();
            mRealm.where(LocationRealmObject.class).findAll().get(mTabLayout.getSelectedTabPosition()-1).deleteFromRealm();
            mRealm.commitTransaction();
            setUpViewPager();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private void setUpViewPager() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        loadDbData();
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void loadDbData() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new Pair<Fragment, String>(
                new FineDustFragment(), "현재 위치"
        ));
        RealmResults<LocationRealmObject> realmResults = mRealm.where(LocationRealmObject.class).findAll();
        for(LocationRealmObject realmObject : realmResults){
            mFragmentList.add(new Pair<Fragment, String>(
                    FineDustFragment.newInstance(realmObject.getLat(),realmObject.getLng()),realmObject.getName()
            ));
        }
    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_FINE_COARSE_PERMISSION);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            FineDustContract.View view = (FineDustContract.View) mFragmentList.get(0).first;
                            view.reload(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_FINE_COARSE_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                getLastKnownLocation();
            }
        }
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter{

        private List<Pair<Fragment, String>> mFragmentList;

        public MyPagerAdapter(@NonNull FragmentManager fm, List<Pair<Fragment, String>> fragmentList) {
            super(fm);
            this.mFragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).first;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).second;
        }
    }
}