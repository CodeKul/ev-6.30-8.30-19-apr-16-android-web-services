package com.codekul.webservices;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codekul.webservices.dto.Address;
import com.codekul.webservices.dto.Example;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText edtAnyThing;
    private Button btnMagic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final OkHttpClient client = new OkHttpClient();

        edtAnyThing = (EditText) findViewById(R.id.edtAnyThing);

        btnMagic = (Button) findViewById(R.id.btnMagic);
        btnMagic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Example example = new Example();
                example.setEmail("aniruddha@gmail.com");
                example.setId(10);
                example.setName("android");
                example.setPhone("098987");
                example.setUsername("android");
                example.setWebsite("codekul.com");

                Address address = new Address();
                address.setCity("pune");
                address.setStreet("karve nagar");
                address.setSuite("iouy");
                address.setZipcode("146038");

                example.setAddress(address);

                ObjectMapper mapper = new ObjectMapper();
                try {
                    String json = mapper.writeValueAsString(example);
                    Log.i("@codekul",json);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

               new TaskWeb(new WebOkyHttpConnector(),"http://jsonplaceholder.typicode.com/users")
                       .execute(edtAnyThing.getText().toString());
            }
        });
    }

    private interface IConnectible {

        String get(String url) throws Exception;

        String post(String url,String data) throws Exception;
    }

    private class WebOkyHttpConnector implements IConnectible {

        private OkHttpClient client = new OkHttpClient();

        @Override
        public String get(String url) throws Exception {

            String jsonResponse = "";
            try {

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                jsonResponse = response.body().string();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        public String post(String url,String data) throws Exception {

            String jsonResponse = "";
            try {

                RequestBody formBody = new FormBody.Builder()
                        .add("rawText",data)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                jsonResponse = response.body().string();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return jsonResponse;
        }
    }

    private class TaskWeb extends AsyncTask<String,Void,String> {

        private IConnectible connector;
        private String url;
        private ProgressDialog progressDialog;

        public TaskWeb(IConnectible connector,String url){

            this.connector = connector;
            this.url = url;
        }

        private String postMagic(String anyThing){

            String json = "";
            try {
                json = connector.post(url,anyThing);

                JSONObject jsonObj = new JSONObject(json);
                json = jsonObj.getString("gruesomeFact");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        private String getHugeData(){


            try {
                return  connector.get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,"Loading","fetching data from server");
        }

        @Override
        protected String doInBackground(String... params) {

            ObjectMapper mapper = new ObjectMapper();
            String hugeJson = getHugeData();
            try {
                List<Example> exampleList = mapper.readValue(hugeJson, new TypeReference<List<Example>>() {
                });

                for(Example ex : exampleList){

                    Log.i("@codekul",ex.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  hugeJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            ((TextView)findViewById(R.id.textResponse)).setText(s);
        }
    }
}
