package com.drollgames.crjump;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.drollgames.crjump.util.Constants;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler, GameHelper.GameHelperListener, ActionResolver {
	private static final String TAG = "AndroidLauncher";
	private InterstitialAd interstitialAd;

	private GameHelper gameHelper;

	private CJMain cjMain;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        /* conserve battery */
		cfg.useAccelerometer = false;
		cfg.useCompass = false;
        /* keep the screen on */
		cfg.useWakelock = true;




		cjMain = new CJMain(AndroidLauncher.this, AndroidLauncher.this);
		initialize(cjMain, cfg);

		if(Constants.ENABLE_ADS) {

			cjMain = new CJMain(AndroidLauncher.this, AndroidLauncher.this);
			initialize(cjMain, cfg);

			interstitialAd = new InterstitialAd(this);
			interstitialAd.setAdUnitId(getString(R.string.ad_unit_id_interstitial_1));
			interstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdClosed() {
					Gdx.app.log(TAG, "ADMOB: onAdClosed");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
					requestNewAdmobInterstitial();
//                        }
//                    });
				}
			});
			requestNewAdmobInterstitial();

		} else {
			cjMain = new CJMain(AndroidLauncher.this, AndroidLauncher.this);
			initialize(cjMain, cfg);
		}

		if (gameHelper == null) {
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
			gameHelper.enableDebugLog(true);
		}
		gameHelper.setup(this);
		gameHelper.setMaxAutoSignInAttempts(3);

	}

	private void requestNewAdmobInterstitial() {
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
				addTestDevice(getString(R.string.test_device_1)).
				addTestDevice(getString(R.string.test_device_2)).
				addTestDevice(getString(R.string.test_device_3)).
				addTestDevice(getString(R.string.test_device_4)).
				addTestDevice(getString(R.string.test_device_5)).
				addTestDevice(getString(R.string.test_device_6)).
				addTestDevice(getString(R.string.test_device_7)).
				addTestDevice(getString(R.string.test_device_8)).
				addTestDevice(getString(R.string.test_device_9)).
				addTestDevice(getString(R.string.test_device_10)).
				addTestDevice(getString(R.string.test_device_11)).
				addTestDevice(getString(R.string.test_device_12)).
				addKeyword("game").addKeyword("jump").addKeyword("crate").addKeyword("space").addKeyword("cosmos").addKeyword("meteor")
				.addKeyword("rocket").addKeyword("platform")
				.build();

		interstitialAd.loadAd(adRequest);
	}

	private void displayAdMobInterstitial() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				} else {
					Gdx.app.error(TAG, "interstitialAd is not loaded");
					requestNewAdmobInterstitial();
				}
			}
		});
	}

	@Override
	public void showIntersitial() {
		Gdx.app.log(TAG, "++ showIntersitial ++");
		if (Constants.ENABLE_ADS) {
			displayAdMobInterstitial();
		} else {
			Log.v(TAG, "ads are not enabled");
		}
	}

	@Override
	public void loadIntersitial() {
	}

	@Override
	public void showMoreApps() {
		String url = getString(R.string.drollgames_website);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	@Override
	public void loadShareIntent() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		String strShareText = getString(R.string.speakit_app_url);
		share.putExtra(Intent.EXTRA_TEXT, strShareText);
		startActivity(share);
	}


	/*------------- Google Game Services stuff ---------------------*/
	@Override
	public void onStart(){
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}

	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable(){
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
			Gdx.app.error(TAG, ex.toString());
		}
	}

	@Override
	public void submitScoreLevel24(int score) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_24_levels), score);
	}

	@Override
	public void submitScoreLevel40(int score) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_40_levels), score);
	}

	@Override
	public void getLeaderboard24() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_24_levels)), 100);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void getLeaderboard40() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_40_levels)), 100);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void signOut() {
		gameHelper.signOut();
	}

	@Override
	public void onSignInFailed() {
		Gdx.app.log(TAG, "onSignInFailed");
	}

	@Override
	public void onSignInSucceeded() {
		Gdx.app.log(TAG, "onSignInSucceeded");
	}

}
