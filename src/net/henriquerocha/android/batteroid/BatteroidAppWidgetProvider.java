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
