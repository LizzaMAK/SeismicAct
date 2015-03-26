package com.nejatdevrim.seismicact;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import com.nejatdevrim.seismicact.services.locationFinder;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;



/**
 * An activity representing a list of Depremler. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DepremDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DepremListFragment} and the item details
 * (if present) is a {@link DepremDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link DepremListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class DepremListActivity extends FragmentActivity
        implements DepremListFragment.Callbacks {
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deprem_list);

        if (findViewById(R.id.deprem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((DepremListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.deprem_list))
                    .setActivateOnItemClick(true);
        }

        Parse.initialize(this, "UOD0s9cdbuzMHd2IfI1cDUQquZC12XQGCMoqNCDT", "asKS99xFysty2GDAY42mNMjFuBzGdy9pr2DOVa1p");

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();

        saveUserProfile();

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link DepremListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(DepremDetailFragment.ARG_ITEM_ID, id);
            DepremDetailFragment fragment = new DepremDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.deprem_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DepremDetailActivity.class);
            detailIntent.putExtra(DepremDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    public void getBestLoc() {
        String msg = "";

        locationFinder lf=new locationFinder(this);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        //String provider = locationManager.getBestProvider(criteria, false);

        //Log.d("",provider);

        //Location myLocation = locationManager.getLastKnownLocation(provider);
        Location myLocation = lf.getReferencePoint();
        if (myLocation != null) {
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            msg = latitude + "|" + longitude;
        } else {
            msg = "Location not found...";
        }
        Log.d("",msg);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void saveUserProfile() {

        getBestLoc();
        //TODO: get location
        double gotLatitude = -122.1541545;
        double gotLongitude = 37.456789;

        ParseInstallation.getCurrentInstallation().put("lastLatitude", Double.valueOf(gotLatitude));
        ParseInstallation.getCurrentInstallation().put("lastLongitude", Double.valueOf(gotLongitude));
        ParseInstallation.getCurrentInstallation().put("gender", "female");

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.alert_dialog_success, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    e.printStackTrace();

                    Toast toast = Toast.makeText(getApplicationContext(), R.string.alert_dialog_failed, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
