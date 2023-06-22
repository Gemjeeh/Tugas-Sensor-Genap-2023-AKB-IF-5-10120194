package com.OmanSubadri_10120194_IF5;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private RecyclerView rvMenu;
    static ArrayList<String> arrayList = new ArrayList<>();
    static ArrayList<Integer> image = new ArrayList<>();
    private MainDrawerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.ivMenu);
        rvMenu = findViewById(R.id.rvMenu);

        arrayList.clear();

        arrayList.add("INFO");
        arrayList.add("LOKASI SEKARANG");
        arrayList.add("LOKASI MAKANAN");
        arrayList.add("PROFILE");
        arrayList.add("KELUAR");

        image.add(R.drawable.baseline_info_24);
        image.add(R.drawable.baseline_my_location_24);
        image.add(R.drawable.baseline_location_searching_24);
        image.add(R.drawable.baseline_person_24);
        image.add(R.drawable.baseline_exit_to_app_24);

        adapter = new MainDrawerAdapter(this, arrayList, image);

        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        rvMenu.setAdapter(adapter);

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}

//10120194_OmanSubadri_IF5