package com.mervynm.nom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.mervynm.nom.models.Location;
import com.mervynm.nom.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {

    MaterialToolbar toolbarBackButton;
    private GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<String> usedLocations = new ArrayList<>();
    boolean isFirstClick = true;

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        setUpToolbar();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        toolbarBackButton = findViewById(R.id.toolbarBackButton);
        setSupportActionBar(toolbarBackButton);
        Drawable backArrowWhite = getResources().getDrawable(R.drawable.ic_back);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(backArrowWhite);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        queryPostsWithLatLong(googleMap);
        moveCameraToCurrentPosition();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                isFirstClick = true;
                marker.showInfoWindow();
                return true;
            }
        });
    }

    private void moveCameraToCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<android.location.Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    android.location.Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                        Log.i("MapViewActivity", "the coords are " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
                    }
                }
            }
        });
    }

    private void queryPostsWithLatLong(final GoogleMap googleMap) {
        final ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.whereExists(Post.KEY_LOCATION);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> postList, ParseException e) {
                for (Post post: postList) {
                    Location postLocation = post.getLocation();
                    if (usedLocations.contains(postLocation.getObjectId())) {
                        continue;
                    }
                    else {
                        usedLocations.add(postLocation.getObjectId());
                    }
                    ParseGeoPoint latLong = null;
                    try {
                        latLong = post.getLocation().fetchIfNeeded().getParseGeoPoint("latLong");
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    if (latLong != null) {
                        LatLng postLatLong = new LatLng(latLong.getLatitude(), latLong.getLongitude());
                        Marker m = googleMap.addMarker(new MarkerOptions()
                                 .position(postLatLong));
                        m.setTag(post);
                    }
                }
                setUpInfoWindow();
            }
        });
    }

    private void setUpInfoWindow() {
        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("MapViewActivity", "it was indeed cicked");
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        Log.i("MapViewActivity", "marker shown");
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.post_info_window, null);
        ImageView imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);
        ImageView imageViewPostImage = view.findViewById(R.id.imageViewPostImage);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        Post clickedPost = (Post) marker.getTag();
        assert clickedPost != null;
        String profilePictureUrl = Objects.requireNonNull(clickedPost.getAuthor().getParseFile("profilePicture")).getUrl();
        Glide.with(this).load(profilePictureUrl)
                .transform(new CircleCrop())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageViewProfilePicture);
        String username = clickedPost.getAuthor().getUsername();
        textViewUsername.setText(username);
        String postImageUrl = clickedPost.getImage().getUrl();
        Glide.with(this).load(postImageUrl)
                .override(Target.SIZE_ORIGINAL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (isFirstClick) {
                            isFirstClick = false;
                            marker.hideInfoWindow();
                            marker.showInfoWindow();
                        }
                        return false;
                    }
                })
                .into(imageViewPostImage);
        SpannableString usernameAndDescription = new SpannableString(username + " " + clickedPost.getDescription());
        usernameAndDescription.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewDescription.setText(usernameAndDescription);
        return view;
    }
}