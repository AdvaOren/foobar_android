package com.example.foobar_dt_ad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteAccount extends AppCompatActivity {
    public final static int FROM_DELETE_SCREEN = 101;
    public final static int DEAD = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnRegret = findViewById(R.id.btnRegret);

        btnDelete.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("toDelete",true);
            setResult(FROM_DELETE_SCREEN,intent);
            finish();
        });

        btnRegret.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("toDelete",false);
            setResult(FROM_DELETE_SCREEN,intent);
            finish();
        });
    }
}