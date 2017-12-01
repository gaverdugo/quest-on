package com.example.queston;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.queston.library.Httppostaux;
import com.example.queston.models.QuestDetail;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestDetailsActivity extends Activity implements OnMapReadyCallback{

	Button btnBack;
	Httppostaux httppostaux;
    String IP_Server = "165.227.92.254";//IP DE NUESTRO PC
    String URL_connect = "http://" + IP_Server + "/getQuest.php";
    QuestDetail questDetail;
    MapFragment mapFragment;
    TextView txtNomQuest;
    TextView txtDesQuest;
    TextView txtReward;
    TextView txtXP;

    Double latitude;
    Double longitude;

    int id;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_quests);
        
		Intent i = getIntent();
		txtNomQuest = (TextView) findViewById(R.id.tituloQuest);
		txtDesQuest = (TextView) findViewById(R.id.descripQuest);
		txtReward = (TextView) findViewById(R.id.rewardQuest);
		txtXP = (TextView) findViewById(R.id.xpQuest);

		latitude = 0d;
		longitude = 0d;

		btnBack = (Button) findViewById(R.id.btnBack);
		httppostaux = new Httppostaux();
		questDetail = new QuestDetail();
		
        id = getIntent().getIntExtra("idQuest", 0);
        Log.w("QuestDetailsActivity", "id: " + id);

		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.questMap);

        new AsyncDetails().execute(Integer.toString(id));

	}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(questDetail.getTitle()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
    }

    class AsyncDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            int id = Integer.parseInt(strings[0]);
            questDetail = getQuest(id);
            return "ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtNomQuest.setText(questDetail.getTitle());
            txtDesQuest.setText(questDetail.getDescription());
            txtReward.setText(questDetail.getReward());
            txtXP.setText(Integer.toString(questDetail.getXp()));

            longitude = questDetail.getLongitude();
            latitude = questDetail.getLatitude();

            mapFragment.getMapAsync(QuestDetailsActivity.this);

        }

        private QuestDetail getQuest(int id) {
            ArrayList<NameValuePair> postParams = new ArrayList<>();
            postParams.add(new BasicNameValuePair("idQuest", Integer.toString(id)));

            JSONObject serverResponse = httppostaux.getServerObject(postParams, URL_connect);

            QuestDetail questDetail = new QuestDetail();

            try {
                questDetail.setId(serverResponse.getInt("id"));
                questDetail.setTitle(serverResponse.getString("title"));
                questDetail.setDescription(serverResponse.getString("description"));
                questDetail.setReward(serverResponse.getString("reward"));
                questDetail.setXp(serverResponse.getInt("xp"));
                questDetail.setLongitude(serverResponse.getDouble("longitude"));
                questDetail.setLatitude(serverResponse.getDouble("latitude"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return questDetail;
        }
    }

}
