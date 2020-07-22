package com.mervynm.nom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mervynm.nom.R;
import com.mervynm.nom.adapters.PostAdapter;
import com.mervynm.nom.models.Location;
import com.mervynm.nom.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewPosts;
    List<Post> posts;
    PostAdapter adapter;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        queryPosts();
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
        adapter = new PostAdapter(getContext(), posts, onLocationClickListener);
        recyclerViewPosts.setAdapter(adapter);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void createLocationDialog(Post post) throws ParseException {
        Location postLocation = post.getLocation();
        String name = postLocation.fetchIfNeeded().getString("name");
        double rating = postLocation.fetchIfNeeded().getDouble("rating");
        String address = postLocation.fetchIfNeeded().getString("address");
        int priceLevel = postLocation.fetchIfNeeded().getInt("priceLevel");
        LocationDialogFragment locationDialogFragment = LocationDialogFragment.newInstance(name,
                                                                                           rating,
                                                                                           address,
                                                                                           priceLevel);
        assert getFragmentManager() != null;
        locationDialogFragment.show(getFragmentManager(), "fragment_location_dialog");
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
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
}