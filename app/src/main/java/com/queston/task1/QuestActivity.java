package com.queston.task1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class QuestActivity extends Activity{
	
	
	
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_quests);
        
		Intent i = getIntent();
		TextView txtNomQuest = (TextView) findViewById(R.id.tituloQuest);
		TextView txtDesQuest = (TextView) findViewById(R.id.descripQuest);
		TextView txtLat = (TextView) findViewById(R.id.txtLat);
		
		
		String titulo = i.getStringExtra("nomQuest");
		String descripcion = i.getStringExtra("desQuest");
		
		txtNomQuest.setText(titulo);
		txtDesQuest.setText(descripcion);

	}
	
	

	public void clickBack(View v){
		finish();
	}
	
	
	
	
	
}
