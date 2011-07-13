/*  
 *  Battery Widget
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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Battery Widget
 * @author Henrique Rocha <hmrocha@gmail.com>
 */
public class BatteryAppWidgetProvider extends AppWidgetProvider {
<<<<<<< HEAD
	private static final String TAG = "BatteryAppWidgetProvider";
	public static final String ACTION_CHANGE_BG = "ACTION_CHANGE_BG";
=======
>>>>>>> 84ce188976447506bd3ae27a49b6b4957e4bbd24
	static int currentLayout = -1;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
<<<<<<< HEAD
		if (BatteryAppWidgetProvider.currentLayout == -1)
			BatteryAppWidgetProvider.currentLayout = R.layout.main;
		context.startService(new Intent(context, BatteryService.class));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_CHANGE_BG)) {
			Log.d(TAG, ACTION_CHANGE_BG);
			
			if (BatteryAppWidgetProvider.currentLayout == R.layout.main)
				BatteryAppWidgetProvider.currentLayout = R.layout.transparent;
			else
				BatteryAppWidgetProvider.currentLayout = R.layout.main;
			
			context.startService(new Intent(context, BatteryService.class));
			context.startService(intent);
		}
		super.onReceive(context, intent);
	}
=======
		context.startService(new Intent(context, BatteryService.class));
	}
>>>>>>> 84ce188976447506bd3ae27a49b6b4957e4bbd24
}
