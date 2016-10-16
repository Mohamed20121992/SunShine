package com.example.mohamed.sunshine.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product_item> arrayList;
    ListView listView;
    View view;
    TextView title, min_temp, max_temp, locationContury, locationCity, textView;
    ProductsAdapter productsAdapter;
    ImageView imageView;
    String location, units, days_com, result, list[][];
    ShareActionProvider actionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        intionlize_componnent();
        lisener_listview();
        othercomponnent();
        test();


    }

    //intionlizing componnent of Mainactivity
    void intionlize_componnent() {
        arrayList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_products);
        title = (TextView) findViewById(R.id.Title);
        min_temp = (TextView) findViewById(R.id.c_temp);
        max_temp = (TextView) findViewById(R.id.f_temp);
        imageView = (ImageView) findViewById(R.id.imageView);
        locationCity = (TextView) findViewById(R.id.location_city);
        locationContury = (TextView) findViewById(R.id.location_contury);
        view = (View) getLayoutInflater().inflate(R.layout.header, null);
        listView.addHeaderView(view);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                listView.removeHeaderView(view);
                return false;
            }
        });
    }

    //make item lisener and passing values to Detail class
    void lisener_listview() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String detailCountry = locationContury.getText().toString();
                String detailCity = locationCity.getText().toString();
                String detailTemp_min = list[2][position];
                String detailTemp_max = list[3][position];
                String detailTitle = list[0][position];
                String detailUrl = list[4][position];
                String desc = list[1][position];
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("country", detailCountry);
                intent.putExtra("city", detailCity);
                intent.putExtra("min", detailTemp_min);
                intent.putExtra("max", detailTemp_max);
                intent.putExtra("title", detailTitle);
                intent.putExtra("desc", desc);
                intent.putExtra("url", detailUrl);
                intent.putExtra("result", result);
                intent.putExtra("postion", position);
                startActivity(intent);
            }
        });
    }

    //intionlizing componnent for check network
    void othercomponnent() {
        textView = (TextView) findViewById(R.id.textView);
        if (isNetworkAvailable()) {
            listView.removeHeaderView(view);
            listView.addHeaderView(view);
            textView.setText("connected");
            textView.setBackgroundResource(android.R.color.holo_green_light);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            location = sharedPreferences.getString(getString(R.string.locationkey), getString(R.string.locationdefault));
            units = sharedPreferences.getString(getString(R.string.unitskey), getString(R.string.unitsdefault));
            days_com = sharedPreferences.getString(getString(R.string.dayskey), getString(R.string.daysdefault));

            //calling task for looding data from server and display it
            Lodding_data getdata = new Lodding_data(location, units, getDays(days_com));
            getdata.execute();


        } else {
            listView.removeHeaderView(view);
            listView.addHeaderView(view);
            Toast.makeText(MainActivity.this, "Please connect internet and refresh service", Toast.LENGTH_LONG).show();
            textView.setText("disconnecting");
            textView.setBackgroundResource(android.R.color.holo_red_light);

        }


    }

    //return char sign for temp depend on sharedrefrence values
    String getUnits(String units) {
        String t = "";
        if (units.equals("metric")) {
            t = "C";
        } else if (units.equals("kelvin")) {
            t = "K";
        } else if (units.equals("fahriye")) {
            t = "F";
        }
        return t;
    }

    //take day number of weeks and rturn number of days depend on sharedrefrance
    String getDays(String days) {
        String numday = "";
        if (days.equals("one week")) {
            numday = "7";
        } else if (days.equals("two week")) {
            numday = "14";
        }
        return numday;
    }

    //check if internet is connectd or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //dispaly data into list view
    void fill_list_view(String day, String description, String tem_min, String temp_max, String url) {
        listView.removeAllViewsInLayout();

        Product_item product_item = new Product_item();
        product_item.setDay(day);
        product_item.setTemp_min(tem_min);
        product_item.setTemp_max(temp_max);
        product_item.setDescription(description);

        product_item.setUrl(url);
        arrayList.add(product_item);

        productsAdapter = new ProductsAdapter(MainActivity.this, arrayList);
        listView.setAdapter(productsAdapter);
        productsAdapter.notifyDataSetChanged();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent intent = new Intent(MainActivity.this, Setting.class);
            startActivity(intent);
        } else if (id == R.id.refresh) {


            if (isNetworkAvailable()) {
                listView.removeHeaderView(view);
                listView.addHeaderView(view);
                textView.setText("connected");
                textView.setBackgroundResource(android.R.color.holo_green_light);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                location = sharedPreferences.getString(getString(R.string.locationkey), getString(R.string.locationdefault));
                units = sharedPreferences.getString(getString(R.string.unitskey), getString(R.string.unitsdefault));
                days_com = sharedPreferences.getString(getString(R.string.dayskey), getString(R.string.daysdefault));


                Lodding_data getdata = new Lodding_data(location, units, getDays(days_com));
                getdata.execute();


            } else {
                listView.removeHeaderView(view);
                listView.addHeaderView(view);
                Toast.makeText(MainActivity.this, "Please connect internet and refresh service", Toast.LENGTH_LONG).show();
                textView.setText("disconnecting");
                textView.setBackgroundResource(android.R.color.holo_red_light);

            }


        } else if (id == R.id.shar) {


            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }


        return super.onOptionsItemSelected(item);
    }

    void test() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Toast.makeText(MainActivity.this, "dfd", Toast.LENGTH_LONG);

                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            Toast.makeText(MainActivity.this, sharedText, Toast.LENGTH_LONG);
            Log.d("from other", sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }

    }

    //class to lodding data and parsing it
    class Lodding_data extends AsyncTask {

        //varables
        ProgressDialog progressDialog;
        String city, days, unit, loc_country, loc_city;
        Bitmap bitmap;
        int lenth;
        HttpURLConnection httpURLConnection;

        //constructor to intionlize varablestake 3 arguments
        public Lodding_data(String city, String unit, String days) {
            this.city = city;
            this.days = days;
            this.unit = unit;
        }

        //Function to connection with weather server to get data (run in function parsing_data_comming())
        String connect_to_server() {

            try {
                Varables vars = new Varables();
                String link_requset = Varables.url + Varables.city + city + Varables.plus + Varables.mode + Varables.plus + Varables.units + unit + Varables.plus + Varables.days + days + Varables.plus + Varables.APPID;
                URL url = new URL(link_requset);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod(vars.method_connect);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line_in_bufferreder;
                while ((line_in_bufferreder = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line_in_bufferreder + "/n");
                }
                result = stringBuilder.toString();


                //Toast.makeText(MainActivity.this,result,Toast.LENGTH_LONG).show();
            } catch (HttpHostConnectException ex) {

                Log.d("error connect", ex.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return result;
        }

        //parsing data that comming from server (run in background )
        void parsing_data_comming() {
            Varables vars = new Varables();
            String data = connect_to_server();
            String day, url, urlplus, description, temp_min, temp_max, listdata[][];
            try {
                Time dtime = new Time();
                dtime.setToNow();
                int numday = dtime.getJulianDay(System.currentTimeMillis(), dtime.gmtoff);
                dtime = new Time();
                Long datetime;
                JSONObject dataobject = new JSONObject(data);
                JSONArray listDaysData = dataobject.getJSONArray(vars.mylist);
                lenth = listDaysData.length();
                listdata = new String[5][lenth];
                for (int i = 0; i < listDaysData.length(); i++) {
                    datetime = dtime.setJulianDay(numday + i);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd / MM");
                    day = simpleDateFormat.format(datetime);
                    JSONObject dayobject = listDaysData.getJSONObject(i);
                    JSONObject dayWetherObject = dayobject.getJSONArray(vars.myWeather).getJSONObject(0);
                    description = dayWetherObject.getString("main");
                    url = dayWetherObject.getString("icon");
                    urlplus = "http://openweathermap.org/img/w/" + url + ".png";
                    JSONObject dayTemp = dayobject.getJSONObject(vars.myTemp);
                    double dayTempMin = dayTemp.getDouble("min");
                    double dayTempMax = dayTemp.getDouble("max");
                    Long min = Math.round(dayTempMin);
                    Long max = Math.round(dayTempMax);
                    temp_min = min.toString();
                    temp_max = max.toString();
                    listdata[0][i] = day;
                    listdata[1][i] = description;
                    listdata[2][i] = temp_min;
                    listdata[3][i] = temp_max;
                    listdata[4][i] = urlplus;

                }
                MainActivity.this.list = listdata;
                JSONObject jsonObject = dataobject.getJSONObject("city");

                loc_city = jsonObject.getString("name");
                loc_country = jsonObject.getString("country");
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(list[4][0]).getContent());


            } catch (JSONException ex) {
                Log.d("error Json", ex.toString());
            } catch (Exception ex) {
                Log.d("error from run thread", ex.toString());
            }

        }

        //display data in their places
        void display_data() {
            try {

                httpURLConnection.disconnect();


                arrayList.clear();
                for (int i = 0; i < lenth; i++) {
                    fill_list_view(list[0][i], list[1][i], list[2][i], list[3][i], list[4][i]);
                }

                locationCity.setText("City : " + loc_city);
                locationContury.setText("Country : " + loc_country);
                Product_item product_item = arrayList.get(0);

                title.setText(product_item.getDay());

                min_temp.setText("MIN " + product_item.getTemp_min() + getUnits(units));
                max_temp.setText("MAX " + product_item.getTemp_max() + getUnits(units));
                try {
                    imageView.setImageBitmap(bitmap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                progressDialog.dismiss();

            } catch (Exception ex) {
                Log.d("error loop", ex.toString());
                progressDialog.dismiss();

            }
        }

        @Override
        protected Object doInBackground(Object[] params) {

            parsing_data_comming();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            display_data();
            super.onPostExecute(o);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Waiting", "Looding.......", true);
            super.onPreExecute();
        }
    }
}


