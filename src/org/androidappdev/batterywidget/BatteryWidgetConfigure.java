package org.androidappdev.batterywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BatteryWidgetConfigure extends Activity {
	private static final String PREFS_NAME = "org.androidappdev.batterywidget.BatteryAppWidgetProvider";
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private boolean mShowTemperature = false;
	private int mDegrees = -1;

	// Preferences
	private static final String PREF_SHOW_TEMPERATURE = "show_temperature";
	private static final String PREF_DEGREES = "degrees";
	public static final int PREF_CELSIUS = 0;
	public static final int PREF_FAHRENHEIT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);

		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// Bind the action for the save button.
		findViewById(R.id.save_button).setOnClickListener(mOnClickListener);

		final CheckBox checkbox = (CheckBox) findViewById(R.id.cb_show_temp);
		checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks, depending on whether it's now
				// checked
				final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_temp_opts);
				if (((CheckBox) v).isChecked()) {
					mShowTemperature = true;
					radioGroup.setVisibility(View.VISIBLE);
				} else {
					mShowTemperature = false;
					radioGroup.setVisibility(View.GONE);
				}
			}
		});

		final RadioButton radioCelsius = (RadioButton) findViewById(R.id.radio_celsius);
		final RadioButton radioFahrenheit = (RadioButton) findViewById(R.id.radio_fahrenheit);
		radioCelsius.setOnClickListener(mRadioListener);
		radioFahrenheit.setOnClickListener(mRadioListener);

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			final Context context = BatteryWidgetConfigure.this;

			// When the button is clicked, save our prefs and return that they
			// clicked OK.
			saveTemperaturePref(context, mAppWidgetId, mShowTemperature,
					mDegrees);

			// Push widget update to surface with newly set prefs
			context.startService(new Intent(context, BatteryService.class));

			// Make sure we pass back the original appWidgetId
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}
	};

	private OnClickListener mRadioListener = new OnClickListener() {
		public void onClick(View v) {
			RadioButton rb = (RadioButton) v;
			mDegrees = rb.getId() == R.id.radio_celsius ? PREF_CELSIUS
					: PREF_FAHRENHEIT;
		}
	};

	private void saveTemperaturePref(Context context, int appWidgetId,
			boolean show, int degrees) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putBoolean(PREF_SHOW_TEMPERATURE, show);
		if (show) {
			prefs.putInt(PREF_DEGREES, degrees);
		}
		prefs.commit();
	}

	public static boolean getTemperaturePref(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getBoolean(PREF_SHOW_TEMPERATURE, false);
	}

	public static int getDegreesPref(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getInt(PREF_DEGREES, PREF_CELSIUS);
	}

}
