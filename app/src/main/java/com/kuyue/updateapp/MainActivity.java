package com.kuyue.updateapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kuyue.updateapp.service.UpdateService;
import com.kuyue.updateapp.utils.ToastUtils;

import static android.R.attr.id;

/**
 * 主Activity
 */
public class MainActivity extends AppCompatActivity {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, UpdateService.class);
//                intent.putExtra("apkUrl", );//要更新包的地址
//                startService(intent);
                ToastUtils.getInstance().show("已经是最新版！");
            }
        });
    }
}
