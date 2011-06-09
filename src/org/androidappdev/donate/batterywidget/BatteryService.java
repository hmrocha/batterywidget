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
package org.androidappdev.donate.batterywidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Service to monitor battery level changes.
 * 
 * @author Henrique Rocha
 */
public class BatteryService extends Service {
	private static final String TAG = "BatteryService";
	private BroadcastReceiver batteryReceiver;
	private Integer batteryLevel = 0;

	@Override
	public void onStart(Intent intent, int startId) {
		if (this.batteryReceiver == null) {
			this.batteryReceiver = batteryLevelReceiver();
			registerReceiver(this.batteryReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
			Log.d(TAG, "Registered receiver");
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
	 * @return a BroadcastReceiver to handle ACTION_BATTERY_CHANGED
	 */
	private BroadcastReceiver batteryLevelReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					int level = intent.getIntExtra("level", 0);
					Log.d(TAG, "Level: " + new Integer(level).toString());
					// Only update widget if level changed, other changes
					// like temperature don't matter.
					if (batteryLevel != level) {
						batteryLevel = level;
						Log.d(TAG, "Starting service");
						Intent levelChanged = new Intent(context,
								BatteryService.class);
						context.startService(levelChanged);
					}
				}
			}
		};
	}
	
	/**
	 * Build a widget update to show the current battery level.
	 */
	private RemoteViews buildUpdate(Context context) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		views.setTextViewText(R.id.battery_level, this.batteryLevel.toString());
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
