package com.mervynm.nom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mervynm.nom.R;
import com.mervynm.nom.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

public class ProfileFragment extends HomeFragment {

    ImageView imageViewProfilePicture;
    TextView textViewUsername;
    RecyclerView recyclerViewPosts;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupVariables(view);
        setUpProfileBar();
    }

    public void setupVariables(View view) {
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);
        textViewUsername = view.findViewById(R.id.textViewUsername);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
    }

    private void setUpProfileBar() {
        ParseUser user = ParseUser.getCurrentUser();
        Glide.with(Objects.requireNonNull(getContext())).load(Objects.requireNonNull(user.getParseFile("profilePicture")).getUrl())
                .transform(new CircleCrop())
                .into(imageViewProfilePicture);
        textViewUsername.setText(user.getUsername());
    }

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.whereEqualTo(Post.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> postList, ParseException e) {
                if (e != null) {
                    Log.e("ProfileFragment", "Issue with getting posts", e);
                    return;
                }
                for (Post post : postList) {
                    Log.i("ProfileFragment", "Post " + post.getDescription() +  ", username " + post.getAuthor().getUsername() + " ," + post.getPrice());
                }
                adapter.clear();
                adapter.addAll(postList);
            }
        });
    }

}