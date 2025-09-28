package com.game.omino.app;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.omino.R;
import com.game.omino.engine.GameWorld;
import com.game.omino.render.GameSurfaceView;

import static com.game.omino.utils.Constants.REPEAT_DELAY_MS;
import static com.game.omino.utils.Constants.STEP;

public class OminoGameActivity extends AppCompatActivity {

	private GameSurfaceView gameView;

	// Handler a livello di classe (main thread)
	private final Handler handler = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ominogame);

		gameView = findViewById(R.id.game_surface);

		// usa makeMoveListener passando un Runnable (qui senza lambda per compatibilità)
		findViewById(R.id.button_up).setOnTouchListener(
				makeMoveListener(new Runnable() {
					@Override
					public void run() {
						GameWorld.getInstance().getOmino().su();
					}
				})
		);

		findViewById(R.id.button_down).setOnTouchListener(
				makeMoveListener(new Runnable() {
					@Override
					public void run() {
						GameWorld.getInstance().getOmino().giu();
					}
				})
		);

		findViewById(R.id.button_left).setOnTouchListener(
				makeMoveListener(new Runnable() {
					@Override
					public void run() {
						GameWorld.getInstance().getOmino().sinistra();
					}
				})
		);

		findViewById(R.id.button_right).setOnTouchListener(
				makeMoveListener(new Runnable() {
					@Override
					public void run() {
						GameWorld.getInstance().getOmino().destra();
					}
				})
		);
	}

	/**
	 * Crea e ritorna un OnTouchListener che esegue moveAction immediatamente su ACTION_DOWN
	 * e lo ripete ogni REPEAT_DELAY_MS finché non arriva ACTION_UP / ACTION_CANCEL.
	 */
	private View.OnTouchListener makeMoveListener(final Runnable moveAction) {
		return new View.OnTouchListener() {
			// Runnable di ripetizione legato all'istanza di questo listener
			private Runnable repeatRunnable;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// esegui subito lo spostamento
						moveAction.run();

						// crea il runnable che si ripeterà ad intervalli
						repeatRunnable = new Runnable() {
							@Override
							public void run() {
								moveAction.run();
								handler.postDelayed(this, REPEAT_DELAY_MS);
							}
						};

						// programma la prima ripetizione
						handler.postDelayed(repeatRunnable, REPEAT_DELAY_MS);

						// feedback visivo
						v.setPressed(true);
						return true;

					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						// ferma solo il runnable associato a questo listener
						if (repeatRunnable != null) {
							handler.removeCallbacks(repeatRunnable);
							repeatRunnable = null;
						}
						v.setPressed(false);
						return true;
				}
				return false;
			}
		};
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
