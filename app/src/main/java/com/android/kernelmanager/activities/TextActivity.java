
package com.android.kernelmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.android.kernelmanager.R;
import com.android.kernelmanager.utils.Utils;


public class TextActivity extends BaseActivity {

    public static final String MESSAGE_INTENT = "message_intent";
    public static final String SUMMARY_INTENT = "summary_intent";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        String message = getIntent().getStringExtra(MESSAGE_INTENT);
        final String url = getIntent().getStringExtra(SUMMARY_INTENT);

        if (message != null)
            ((TextView) findViewById(R.id.message_text)).setText(message);
        if (url != null)
            findViewById(R.id.help_fab).setOnClickListener(
                    v -> Utils.launchUrl(url, TextActivity.this));
    }

}
