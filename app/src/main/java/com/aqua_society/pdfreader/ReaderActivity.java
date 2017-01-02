package com.aqua_society.pdfreader;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;


public class ReaderActivity extends AppCompatActivity {


    FrameLayout activity_reader;
    PDFView pdfView;
    ImageButton previous, next;
    Button btn_go_to;
    ImageView btn_fullScreen;

    TextView toolbar_page_location;
    EditText input_go_to;

    Toolbar toolbar;

    int currentPage = 0;

    int nbr_page_load_add = 5;

    private boolean hideControllers = false, isFullScreen = false;

    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        initView();
        initPDF();
        initActionBar();
        initView();
        btn_Clicks();
        interstitial_AddInit();

    }

    private void initView() {
        activity_reader = (FrameLayout) findViewById(R.id.activity_reader);

        pdfView = (PDFView) findViewById(R.id.pdfView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        previous = (ImageButton) findViewById(R.id.previous);
        next = (ImageButton) findViewById(R.id.next);

        btn_fullScreen = (ImageView) findViewById(R.id.btn_fullScreen);

        toolbar_page_location = (TextView) findViewById(R.id.toolbar_page_location);
        input_go_to = (EditText) findViewById(R.id.input_go_to);
        btn_go_to = (Button) findViewById(R.id.btn_go_to);

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }
    }

    private void btn_Clicks() {
        btn_go_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int page = Integer.parseInt(input_go_to.getText().toString());
                if (page >= 0 && page < pdfView.getPageCount()) {
                    currentPage = page - 1;
                }
                pdfView.jumpTo(currentPage);
                hideKeybord();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage <= 0) {
                    currentPage = 0;
                } else {
                    currentPage--;
                }
                pdfView.jumpTo(currentPage);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage >= pdfView.getPageCount()) {
                    currentPage = pdfView.getPageCount() - 1;
                } else {
                    currentPage++;
                }
                pdfView.jumpTo(currentPage);
            }
        });

        btn_fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    isFullScreen = true;
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    isFullScreen = false;
                }

            }
        });
    }

    private boolean scrollDown = true;
    private float lastScroll = 0;

    private void initPDF() {
        pdfView.fromAsset("pdf_file.pdf")
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(utils.getCurrentPosition(getBaseContext()))
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        currentPage = utils.getCurrentPosition(getBaseContext());
                        pdfView.jumpTo(currentPage);
                        toolbar_page_location.setText("/" + nbPages);
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        currentPage = page;
                        input_go_to.setText("" + (currentPage + 1));
                        utils.SaveCurrentPage(getBaseContext(), currentPage);
                        ShowInterstitial(currentPage);

                    }
                })

                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        if (positionOffset > lastScroll)
                            scrollDown = true;
                        else
                            scrollDown = false;
                        lastScroll = positionOffset;

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (scrollDown)
                                    Hide_Controllers(true);
                                else {
                                    Hide_Controllers(false);
                                }
                            }
                        }, 500);
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError (Throwable t){

                    }
                })
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .load();

        pdfView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                if (!hideControllers) {
                    Hide_Controllers(true);
                    hideControllers = true;
                } else {
                    Hide_Controllers(false);
                    hideControllers = false;
                }


            }
        });
    }

    private void Hide_Controllers(boolean show){
        if (show) {
            previous.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            hideControllers = true;
        } else {
            previous.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            hideControllers = false;
        }
    }

    public void hideKeybord() {
        InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity_reader.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if(utils.isInternetAvailable()){
            finish();
            Intent intent = new Intent(ReaderActivity.this, OurApps.class);
            startActivity(intent);
        }
    }

    public void setShareIntent(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello World :D!");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "شارك التطبيق"));
    }

    private void ShowInterstitial(int page) {
        if ((page % nbr_page_load_add) == 0) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    private void interstitial_AddInit() {
        mInterstitialAd = utils.interstitialAds(getBaseContext());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(utils.adRequest());
    }
}
