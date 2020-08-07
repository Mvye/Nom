package com.mervynm.nom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mervynm.nom.R;
import com.mervynm.nom.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends HomeFragment {

    ImageView imageViewProfilePicture;
    TextView textViewUsername;
    Button buttonFollow;
    RecyclerView recyclerViewPosts;
    ParseUser userOfProfile;
    List<String> followingUsers;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        userOfProfile = getArguments().getParcelable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupVariables(view);
        setFollowOnClick();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setFollowOnClick() {
        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFollowingAndButton(true);
            }
        });
    }

    private void updateFollowingAndButton(final boolean pressedButton) {
        buttonFollow.setClickable(false);
        followingUsers = new ArrayList<>();
        final ParseRelation<ParseUser> following = ParseUser.getCurrentUser().getRelation("following");
        if (pressedButton && buttonFollow.getText().equals("Unfollow")) {
            following.remove(userOfProfile);
        }
        else if (pressedButton && buttonFollow.getText().equals("Follow")){
            following.add(userOfProfile);
        }
        if (pressedButton) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    updateButton(following);
                }
            });
        }
        else {
            updateButton(following);
        }
    }

    private void updateButton(ParseRelation<ParseUser> following) {
        final ParseQuery<ParseUser> followingList = following.getQuery();
        followingList.include("User");
        followingList.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                for (ParseUser user : users) {
                    followingUsers.add(user.getObjectId());
                }
                if (followingUsers.contains(userOfProfile.getObjectId())) {
                    buttonFollow.setText(R.string.unfollow);
                }
                else {
                    buttonFollow.setText(R.string.follow);
                }
                buttonFollow.setClickable(true);
            }
        });
    }

    @Override
    protected void setupToolbar(View view) {
        Glide.with(Objects.requireNonNull(getContext())).load(Objects.requireNonNull(userOfProfile.getParseFile("profilePicture")).getUrl())
                .transform(new CircleCrop())
                .into(imageViewProfilePicture);
        textViewUsername.setText(userOfProfile.getUsername());
        if (userOfProfile.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            buttonFollow.setVisibility(View.GONE);
        }
        else {
            buttonFollow.setVisibility(View.VISIBLE);
            updateFollowingAndButton(false);
        }
    }

    public void setupVariables(View view) {
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);
        textViewUsername = view.findViewById(R.id.textViewUsername);
        buttonFollow = view.findViewById(R.id.buttonFollow);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
    }

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.whereEqualTo(Post.KEY_AUTHOR, userOfProfile);
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