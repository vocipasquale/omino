package com.game.omino.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.omino.R;


public class MenuActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		findViewById(R.id.button_chiudi_menu).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "Start button clicked!");
				startActivity(new Intent(MenuActivity.this, MainActivity.class));
			}
		});
	}
}


