/*  Batteroid 0.1 - Android widget to display battery level
    Copyright (C) <year>  <name of author>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.henriquerocha.android.batteroid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;

public class BatteroidAppWidgetProvider extends AppWidgetProvider {
	private RemoteViews views = 
		new RemoteViews("net.henriquerocha.android.batteroid", R.layout.main);
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		context.getApplicationContext().registerReceiver(this,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		ComponentName cn = 
			new ComponentName(context, BatteroidAppWidgetProvider.class);
		appWidgetManager.updateAppWidget(cn, this.views);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
			Integer level = intent.getIntExtra("level", -1);
			this.views.setTextViewText(R.id.battery_level, level.toString());
			ComponentName cn = 
				new ComponentName(context, BatteroidAppWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(cn, this.views);
		}
		super.onReceive(context, intent);
	}
}
