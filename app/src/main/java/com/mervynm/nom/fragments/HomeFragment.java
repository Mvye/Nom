package com.mervynm.nom.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mervynm.nom.R;
import com.mervynm.nom.adapters.PostAdapter;
import com.mervynm.nom.models.Location;
import com.mervynm.nom.models.Post;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment {

    private static final int RC_LOCATION = 33;
    Toolbar toolbar;
    SearchView searchView;
    SwipeRefreshLayout swipeContainer;
    FusedLocationProviderClient fusedLocationProviderClient;
    RecyclerView recyclerViewPosts;
    List<Post> posts;
    PostAdapter adapter;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSwipeRefreshLayout(view);
        setupRecyclerView(view);
        setupToolbar(view);
        queryPosts();
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void requestToUseFineLocation() {
        String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), perm)) {
            sortByDistance();
        }
        else {
            EasyPermissions.requestPermissions(this, "This feature requires Location permission, request permission?", RC_LOCATION, perm);
        }
    }

    private void setupSwipeRefreshLayout(View view) {
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupRecyclerView(View view) {
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        posts = new ArrayList<>();
        PostAdapter.OnLocationClickListener onLocationClickListener = new PostAdapter.OnLocationClickListener() {
            @Override
            public void OnLocationClicked(int position) {
                try {
                    createLocationDialog(posts.get(position));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        PostAdapter.OnRecipieClickListener onRecipieClickListener = new PostAdapter.OnRecipieClickListener() {
            @Override
            public void OnRecipeClicked(int position) {
                createRecipeDialog(posts.get(position));
            }
        };
        adapter = new PostAdapter(getContext(), posts, onLocationClickListener, onRecipieClickListener);
        recyclerViewPosts.setAdapter(adapter);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void createLocationDialog(Post post) throws ParseException {
        Location postLocation = post.getLocation();
        String name = postLocation.fetchIfNeeded().getString("name");
        double rating = postLocation.fetchIfNeeded().getDouble("rating");
        String address = postLocation.fetchIfNeeded().getString("address");
        int priceLevel = postLocation.fetchIfNeeded().getInt("priceLevel");
        String pictureUrl;
        if (postLocation.fetchIfNeeded().getParseFile("picture") != null) {
            pictureUrl = Objects.requireNonNull(postLocation.fetchIfNeeded().getParseFile("picture")).getUrl();
        }
        else {
            pictureUrl = "";
        }
        LocationDialogFragment locationDialogFragment = LocationDialogFragment.newInstance(name,
                                                                                           rating,
                                                                                           address,
                                                                                           priceLevel,
                                                                                           pictureUrl);
        assert getFragmentManager() != null;
        locationDialogFragment.show(getFragmentManager(), "fragment_location_dialog");
    }

    private void createRecipeDialog(Post post) {
        String recipeUrl = post.getRecipeUrl();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeUrl));
        if (browserIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            startActivity(browserIntent);
        }
    }

    protected void setupToolbar(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_sort);
        toolbar.setOverflowIcon(drawable);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sortCreatedAt) {
                    Toast.makeText(getContext(), "sort created at", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.sortPrice) {
                    Toast.makeText(getContext(), "sort price", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.sortDistance) {
                    requestToUseFineLocation();
                }
                return true;
            }
        });
        searchView = (SearchView) toolbar.getMenu().findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void sortByDistance() {
        Task<android.location.Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    android.location.Location lastKnownLocation = task.getResult();
                    assert lastKnownLocation != null;
                    adapter.sortByDistance(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    Log.i("HomeFragment", "the coords are " + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
                }
                else {
                    Toast.makeText(getContext(), "Could not get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void queryPosts() {
        final ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        final List<ParseUser> followingUsers = new ArrayList<>();
        final ParseQuery<ParseObject> following = ParseUser.getCurrentUser().getRelation("following").getQuery();
        following.include("User");
        following.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                for (ParseObject user : users) {
                    followingUsers.add((ParseUser) user);
                }
                followingUsers.add(ParseUser.getCurrentUser());
                query.whereContainedIn(Post.KEY_AUTHOR, followingUsers);
                query.setLimit(20);
                query.addDescendingOrder(Post.KEY_CREATED_AT);
                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> postList, ParseException e) {
                        if (e != null) {
                            Log.e("Homefragment", "Issue with getting posts", e);
                            return;
                        }
                        for (Post post : postList) {
                            Log.i("Homefragment", "Post " + post.getDescription() +  ", username " + post.getAuthor().getUsername() + " ," + post.getPrice());
                        }
                        adapter.clear();
                        adapter.addAll(postList);
                    }
                });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}