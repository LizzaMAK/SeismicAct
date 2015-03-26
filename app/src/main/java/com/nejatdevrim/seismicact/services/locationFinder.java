package com.nejatdevrim.seismicact.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class locationFinder {

    private Context context;

    public locationFinder(Context context) {
        this.context = context;
    }

    public Location getReferencePoint() {
        return getLastKnownLocation();
    }

    //Getting last known location
    private Location getLastKnownLocation() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

		/* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        return l;
    }
}