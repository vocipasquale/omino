package com.game.omino.app;

import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.omino.R;
import com.game.omino.engine.GameWorld;
import com.game.omino.render.GameSurfaceView;

import static com.game.omino.utils.Constants.TILE_SIZE;

public class OminoGameActivity extends AppCompatActivity {

	private GameSurfaceView gameView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Usa il layout XML aggiornato
		setContentView(R.layout.activity_ominogame);

		// Trova GameSurfaceView dal layout
		gameView = findViewById(R.id.game_surface);

		// Imposta listener per i pulsanti della barra dei comandi
		findViewById(R.id.button_up).setOnClickListener(v ->
				GameWorld.getInstance().getOmino().y -= TILE_SIZE // Y decresce verso l'alto
		);

		findViewById(R.id.button_down).setOnClickListener(v ->
				GameWorld.getInstance().getOmino().y += TILE_SIZE // Y aumenta verso il basso
		);

		findViewById(R.id.button_left).setOnClickListener(v ->
				GameWorld.getInstance().getOmino().x -= TILE_SIZE
		);

		findViewById(R.id.button_right).setOnClickListener(v ->
				GameWorld.getInstance().getOmino().x += TILE_SIZE
		);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gameView != null) {
			gameView.onResumeLoop();
		}
	}

	@Override
	protected void onPause() {
		if (gameView != null) {
			gameView.onPauseLoop();
		}
		super.onPause();
	}
}
