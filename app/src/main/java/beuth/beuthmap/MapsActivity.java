package beuth.beuthmap;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static beuth.beuthmap.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


        Button but0 = (Button) findViewById(R.id.button0);
        Button but1 = (Button) findViewById(R.id.button1);


        but0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GeoJsonLayer layer0 = null;

                try {
                    layer0 = new GeoJsonLayer(mMap, R.raw.level0,
                            getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                assert layer0 != null;
                layer0.addLayerToMap();


            }
        });

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GeoJsonLayer layer1 = null;

                try {
                    layer1 = new GeoJsonLayer(mMap, R.raw.level1,
                            getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                assert layer1 != null;
                layer1.addLayerToMap();

            }
        });

        //Spinner

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.room_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng beuth = new LatLng(52.544307, 13.352703);
        mMap.addMarker(new MarkerOptions().position(beuth).title("Beuth Hochschule für Technik Berlin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(beuth));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        InputStream inputStream = getResources().openRawResource(R.raw.coordinates);
        String file = null;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line + "\n");
            }
            file = sb.toString();
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
            }
        }


        try {

            JSONObject jsonObj = new JSONObject(file);
            JSONArray features = jsonObj.getJSONArray("features");

            if (marker != null) {
                marker.remove();
            }

            mMap.clear();

            for (int i = 0; i < features.length(); i++) {

                if (features.getJSONObject(i).getJSONObject("properties").getString("ref").equals(parent.getSelectedItem().toString())) {

                    JSONArray coordinates = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                    String latitude = coordinates.getString(0);
                    String longitude = coordinates.getString(1);

                    double lat = Double.parseDouble(latitude);
                    double lng = Double.parseDouble(longitude);

                    LatLng raum = new LatLng(lat, lng);

                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(raum));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private GeoJsonLayer layer1;
    private GeoJsonLayer layer0;

    public void onClick0(View view) {
        if (this.layer0 == null) {
            try {
                this.layer0 = new GeoJsonLayer(mMap, R.raw.level0, getApplicationContext());
                GeoJsonPolygonStyle style = layer0.getDefaultPolygonStyle();
                style.setStrokeColor(0xff888888);
                style.setStrokeWidth(5);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        this.layer0.addLayerToMap();
        if (this.layer1 != null) { this.layer1.removeLayerFromMap(); }
    }


    public void onClick1(View view) {
        if (this.layer1 == null) {
            try {
                this.layer1 = new GeoJsonLayer(mMap, R.raw.level1, getApplicationContext());
                    GeoJsonPolygonStyle style = layer1.getDefaultPolygonStyle();
                    style.setStrokeColor(0xff888888);
                    style.setStrokeWidth(5);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        this.layer1.addLayerToMap();
        if (this.layer0 != null) { this.layer0.removeLayerFromMap(); }
    }
}



