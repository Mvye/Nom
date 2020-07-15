package com.mervynm.nom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.Target;
import com.mervynm.nom.R;
import com.mervynm.nom.models.Post;
import com.parse.ParseFile;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    Context context;
    List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> postList) {
        posts.addAll(postList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfilePicture;
        TextView textViewUsername;
        ImageView imageViewPostImage;
        ImageView imageViewLike;
        ImageView imageViewLocation;
        ImageView imageViewPrice;
        ImageView imageViewRecipe;
        TextView textViewLikeAmount;
        TextView textViewUsername2;
        TextView textViewDescription;
        TextView textViewCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfilePicture = itemView.findViewById(R.id.imageViewProfilePicture);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            imageViewPostImage = itemView.findViewById(R.id.imageViewPostImage);
            imageViewLike = itemView.findViewById(R.id.imageViewLike);
            imageViewLocation = itemView.findViewById(R.id.imageViewLocation);
            imageViewPrice = itemView.findViewById(R.id.imageViewPrice);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            textViewLikeAmount = itemView.findViewById(R.id.textViewLikeAmount);
            textViewUsername2 = itemView.findViewById(R.id.textViewUsername2);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewCreatedAt = itemView.findViewById(R.id.textViewCreatedAt);
        }

        public void bind(Post post) {
            Glide.with(context).load(Objects.requireNonNull(post.getAuthor().getParseFile("profilePicture")).getUrl())
                               .transform(new CircleCrop())
                               .into(imageViewProfilePicture);
            String username = post.getAuthor().getUsername();
            textViewUsername.setText(username);
            Glide.with(context).load(post.getImage().getUrl())
                               .override(Target.SIZE_ORIGINAL)
                               .into(imageViewPostImage);
            imageViewLocation.setVisibility(View.GONE);
            if (post.getPrice() == 0) {
                imageViewPrice.setVisibility(View.GONE);
            }
            if (!post.getHomemade()) {
                imageViewRecipe.setVisibility(View.GONE);
            }
            textViewLikeAmount.setText(R.string.tempLikes);
            textViewUsername2.setText(username);
            textViewDescription.setText(post.getDescription());
            textViewCreatedAt.setText(post.getCreatedAt().toString());
        }
    }
}
