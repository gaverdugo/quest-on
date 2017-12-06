package com.example.queston;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.queston.adapters.QuestsAdapter;
import com.example.queston.library.Httppostaux;
import com.example.queston.models.QuestListItem;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


@SuppressWarnings("unused")
public class MainActivity extends Activity {
    private ListView lv;
    double latitude;
    double longitude;
    double radio = 20;
    ArrayList<String> tasks = new ArrayList<String>();
    ArrayList<String> desc_tasks = new ArrayList<String>();
    ArrayList<QuestListItem> tempQuests = new ArrayList<>();
    ArrayList<QuestListItem> lvQuests = new ArrayList<>();
    QuestsAdapter questsAdapter;
    Httppostaux post;
    String IP_Server = "165.227.92.254";
    String URL_connect = "http://" + IP_Server + "/actualizaQuest.php";
    private ProgressDialog pDialog;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(saveInstanceState);
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Actualizando....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        questsAdapter = new QuestsAdapter(this, lvQuests);

        post = new Httppostaux();

        Intent i = getIntent();
        TextView t = (TextView) findViewById(R.id.txtLat);
        String usuario = i.getStringExtra("user");
        t.setText("Hola  " + usuario + "!");

        generateListView();

        //empieza listview
    }
    //FIN DE onCreate


    @Override
    protected void onPause() {
        super.onPause();
        pDialog.dismiss();
    }

    //Esto es del menu.. luego lo movemos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //FIN MENU


    //Metodo para el boton de actualizar
    //Hace un async para actualizar desde la abse de datos trayendo las quests cercanas
    public void updateLocation(View v) {
        GPSTracker gps = new GPSTracker(MainActivity.this);

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }


        new AsyncActualizar().execute(Double.toString(latitude), Double.toString(longitude));
        lvQuests.clear();
        lvQuests = tempQuests;
        questsAdapter.notifyDataSetChanged();


        Toast.makeText(getApplicationContext(), "Lat: " + latitude + "\nLon:" + longitude, Toast.LENGTH_SHORT).show();
        gps.stopSelf();
        generateListView();
    }


    //Metodo para logout
    public void logOut(View v) {
        final TextView t = (TextView) findViewById(R.id.txtLat);


        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(i);

    }


    public void generateListView() {
        lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(new QuestsAdapter(this, lvQuests));
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, QuestDetailsActivity.class);
                intent.putExtra("idQuest", lvQuests.get(i).getId());
                startActivity(intent);
            }
        });

        //fin de listview

        /*
        final TextView nombre = (TextView) findViewById(R.id.textView3);
        final TextView desc = (TextView) findViewById(R.id.textView4);

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                nombre.setText(tasks.get((int) lv.getAdapter().getItemId(position)));
                desc.setText(desc_tasks.get((int) lv.getAdapter().getItemId(position)));

                Intent i = new Intent(getApplicationContext(), QuestDetailsActivity.class);


                String nomQuest = "Hi";
                i.putExtra("nomQuest", nombre.getText());
                String desQuest = "World";
                i.putExtra("desQuest", desc.getText());


                startActivity(i);


            }
        });
        */
    }


    class AsyncActualizar extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            //para el progress dialog
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Double latitud, longitud;


            latitud = Double.parseDouble(params[0]);
            longitud = Double.parseDouble(params[1]);

            if (getNearbyQuests(latitud, longitud).size() > 0) {
                tempQuests = getNearbyQuests(latitud, longitud);
                return "ok"; //se obtuvieron datos
            } else {
                return "err"; // oh no
            }

        }


        protected void onPostExecute(String result) {

            pDialog.dismiss();//ocultamos progess dialog.
            Log.e("onPostExecute=", "" + result);
        }

        public ArrayList<QuestListItem> getNearbyQuests(double latpost, double lonpost) {

            ArrayList<NameValuePair> postparameters2send = new ArrayList<>();

            postparameters2send.add(new BasicNameValuePair("latitude", Double.toString(latpost)));
            postparameters2send.add(new BasicNameValuePair("longitude", Double.toString(lonpost)));

            //realizamos una peticion y como respuesta obtenes un array JSON
            JSONArray serverResponse = post.getServerArray(postparameters2send, URL_connect);

            ArrayList<QuestListItem> quests = new ArrayList<>();

            if (serverResponse != null && serverResponse.length() > 0) {

                for(int i = 0; i < serverResponse.length(); i++) {
                    QuestListItem quest = new QuestListItem();
                    try {
                        JSONObject object = serverResponse.getJSONObject(i);
                        quest.setId(object.getInt("id"));
                        quest.setTitle(object.getString("title"));
                        quest.setDescription(object.getString("description"));
                        quests.add(quest);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

            return quests;

        }

    }

}


