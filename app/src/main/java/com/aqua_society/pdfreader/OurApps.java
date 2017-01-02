package com.aqua_society.pdfreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OurApps extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppsAdapter appsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_apps);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        appsAdapter = new AppsAdapter(getBaseContext(),generateApps());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(appsAdapter);
    }


    private List<Apps> generateApps(){
        List<Apps> appsList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.dropbox.com/s/tobqthcuqy5wmml/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApi apps = retrofit.create(MyApi.class);

        Call<List<Apps>> connection = apps.getMyApps();
        connection.enqueue(new Callback<List<Apps>>() {

            @Override
            public void onResponse(Call<List<Apps>> call, Response<List<Apps>> response) {
                Log.d("ourApps","nombre des app : "+response.body().size());
                appsAdapter.update(response.body());
                appsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Apps>> call, Throwable t) {
                Log.d("ourApps","no fuck");
            }
        });

        return appsList;
    }
}
