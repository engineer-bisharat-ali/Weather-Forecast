package com.example.weatherforcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "0c4631e825004a008e475020233108";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/forecast.json";
    private static final int NUM_DAYS = 1;


    private TextView temperature_txt;
    private TextView condition_txt;
    private TextView location;
    private ImageView main_cloud_image;
    private RequestQueue requestQueue;
    private RecyclerView weatherRv;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private TextView Humidity_value, Humidity_description;
    private TextView sunrise_time, sunset_time;
    private TextView TV_uv_index_value, TV_uv_index_describe;
    private TextInputEditText cityedt;
    private ImageView searchIV;
    private final String default_city = "Karachi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature_txt = findViewById(R.id.temperature_txt);
        condition_txt = findViewById(R.id.preciption);
        main_cloud_image = findViewById(R.id.main_cloud_image);
        location = findViewById(R.id.loction);
        Humidity_value = findViewById(R.id.Humidity_value);
        Humidity_description = findViewById(R.id.Humidity_description);
        sunrise_time = findViewById(R.id.idTV_sunrise_time);
        sunset_time = findViewById(R.id.idTV_sunset_time);
        TV_uv_index_value = findViewById(R.id.TV_uv_index_value);
        TV_uv_index_describe = findViewById(R.id.TV_uv_index_describe);
        cityedt = findViewById(R.id.idEDTcity);
        searchIV = findViewById(R.id.search_city);
        weatherRv = findViewById(R.id.idRvWeather);

        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        weatherRv.setAdapter(weatherRVAdapter);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        getWeather(default_city);    // Weather Details for Default city (Karachi) .......... Method Call


        cityedt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String city = cityedt.getText().toString();
                    location.setText(city);
                    getWeather(city);
                    cityedt.setText("");
                    return true;
                }
                return false;
            }
        });

//
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityedt.getText().toString();
                location.setText(city);
                getWeather(city);
                cityedt.setText("");
            }
        });


    }


    // Method For Fetch Weather Data
    private void getWeather(String city) {
        String url = BASE_URL + "?key=" + API_KEY + "&q=" + city + "&days=" + NUM_DAYS;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject current = response.getJSONObject("current");
                    int temperature = (int) current.getDouble("temp_c");       // get current Temperature
                    temperature_txt.setText(temperature + "°C");

                    JSONObject condition = current.getJSONObject("condition");
                    String text_partly = condition.getString("text");          // get current condition
                    condition_txt.setText(text_partly);

                    String condition_Icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(condition_Icon)).into(main_cloud_image);

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject dayForecast = forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONObject day = dayForecast.getJSONObject("day");


                    int Humidity = current.getInt("humidity");
                    Humidity_value.setText("" + Humidity + "%");

                    if (Humidity > 0 && Humidity <= 40) {
                        Humidity_description.setText("Too Dry");
                    } else if (Humidity > 40 && Humidity <= 60) {
                        Humidity_description.setText("Optimal");
                    } else
                        Humidity_description.setText("Too Humid");


                    JSONObject forecastobj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastobj.getJSONArray("forecastday").getJSONObject(0);


                    String sunrise = forecastO.getJSONObject("astro").getString("sunrise");
                    sunrise_time.setText(sunrise);

                    String sunset = forecastO.getJSONObject("astro").getString("sunset");
                    sunset_time.setText(sunset);


                    double uv_index = current.getDouble("uv");
                    TV_uv_index_value.setText(uv_index + "");

                    if (uv_index > 0 && uv_index < 3) {
                        TV_uv_index_describe.setText("Low");
                    } else if (uv_index > 2 && uv_index < 6) {
                        TV_uv_index_describe.setText("Moderate");
                    } else if (uv_index > 5 && uv_index < 8) {
                        TV_uv_index_describe.setText("High");
                    } else if (uv_index > 7 && uv_index < 11) {
                        TV_uv_index_describe.setText("Very High");
                    } else
                        TV_uv_index_describe.setText("Extreme");

                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourobj = hourArray.getJSONObject(i);
                        String time = hourobj.getString("time");      // get hour time
                        String temper = hourobj.getString("temp_c");   // get hourly forecast temperature
                        String img = hourobj.getJSONObject("condition").getString("icon");    // get condition icon
                        String wind = hourobj.getString("wind_kph");                                // get wind hourly forecast

                        weatherRVModelArrayList.add(new WeatherRVModel(time, temper, img, wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }   // method end
}

