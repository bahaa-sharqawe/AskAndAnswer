package com.orchidatech.askandanswer.Activity;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.orchidatech.askandanswer.R;

public class TermsActivity extends AppCompatActivity {
    TextView tv_accept;
    ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        resizeLogo();
        tv_accept = (TextView) this.findViewById(R.id.tv_accept);
        tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TermsActivity.this, Categories.class));
            }
        });
    }
    private void resizeLogo() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int)(screenSize.y*0.25);
        iv_logo.getLayoutParams().width = (int)(screenSize.y*0.25);
    }

}
