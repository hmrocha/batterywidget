/*  
 *  Battery Widget - Simple Android Battery Widget
 *  Copyright (C) 2011 Henrique Rocha <hmrocha@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.androidappdev.batterywidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Service to monitor battery level and temperature changes.
 * 
 * @author Henrique Rocha
 */
public class BatteryService extends Service {
	private static final String TAG = "BatteryService";
	private BroadcastReceiver batteryReceiver;
	private Integer currentLevel = 0;
	private Integer currentTemperature = 0;
	private int previousStatus = -1;

	@Override
	public void onStart(Intent intent, int startId) {
		if (this.batteryReceiver == null) {
			this.batteryReceiver = batteryLevelReceiver();
			registerReceiver(this.batteryReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
		}

		// Build the widget update.
		RemoteViews updateViews = buildUpdate(this);

		// Push update for this widget to the home screen
		ComponentName batteryWidget = new ComponentName(this,
				BatteryAppWidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(batteryWidget, updateViews);
	}

	/**
	 * Battery level receiver
	 * 
	 * @return a BroadcastReceiver to handle ACTION_BATTERY_CHANGED
	 */
	private BroadcastReceiver batteryLevelReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean needsUpdate = false;
				int layout = -1;
				String action = intent.getAction();

				// Change dots to blue if charging.
				int status = intent.getIntExtra("status", 0);
				Log.d(TAG, "status: " + status);

				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					int level = intent.getIntExtra("level", 0);
					int temperature = intent.getIntExtra("temperature", 0);
					Log.d(TAG, "Level: " + level);
					// Only update widget if level changed, other changes
					// like temperature don't matter.
					if (currentLevel != level) {
						currentLevel = level;						
						needsUpdate = true;
					}
					boolean showTemperature = BatteryWidgetConfigure.getTemperaturePref(context);
					if (showTemperature && currentTemperature != temperature) {
						currentTemperature = temperature;
						needsUpdate = true;
					}

					if (level <= 100)
						layout = R.layout.main;
					if (level <= 30)
						layout = R.layout.below30;
					if (level <= 15)
						layout = R.layout.below15;

				}

				if (status == BatteryManager.BATTERY_STATUS_CHARGING
						&& previousStatus != BatteryManager.BATTERY_STATUS_CHARGING) {
					BatteryAppWidgetProvider.currentLayout = R.layout.charging;
					needsUpdate = true;
				} else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
						&& previousStatus != BatteryManager.BATTERY_STATUS_DISCHARGING) {
					BatteryAppWidgetProvider.currentLayout = layout;
					needsUpdate = true;
				}

				if (needsUpdate) {
					Intent statusChanged = new Intent(context,
							BatteryService.class);
					context.startService(statusChanged);
				}
			}
		};
	}

	/**
	 * Build a widget update to show the current battery level.
	 */
	private RemoteViews buildUpdate(Context context) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				BatteryAppWidgetProvider.currentLayout);

		// Action for tap on widget
		// Intent bcast = new Intent(context, BatteryAppWidgetProvider.class);
		// bcast.setAction(BatteryAppWidgetProvider.ACTION_CHANGE_BG);
		// PendingIntent pending = PendingIntent.getBroadcast(context, 0, bcast,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// views.setOnClickPendingIntent(R.id.widget, pending);

		views.setTextViewText(R.id.battery_level, this.currentLevel.toString());
		Integer temperature = this.currentTemperature / 10;
		
		boolean showTemperature = BatteryWidgetConfigure.getTemperaturePref(context);
		if (showTemperature) {
			int degrees = BatteryWidgetConfigure.getDegreesPref(context);
			Log.d(TAG, "degrees:" + degrees);
			if (degrees == BatteryWidgetConfigure.PREF_FAHRENHEIT) {
				 temperature = (int) (1.8 * (temperature + 32) + 0.5);
			}
			views.setTextViewText(R.id.temperature, temperature + "º");				
			views.setViewVisibility(R.id.temperature, View.VISIBLE);
		}
		else {
			views.setViewVisibility(R.id.temperature, View.GONE);			
		}
		ComponentName cn = new ComponentName(context,
				BatteryAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views);
		return views;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// We don't need to bind to this service
		return null;
	}

}
