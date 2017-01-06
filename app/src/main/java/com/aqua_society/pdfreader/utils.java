package com.aqua_society.pdfreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MrCharif on 01/01/2017.
 */

public class utils {

    public static String CURRENT_PAGE_POSITION_GLOBALE = "current_page_position";

    public static String bannerAd_CODE = "";
    public static String interstitialAd_CODE = "";
    public static String nativeAd_CODE = "";

    public static List<Apps> APPS_LIST = new ArrayList<>();


    public static void SaveCurrentPage(Context context, int position) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);

        prefs.edit().putInt(utils.CURRENT_PAGE_POSITION_GLOBALE, position).apply();
    }

    public static int getCurrentPosition(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(utils.CURRENT_PAGE_POSITION_GLOBALE, 0);
    }

    public static boolean protection(Context context) {
        //http://www.unit-conversion.info/texttools/convert-text-to-binary/
        //https://www.base64encode.org/
        String protection_code = "MDExMDAwMTEgMDExMDExMTEgMDExMDExMDEgMDAxMDExMTAgMDExMDAwMDEgMDExMTAwMDEgMDExMTAxMDEgMDExMDAwMDEgMDEwMTExMTEgMDExMTAwMTEgMDExMDExMTEgMDExMDAwMTEgMDExMDEwMDEgMDExMDAxMDEgMDExMTAxMDAgMDExMTEwMDEgMDAxMDExMTAgMDExMTAwMDAgMDExMDAxMDAgMDExMDAxMTAgMDExMTAwMTAgMDExMDAxMDEgMDExMDAwMDEgMDExMDAxMDAgMDExMDAxMDEgMDExMTAwMTA=";
        String p_name = context.getPackageName();

        byte[] valueDecoded = Base64.decode(protection_code, 0);
        String str = utils.int2str(new String(valueDecoded));

        return str.equals(p_name);
    }

    public static String int2str(String s) {
        String[] ss = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s1 : ss) {
            sb.append((char) Integer.parseInt(s1, 2));
        }
        return sb.toString();
    }

    public static AdView bannerAds(Context context, View view) {
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);

        if (!utils.bannerAd_CODE.equals("") && utils.bannerAd_CODE != null) {
            mAdView.setAdUnitId(utils.bannerAd_CODE);
        } else {
            mAdView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
        }

        ((RelativeLayout) view).addView(mAdView);
        mAdView.loadAd(utils.adRequest());

        return mAdView;
    }

    public static NativeExpressAdView nativAds(Context context, View view) {
        NativeExpressAdView native_adView = new NativeExpressAdView(context);
        native_adView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 132));

        if (utils.nativeAd_CODE != null && !utils.nativeAd_CODE.equals("")) {
            native_adView.setAdUnitId(utils.nativeAd_CODE);
        } else {
            native_adView.setAdUnitId(context.getString(R.string.native_ad_unit_id));
        }


        ((LinearLayout) view).addView(native_adView);
        native_adView.loadAd(utils.adRequest());

        return native_adView;
    }

    public static AdRequest adRequest() {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        return request;
    }

    public static InterstitialAd interstitialAds(Context context) {
        InterstitialAd interstitialAd = new InterstitialAd(context);

        if (!utils.nativeAd_CODE.equals("") && utils.interstitialAd_CODE != null) {
            interstitialAd.setAdUnitId(utils.interstitialAd_CODE);
        } else {
            interstitialAd.setAdUnitId(context.getString(R.string.interstitial_ad_unit_id));
        }

        return interstitialAd;
    }

    public static boolean isInternetAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void generateApps(){
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
                utils.APPS_LIST = response.body();
            }

            @Override
            public void onFailure(Call<List<Apps>> call, Throwable t) {
                Log.d("ourApps","no fuck");
            }
        });
    }
}
