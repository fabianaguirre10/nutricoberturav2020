package org.odk.collect.android.Tracking;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.BaseDatosEngine.BaseDatosEngine;
import org.odk.collect.android.logic.PropertyManager;

public class TrackingBussiness {

    public   void  SetLocationMerch(){
        JavaRestClient tarea = new JavaRestClient();
        String deviceId = new PropertyManager(Collect.getInstance().getApplicationContext())
                .getSingularProperty(PropertyManager.withUri(PropertyManager.PROPMGR_DEVICE_ID));
        String imei = deviceId;
        imei=imei.replace("imei:", "");
        imei=imei.replace("android_id:", "");
        BaseDatosEngine _context = new BaseDatosEngine();
        _context = _context.open();
        String campaing= _context.GetCampaignSelect();
        _context.close();
        GPSTracker _track =new GPSTracker(Collect.getInstance().getApplicationContext());
        Tracking _traTracking=new Tracking(_track.getLongitude(),_track.getLatitude(),_track.getAccurency(),campaing,campaing,imei,LeveLBatery());
        tarea.SetTracking(_traTracking);

    }
    private int LeveLBatery()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = Collect.getInstance().getApplicationContext().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = level * 100 / scale;
        return  batteryPct;
    }
}
