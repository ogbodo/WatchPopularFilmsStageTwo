package com.izuking.udacity.android.watchmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	private static final int SPLASH_DURATION = 1000; // 1 seconds

	// SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Handler handler = new Handler();

		// run a thread after 1 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent i = new Intent(Splash.this, MainActivity.class);
				finish();
				startActivity(i);
				overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

			}

		}, SPLASH_DURATION);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

}
