package com.ruthvik.mapclustering;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.ruthvik.mapclustering.model.MapPost;
import com.ruthvik.mapclustering.model.MarkerItem;
import com.ruthvik.mapclustering.utils.Util;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<MarkerItem>, ClusterManager.OnClusterClickListener<MarkerItem> {


    // Important: Donot forget to call PlotResults method on API response or if static Data call it after appending your Data


    private MapFragment mapFragment;
    private GoogleMap map;
    private ClusterManager<MarkerItem> mClusterManager;
    private boolean showCluster = true;
    private LatLng latilong;
    DecimalFormat format = new DecimalFormat(".######");
    private GoogleApiClient googleApiClient;
    private android.location.Location lastLocation;

    @BindView(R.id.location_marker)
    public ImageView locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buildGoogleApiClient();
        initMaps();
    }

    private void initMaps() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //map styling
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
                if (!success) {
                    // Log.e(TAG, "Style parsing failed.");
                    Toast.makeText(this, "Style parsing failed", Toast.LENGTH_LONG).show();
                }
            } catch (Resources.NotFoundException e) {
                //Log.e(TAG, "Can't find style. Error: ", e);
                Toast.makeText(this, "Can't find style. Error", Toast.LENGTH_LONG).show();
            }
            mClusterManager = new ClusterManager<MarkerItem>(this, map);
            mClusterManager.setRenderer(new DefaultClusterRenderer<MarkerItem>(getApplicationContext(), map, mClusterManager) {

                @Override
                protected boolean shouldRenderAsCluster(Cluster cluster) {
                    if (showCluster && cluster.getSize() > 1) {
                        Collection<MarkerItem> items = cluster.getItems();
                        boolean equal = true;
                        MarkerItem firstItem = null;
                        for (MarkerItem markerItem : items) {
                            if (firstItem == null) {
                                firstItem = markerItem;
                            } else {
                                if (!markerItem.getPosition().equals(firstItem.getPosition())) {
                                    equal = false;
                                    break;
                                }
                            }
                        }
                        if (equal) {//if all positions are exactly equal, form a cluster or else depend on default logic
                            return true;
                        } else {
                            return cluster.getSize() >= Util.getMinClusterSize(MainActivity.this);
                        }
                    }
                    return false;
                }

                @Override
                protected void onBeforeClusterItemRendered(MarkerItem item, MarkerOptions
                        markerOptions) {
                    Object object = item.getObject();
                    if (object == null) {
                        super.onBeforeClusterItemRendered(item, markerOptions);
                        return;
                    }
                    if (object instanceof MapPost) {
                        MapPost mapPost = (MapPost) object;
                        markerOptions.title(mapPost.getTitle()).icon(getBitmap(mapPost, null));
                    }
                }

                @Override
                protected void onBeforeClusterRendered
                        (Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
                    markerOptions.icon(getBadgeBitmap(cluster.getItems()));
                }
            });
            map.setMyLocationEnabled(true);
            if (latilong != null) {
                map.moveCamera(CameraUpdateFactory.newLatLng(latilong));
            }
            map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()

            {
                @Override
                public void onCameraIdle() {
                    //zoomLevel = map.getCameraPosition().zoom;
                    mClusterManager.onCameraIdle();
                    LatLng newLatLng = map.getCameraPosition().target;
                    if (latilong != null && format.format(newLatLng.latitude).equals(format.format(latilong.latitude))
                            && format.format(newLatLng.longitude).equals(format.format(latilong.longitude))) {
                        return;
                    }
                    latilong = newLatLng;

                    //Make API Call with New location
                }
            });
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
            });
            map.setOnInfoWindowClickListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterItemClickListener(this);
            mClusterManager.setOnClusterClickListener(this);
            // Make API call for Data from server
            /*if (localtion!=null){
                invokeAPI();
            }*/

            locationMarker.setVisibility(View.VISIBLE);
        }
    }


    private void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MarkerItem> cluster) {
        return false;
    }

    @Override
    public boolean onClusterItemClick(MarkerItem markerItem) {
        return false;
    }


    public BitmapDescriptor getBitmap(MapPost post, String defaultValue) {
        String categoryId = null;
        if (post != null) {
            categoryId = post.getCategoryId();
        }
        int resId = R.drawable.marker_default_issue_large;
        String txt = TextUtils.isEmpty(defaultValue) ? "O" : defaultValue;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), conf);
        Canvas canvas1 = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.marker_text_size));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        // paint.setTypeface(TypefaceUtils.getFontSemiBold(this, "1"));

        canvas1.drawBitmap(bitmap, 0, 0, paint);
        canvas1.drawText(txt, bitmap.getWidth() / 2, (bitmap.getHeight() / 2 - ((paint.descent() + paint.ascent()) / 2)), paint);
        return BitmapDescriptorFactory.fromBitmap(bmp);
    }

    public BitmapDescriptor getBadgeBitmap(Collection<MarkerItem> markerItems) {
        int resId = R.drawable.marker_default_issue_large;
        String txt = "O";
        int size = markerItems.size();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), conf);
        Canvas canvas1 = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.marker_text_size));//A
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        //paint.setTypeface(TypefaceUtils.getFontSemiBold(this, "1"));

        Paint badgeTxtPaint = new Paint();
        badgeTxtPaint.setTextSize(getResources().getDimension(R.dimen.marker_cluster_count_text_size));//5
        badgeTxtPaint.setTextAlign(Paint.Align.CENTER);
        badgeTxtPaint.setColor(Color.WHITE);
        //badgeTxtPaint.setTypeface(TypefaceUtils.getFontSemiBold(this, "1"));

        Paint badgePaint = new Paint();
        badgePaint.setColor(ContextCompat.getColor(this, R.color.colorAccent));

        float badgeWidth = getResources().getDimension(R.dimen.badge_heigth_width_newsmap);
        float badgeHeight = getResources().getDimension(R.dimen.badge_heigth_width_newsmap);

        canvas1.drawBitmap(bitmap, 0, 0, paint);
        canvas1.drawText(txt, bitmap.getWidth() / 2, (bitmap.getHeight() / 2 - ((paint.descent() + paint.ascent()) / 2)), paint);
        canvas1.drawCircle(bitmap.getWidth() - badgeWidth / 2, badgeHeight / 2, badgeWidth / 2, badgePaint);
        canvas1.drawText(String.valueOf(size), bitmap.getWidth() - badgeWidth / 2, (badgeHeight / 2 - ((badgeTxtPaint.descent() + badgeTxtPaint.ascent()) / 2)), badgeTxtPaint);
        return BitmapDescriptorFactory.fromBitmap(bmp);
    }


    private void plotResults(List<MapPost> communityPostList) {
        if (communityPostList == null || communityPostList.size() == 0) {
            return;
        }
        if (map != null) {
            for (MapPost mapPost : communityPostList) {
                com.ruthvik.mapclustering.model.Location location = mapPost.getLocation();
                if (location == null) {
                    continue;
                }
                mClusterManager.addItem(new MarkerItem(location.getLatitude(), location.getLongitude(), mapPost.getTitle(), mapPost));
            }
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latilong, zoom);
            map.animateCamera(cameraUpdate);
            mClusterManager.cluster();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    private void getLastKnownLocation() {
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (googleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, MainActivity.this);
                googleApiClient.disconnect();
            }
        } catch (Exception e) {
        }
    }


    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }


}
