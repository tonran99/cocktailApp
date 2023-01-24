package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Settings extends AppCompatActivity {
int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView textView = findViewById(R.id.textViewCounter);

            counter = getIntent().getIntExtra("INT_COUNTER",0);
            textView.setText(""+counter);

    }


    public void backToMain(View view){
        //siirrytään takaisin mainiin

        Intent intent   = new Intent(this,MainActivity.class);
        startActivity(intent);


        // esimerkki implisiittisestä intentistä
        // kysyy käyttäjältä millä selaimella haluaa avata sivun
        /*
        Intent intent2 = new Intent(intent.ACTION_VIEW);
        intent2.setData(Uri.parse("https://google.com"));
        startActivity(intent2);
        */
    }

    @Override
    protected void onStart(){
        super.onStart();
        //aktiviteettti on käynnistymässä
    }

    @Override
    protected void onStop(){
        super.onStop();
        //aktiviteetti on pois näkyvistä
    }

    @Override
    protected void onResume(){
        super.onResume();
        //aktiviteetti on tulee näkyviin
    }

    @Override
    protected void onPause(){
        super.onPause();
        //aktiveetti on menossa pois näkyvistä
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //aktiviteetti on tuhottu, siivottu muistista.
        // Esim. kun sovellus menee portrait tilaankin
    }
}
