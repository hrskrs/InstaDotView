package com.hrskrs.instadotssample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hrskrs.instadotlib.InstaDotView;

public class MainActivity extends AppCompatActivity {

    private int page = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button increase = findViewById(R.id.increase_btn);
        Button decrease = findViewById(R.id.decrease_btn);
        final TextView counter = findViewById(R.id.counter_view);
        final InstaDotView instaDotView = findViewById(R.id.instadot);
        final EditText visibleDots = findViewById(R.id.visible_dots_edittext);
        final EditText itemSize = findViewById(R.id.item_size_edittext);
        Button updateVisibleDots = findViewById(R.id.updatebtn);

        instaDotView.setNoOfPages(20);
        updateVisibleDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(visibleDots.getText().toString()))
                    instaDotView.setVisibleDotCounts(Integer.parseInt(visibleDots.getText().toString()));

                if (!TextUtils.isEmpty(itemSize.getText().toString()))
                    instaDotView.setNoOfPages(Integer.parseInt(itemSize.getText().toString()));

                page = 0;
                counter.setText(page + "");
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                if (page > instaDotView.getNoOfPages() - 1) page = instaDotView.getNoOfPages() - 1;
                counter.setText(page + "");
                instaDotView.onPageChange(page);
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page--;
                if (page < 0) page = 0;
                counter.setText(page + "");
                instaDotView.onPageChange(page);
            }
        });
    }
}
