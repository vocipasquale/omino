package com.game.omino.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.game.omino.R;
import com.game.omino.engine.GameEventListener;
import com.game.omino.engine.GameWorld;
import com.game.omino.levels.LevelManager;
import com.game.omino.render.GameSurfaceView;
//import com.game.omino.scene.PlayScene;
//import com.game.omino.scene.SceneManager;

import static com.game.omino.utils.Constants.REPEAT_DELAY_MS;

public class OminoGameActivity extends AppCompatActivity implements GameEventListener {

	private GameSurfaceView gameView;
	private GridLayout controlBar;

	// Handler a livello di classe (main thread)
	private final Handler handler = new Handler(Looper.getMainLooper());

	@SuppressLint("MissingInflatedId")
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ominogame);

		gameView = findViewById(R.id.game_surface);
		controlBar = findViewById(R.id.control_bar);

		gameView.setVisibility(View.VISIBLE);
		controlBar.setVisibility(View.VISIBLE);

		// scena iniziale
		try {
            //SceneManager.setScene(new PlayScene(LevelManager.getInstance(getBaseContext()).startNextLevel()));
			GameWorld.getInstance().loadLevel(LevelManager.getInstance(getBaseContext()).startNextLevel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

		GameWorld.getInstance().setGameEventListener(this);

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

		//findViewById(R.id.button_center).setOnTouchListener(makeFireListener());
		findViewById(R.id.button_fire_left).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GameWorld.getInstance().getOmino().fire(false); // Chiama fire() per il pulsante sinistro
			}
		});

		findViewById(R.id.button_fire_right).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GameWorld.getInstance().getOmino().fire(true); // Chiama fire() per il pulsante destro
			}
		});

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

						// azione di rilascio
						if (moveAction instanceof Runnable) {
							if (v.getId() == R.id.button_up) {
								GameWorld.getInstance().getOmino().stopSu();
							} else if (v.getId() == R.id.button_down) {
								GameWorld.getInstance().getOmino().stopGiu();
							} else if (v.getId() == R.id.button_right) {
								GameWorld.getInstance().getOmino().stopDestra();
							} else if (v.getId() == R.id.button_left) {
								GameWorld.getInstance().getOmino().stopSinistra();
							}
						}
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

	@Override
	public void onGameOver() {
		LevelManager.getInstance(getBaseContext()).reset();
		GameWorld.getInstance().reset();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Trova il FrameLayout e il pulsante di continuazione
				FrameLayout gameOverlay = findViewById(R.id.game_overlay);
				Button gameOverButton = findViewById(R.id.gameover_button);

				// Mostra l'overlay
				gameOverlay.setVisibility(View.VISIBLE);
				gameOverButton.setVisibility(View.VISIBLE);

				gameView.setVisibility(View.INVISIBLE);
				controlBar.setVisibility(View.INVISIBLE);

				// Imposta il listener per il pulsante di continuazione
				gameOverButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(OminoGameActivity.this, MainActivity.class));
					}
				});
			}
		});
	}

	@Override
	public void onLifeLost() {

	}

	@Override
	public void onLevelPassed() {
		try {
			GameWorld.getInstance().loadLevel(LevelManager.getInstance(getBaseContext()).startNextLevel());
			gameView.initScreen();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onGameFinished() {
		LevelManager.getInstance(getBaseContext()).reset();
		GameWorld.getInstance().reset();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Trova il FrameLayout e il pulsante di continuazione
				FrameLayout gameOverlay = findViewById(R.id.game_overlay);
				Button gameFinishButton = findViewById(R.id.gamefinish_button);

				gameView.setVisibility(View.INVISIBLE);
				controlBar.setVisibility(View.INVISIBLE);

				// Mostra l'overlay
				gameOverlay.setVisibility(View.VISIBLE);
				gameFinishButton.setVisibility(View.VISIBLE);

				// Imposta il listener per il pulsante di continuazione
				gameFinishButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(OminoGameActivity.this, MainActivity.class));
					}
				});
			}
		});
	}
}
