package com.example.mohamed.sunshine.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class Detail extends AppCompatActivity {

    //varables
    TextView Title,City,Country,Min,Max,Desc,Pressure,Speed,Temp_day,Temp_night,Temp_even,Temp_morn;
    ImageView imageView;
    String result,min,max,title,desc,country,city,url;
    int com_postion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        intiolize_componnent();
        get_passing_values();

        //calling task in background
        looding_data looding_data=new looding_data(url);
        looding_data.execute();

    }
    //get value that passing from MainActivity class
    void get_passing_values(){
         Intent intent=getIntent();
         city=intent.getStringExtra("city");
         city=city.substring(7);
         country=intent.getStringExtra("country");
         country=country.substring(9);
         min=intent.getStringExtra("min");
         max=intent.getStringExtra("max");
         title=intent.getStringExtra("title");
         desc=intent.getStringExtra("desc");
         url=intent.getStringExtra("url");
         result=intent.getStringExtra("result");
         com_postion=intent.getIntExtra("postion",0);
    }

    //intionlize componnent of Detail activity
    void intiolize_componnent(){
        Title=(TextView)findViewById(R.id.title_detail);
        City=(TextView)findViewById(R.id.detail_city);
        Country=(TextView)findViewById(R.id.detail_countury);
        Min=(TextView)findViewById(R.id.detail_temp_min);
        imageView=(ImageView)findViewById(R.id.imageView2);
        Max=(TextView)findViewById(R.id.detail_tem_max);
        Desc=(TextView)findViewById(R.id.detail_description);
        Pressure=(TextView)findViewById(R.id.detail_pressure);
        Speed=(TextView)findViewById(R.id.detail_speed);
        Temp_day=(TextView)findViewById(R.id.detail_day_temp);
        Temp_night=(TextView)findViewById(R.id.detail_night);
        Temp_even=(TextView)findViewById(R.id.detail_even);
        Temp_morn=(TextView)findViewById(R.id.detail_day_morn);

    }
    class looding_data extends AsyncTask{
        ProgressDialog progressDialog;Bitmap bitmap;String url,pressure,speed,day_temp,night_temp,even_tem,morn_temp;Varables vars=new Varables();
        //constractor tack one arrgument
        looding_data(String url){

            this.url=url;
        }

        //display data into places
        void set_data(){

            try {
                imageView.setImageBitmap(bitmap);
                Pressure.setText(pressure);
                Country.setText(country);
                City.setText(city);
                Min.setText(min+ " C");
                Max.setText(max+ " C");
                Title.setText(title);
                Desc.setText(desc);
                Speed.setText(speed);
                Temp_morn.setText(morn_temp);
                Temp_day.setText(day_temp);
                Temp_even.setText(even_tem);
                Temp_night.setText(night_temp);

                progressDialog.dismiss();

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        //parsing double value to long and return string
        String parsing_doubl(double var){
            Long var_long=Math.round(var);
            return var_long.toString();
        }
        //parsing data to set it(it run in background
        void parsing_data(){
            try {
                JSONObject dataobject=new JSONObject(result);
                JSONArray listDaysData=dataobject.getJSONArray(vars.mylist);
                JSONObject dayobject=listDaysData.getJSONObject(com_postion);
                JSONObject tempObject=dayobject.getJSONObject("temp");
                double pressure_double=dayobject.getDouble("pressure");
                double speed_double=dayobject.getDouble("speed");
                double morning_temp_double=tempObject.getDouble("morn");
                double day_temp_double=tempObject.getDouble("day");
                double even_temp_double=tempObject.getDouble("eve");
                double night_temp_double=tempObject.getDouble("night");
                pressure=parsing_doubl(pressure_double);
                speed=parsing_doubl(speed_double);
                morn_temp=parsing_doubl(morning_temp_double);
                day_temp=parsing_doubl(day_temp_double);
                even_tem=parsing_doubl(even_temp_double);
                night_temp=parsing_doubl(night_temp_double);
                bitmap= BitmapFactory.decodeStream((InputStream)new URL(url).getContent());

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {


            parsing_data();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            set_data();
            super.onPostExecute(o);

        }
        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(Detail.this,"Waiting","Looding.......",true);
            super.onPreExecute();
        }


    }
}
