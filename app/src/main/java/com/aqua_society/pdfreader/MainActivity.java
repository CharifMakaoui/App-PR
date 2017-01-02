package com.aqua_society.pdfreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.melnykov.fab.FloatingActionButton;

import java.util.EmptyStackException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RelativeLayout StartSection, SplashScreen;

    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;

    FloatingActionButton shareButton;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        StartSplash();
        GetAdsCodes();
    }

    private void initView() {
        StartSection = (RelativeLayout) findViewById(R.id.StartSection);
        SplashScreen = (RelativeLayout) findViewById(R.id.SplashScreen);

        shareButton = (FloatingActionButton) findViewById(R.id.shareButton);
        shareButton.setVisibility(View.GONE);

        LinearLayout start = (LinearLayout) findViewById(R.id.ShowBook);
        TextView ShowBook_text = (TextView) findViewById(R.id.ShowBook_text);

        LinearLayout ourApps = (LinearLayout) findViewById(R.id.OurApps);
        LinearLayout AboutAuteur = (LinearLayout) findViewById(R.id.AboutAuteur);
        LinearLayout VoteUs = (LinearLayout) findViewById(R.id.VoteUs);




        int currentPage = utils.getCurrentPosition(getBaseContext());
        if(currentPage > 0)
            ShowBook_text.setText("أكمل القراءة صفحة : "+(currentPage + 1));

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                else{
                    workToDoAfterCloseAdd();
                }
            }
        });

        ourApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OurApps.class);
                MainActivity.this.startActivity(intent);
            }
        });

        VoteUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id="+getPackageName()));

                try{
                    startActivity(intent);
                }
                catch(Exception e){
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="
                            +getPackageName()));
                }
                startActivity(intent);
            }
        });
    }

    private void workToDoAfterCloseAdd(){
        Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
        MainActivity.this.startActivity(intent);
        finish();
    }

    private void StartSplash() {

        SplashScreen.setVisibility(View.VISIBLE);
        StartSection.setVisibility(View.GONE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.setVisibility(View.GONE);
                StartSection.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }

    private void GetAdsCodes(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.dropbox.com/s/gmonpx7kzmtm4pz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApi admobCodes = retrofit.create(MyApi.class);

        Call<AdsObject> connection = admobCodes.getAdsConfig();
        connection.enqueue(new Callback<AdsObject>() {
            @Override
            public void onResponse(Call<AdsObject> call, Response<AdsObject> response) {
                if(!utils.protection(getBaseContext()))
                    throw new EmptyStackException();
                Log.d("data","Nice Get Admob Codes From File URL "+ response.body().getBannerAd());
                if(response.body().getBannerAd() != null)
                    utils.bannerAd_CODE = response.body().getBannerAd();

                if(response.body().getInterstitialAd() != null)
                    utils.interstitialAd_CODE = response.body().getInterstitialAd();

                if(response.body().getNativeAd() != null)
                    utils.nativeAd_CODE = response.body().getNativeAd();

                start();
            }

            @Override
            public void onFailure(Call<AdsObject> call, Throwable t) {
                Log.d("data","Error Get Admob Codes From File URL");
                start();
            }
        });

    }

    private void start() {
        native_AdsInit();
        interstitial_AddInit();
    }

    // Ads Functions :
    private void native_AdsInit() {
        LinearLayout container = (LinearLayout) findViewById(R.id.adView_native);
        native_adView = utils.nativAds(getBaseContext(), container);
    }

    private void interstitial_AddInit() {
        mInterstitialAd = utils.interstitialAds(getBaseContext());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                workToDoAfterCloseAdd();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(utils.adRequest());
    }

    public void setShareIntent(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello World :D!");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "شارك التطبيق"));
    }
}
