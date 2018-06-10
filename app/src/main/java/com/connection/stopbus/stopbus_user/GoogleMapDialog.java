package com.connection.stopbus.stopbus_user;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by mjin on 2018-06-07.
 */

public class GoogleMapDialog extends DialogFragment{
    private static final String TAG = "sb";

    MapView mapView;
    GoogleMap googleMap;

    private String stationName;
    private LatLng latlng;

    public static GoogleMapDialog newInstance(String stationName, LatLng latlng) {
        GoogleMapDialog dialog = new GoogleMapDialog();
        Bundle args = new Bundle();

        args.putString("stationName", stationName);
        args.putDouble("latitude", latlng.latitude);
        args.putDouble("longitude", latlng.longitude);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            stationName = getArguments().getString("stationName");
            latlng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.google_map, null);
        ((TextView)view.findViewById(R.id.dialogTitle)).setText(stationName);

        /* Google Map Setting */
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        if (mapView != null) {
            Log.d(TAG, "NOT NULL!");
            mapView.getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(GoogleMap map) {
                    Log.d(TAG, "onMapReady() called in getMapAsync()");
                    googleMap = map;

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.9f));
                    googleMap.addMarker(new MarkerOptions().position(latlng).title(stationName));
                }
            });
        } else {
            Log.d(TAG, "NULL!");
        }

        builder.setView(view);
        Dialog dialog = builder.create();

        return dialog;
    }

    private void dismissDialog() {
        this.dismiss();
    }

}