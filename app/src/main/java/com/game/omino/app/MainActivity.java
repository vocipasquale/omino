package com.game.omino.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.omino.R;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "Start button clicked!");
				startActivity(new Intent(MainActivity.this, OminoGameActivity.class));
			}
		});

		findViewById(R.id.button_menu).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "Menu button clicked!");
				startActivity(new Intent(MainActivity.this, MenuActivity.class));
			}
		});
	}
}


