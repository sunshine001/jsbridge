package com.example.cisco.sourcecode;

import android.app.Activity;
import android.os.Bundle;

import com.cisco.jsapi.JSApiBridge;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSApiBridge.work("native://api/toast?p=%7b%22title%22%3a%22%e5%bc%80%e6%ba%90%e7%a4%be%e5%8c%ba%22%7d");
        JSApiBridge.work("native://api/show?p=%7b%22title%22%3a%22%e5%bc%80%e6%ba%90%e7%a4%be%e5%8c%ba%22%7d");
        JSApiBridge.work("native://api/other?p=%7b%22title%22%3a%22%e5%bc%80%e6%ba%90%e7%a4%be%e5%8c%ba%22%7d");
    }

}
