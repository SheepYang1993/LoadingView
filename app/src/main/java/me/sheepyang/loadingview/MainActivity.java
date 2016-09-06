package me.sheepyang.loadingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.sheepyang.loadingview.view.LoadingView;

public class MainActivity extends AppCompatActivity {

    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingView = (LoadingView) findViewById(R.id.loading);
    }

    public void start(View view) {
        loadingView.start();
    }

    public void reset(View view) {
        loadingView.reset();
    }
}
