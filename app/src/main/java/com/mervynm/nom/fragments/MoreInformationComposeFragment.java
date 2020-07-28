package com.mervynm.nom.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.hootsuite.nachos.NachoTextView;
import com.mervynm.nom.R;
import com.mervynm.nom.models.Location;
import com.mervynm.nom.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MoreInformationComposeFragment extends Fragment implements EasyPermissions.PermissionCallbacks{

    private static final int RC_LOCATION = 33;
    Button buttonUseCurrentLocation;
    ImageView imageViewLocationPicture;
    TextView textViewLocation;
    EditText editTextRecipe;
    EditText editTextPrice;
    NachoTextView nachoTextViewTags;
    Button buttonPost;
    PlacesClient placesClient;
    File photoFile;
    String description;
    Boolean homemade;
    Location postLocation;
    String recipeUrl;
    double priceDouble;
    List<String> tags;

    public MoreInformationComposeFragment() {
    }

    public static MoreInformationComposeFragment newInstance(File photoFile, String description, Boolean homemade) {
        MoreInformationComposeFragment moreInformationComposeFragment = new MoreInformationComposeFragment();
        Bundle args = new Bundle();
        args.putSerializable("image", photoFile);
        args.putString("description", description);
        args.putBoolean("homemade", homemade);
        moreInformationComposeFragment.setArguments(args);
        return moreInformationComposeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        photoFile = (File) getArguments().getSerializable("image");
        description = getArguments().getString("description");
        homemade = getArguments().getBoolean("homemade");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_information_compose, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupVariables(view);
        setupNachoTextView();
        initializePlaces();
        setUpAutocompleteSupportFragment();
        setupOnClickListener();
    }

    private void setupVariables(View view) {
        buttonUseCurrentLocation = view.findViewById(R.id.buttonUseCurrentLocation);
        imageViewLocationPicture = view.findViewById(R.id.imageViewLocationPicture);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        editTextRecipe = view.findViewById(R.id.editTextRecipe);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        nachoTextViewTags = view.findViewById(R.id.nachoTextViewTags);
        buttonPost = view.findViewById(R.id.buttonPost);
    }

    private void setupNachoTextView() {
        String[] tagSuggestions = new String[]{"sour", "spicy", "sweet", "Japanese", "Indian", "Italian", "vegetarian", "vegan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_dropdown_item_1line, tagSuggestions);
        nachoTextViewTags.setAdapter(adapter);
    }

    private void initializePlaces() {
        Places.initialize(Objects.requireNonNull(getContext()), getResources().getString(R.string.api_key));
        placesClient = Places.createClient(getContext());
    }

    private void setUpAutocompleteSupportFragment() {
        assert getFragmentManager() != null;
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.PRICE_LEVEL,
                Place.Field.PHOTO_METADATAS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                Log.i("MoreInfoCompose", "Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getRating() + ", " + place.getPriceLevel());
                textViewLocation.setText(String.format("Using location: %s", place.getName()));
                postLocation = createLocation(place);
            }

            @Override
            public void onError(@NotNull Status status) {
                Log.i("MoreInfoCompose", "An error occurred: " + status);
            }
        });
    }

    private Location createLocation(Place place) {
        final Location location = new Location();
        location.setName(place.getName());
        location.setAddress(place.getAddress());
        if (place.getRating() != null) {
            location.setRating(place.getRating());
        }
        if (place.getPriceLevel() != null) {
            location.setPriceLevel(place.getPriceLevel());
        }
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            Log.w("MoreInfoCompose", "No photo metadata.");
        } else {
            final PhotoMetadata photoMetadata = metadata.get(0);
            final String attributions = photoMetadata.getAttributions();
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                @Override
                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    location.setPicture(persistImage(bitmap));
                    imageViewLocationPicture.setImageBitmap(bitmap);
                }
            });
        }
        return location;
    }

    private ParseFile persistImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();
        ParseFile picture = new ParseFile("locationPicture", bitmapBytes);
        try {
            picture.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return picture;
    }

    private void setupOnClickListener() {
        buttonUseCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestToUseFineLocation();
            }
        });
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfInfoInputtedIsCorrect();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentPlace() {
        FindCurrentPlaceRequest findCurrentPlaceRequest = FindCurrentPlaceRequest
                .builder(Arrays.asList(Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.RATING,
                        Place.Field.PRICE_LEVEL,
                        Place.Field.PHOTO_METADATAS)).build();
        placesClient.findCurrentPlace(findCurrentPlaceRequest).addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
            @Override
            public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                for (PlaceLikelihood placeLikelihood : findCurrentPlaceResponse.getPlaceLikelihoods()) {
                    Log.i("MoreInfoCompose", String.format("Place '%s' has likelihood: %f",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
            }
        });
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void requestToUseFineLocation() {
        String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), perm)) {
            getCurrentPlace();
        }
        else {
            /*EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_LOCATION, perm)
                            .setRationale("Request Fine Location Access")
                            .setPositiveButtonText("Ok")
                            .setNegativeButtonText("cancel")
                            .setTheme(R.style.AppTheme)
                            .build());*/
            EasyPermissions.requestPermissions(this, "Request Permission", RC_LOCATION, perm);
        }
    }

    private void checkIfInfoInputtedIsCorrect() {
        boolean location = false;
        boolean recipe = false;
        boolean price = false;
        boolean tag = false;
        if (!textViewLocation.getText().toString().isEmpty()) {
            location = true;
        }
        if (!editTextRecipe.getText().toString().isEmpty()) {
            if (URLUtil.isValidUrl(editTextRecipe.getText().toString())) {
                recipe = true;
                recipeUrl = editTextRecipe.getText().toString();
            }
            else {
                Toast.makeText(getContext(), "Invalid recipe URL", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (!editTextPrice.getText().toString().isEmpty()) {
            String pattern = "^([1-9]{1}[0-9]*|0{0,2})(\\.?)(\\d\\d?)$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(editTextPrice.getText().toString());
            if (m.find()) {
                priceDouble = Double.parseDouble(editTextPrice.getText().toString());
                price = true;
            }
            else {
                Toast.makeText(getContext(), "Invalid price, please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!nachoTextViewTags.getChipValues().isEmpty()) {
            tag = true;
            tags = nachoTextViewTags.getChipValues();
            Log.i("MoreInfoCompose", "This is the list " + nachoTextViewTags.getChipValues().toString());
        }
        createPost(location, recipe, price, tag);
    }

    private void createPost(boolean location, boolean recipe, boolean price, boolean tag) {
        Post post = new Post();
        post.setAuthor(ParseUser.getCurrentUser());
        post.setImage(new ParseFile(photoFile));
        post.setKeyDescription(description);
        post.setHomemade(homemade);
        if (location) {
            if (saveLocation()) {
                post.setLocation(postLocation);
            }
            else {
                Toast.makeText(getContext(), "Location could not be saved", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (recipe) {
            post.setRecipeUrl(recipeUrl);
        }
        if (price) {
            post.setPrice(priceDouble);
        }
        if (tag) {
            post.setTags(tags);
        }
        savePost(post);
    }

    private boolean saveLocation() {
        final boolean[] save = {true};
        postLocation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("MoreInfoCompose", "Issue saving location", e);
                    Toast.makeText(getContext(), "Issue saving location", Toast.LENGTH_SHORT).show();
                    save[0] = false;
                    return;
                }
                Log.i("MoreInfoCompose", "Location successfully saved");
            }
        });
        return save[0];
    }

    private void savePost(Post post) {
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("MoreInfoCompose", "Issue saving post", e);
                    Toast.makeText(getContext(), "Issue saving post", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("MoreInfoCompose", "Post successsfully saved");
                goToHome();
            }
        });
    }

    private void goToHome() {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new HomeFragment()).addToBackStack("MoreInfo").commit();
    }
}
