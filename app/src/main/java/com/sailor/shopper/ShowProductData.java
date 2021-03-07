package com.sailor.shopper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

public class ShowProductData extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product_data);
        mTextView = findViewById(R.id.ptxScannedCode);
        getProductData("5900020019592");
    }

    @Override
    protected void onStart() {
        super.onStart();
        String scannedCode = getIntent().getStringExtra("result");
        mTextView.setText(scannedCode);
    }

    private void getProductData(String readBarcode){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = String.format("https://world.openfoodfacts.org/api/v0/product/%S.json",readBarcode );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonObject jsonObject = (JsonObject) JsonParser.parseString(response);
               String productName = jsonObject.get("product_name_pl").toString();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

}