package com.codekul.webservices;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.HttpURLConnection;

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

                new TaskWeb(new WebOkyHttpConnector(),"your url")
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
            return null;
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,"Loading","fetching data from server");
        }

        @Override
        protected String doInBackground(String... params) {

            String json = "";
            try {
                json = connector.post(url,params[0]);

                JSONObject jsonObj = new JSONObject(json);
                json = jsonObj.getString("gruesomeFact");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            ((TextView)findViewById(R.id.textResponse)).setText(s);
        }
    }
}
