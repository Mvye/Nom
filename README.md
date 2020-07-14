Original App Design Project - README
===

# Nom

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Social media app centered around food. Users can take and share pictures of their food and include information on location/price/description/recipes. Others can view, search, and like your posts. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social Media
- **Mobile:** Mobile would allow you to take pictures of food with the camera as well as update your feed on the go (say at a restaurant).
- **Story:** Connect people to each other through food, expose people to a variety of food, and make it easier for people to try or make new food.
- **Market:** Food lovers, people who want to get into cooking, those traveling.
- **Habit:** People would use this app when they want to get food ideas for cooking or want to know some food recommendations while traveling. People can also use the app to share something they've made or enjoyed eating recently. 
- **Scope:** V1 Users can take pictures on the app and make a post with them. Users can add additional information with their post such as the location (if the meal is from a restaurant), recipie (if the meal is homemade), price (how much the meal cost to get or make), and description of the meal. V2 Users can search for and like other people's food posts.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

1. User can take pictures with the app
2. User can make posts 
    a. Posts are required to have an image, description, and (new idea) boolean representing whether or not it’s homemade
    b. Posts can optionally have price, location (if it’s not homemade), and recipe (if it’s homemade)
    c. Posts can optionally have tags that can help with searching, like if it’s spicy, sweet, sour, Mexican, healthy [I’m thinking this could be an Array of Strings]
3. Users can like other user’s posts (potentially should be a stretch feature?)
4. User can search for posts via tags (using queryManager), sorting home feed (optional, backup complex algorithm)
5. There’s three different main views (fragments) accessible with bottom nav bar
    a. HomeFeed -  list of posts made by user’s the current user is following and the user
    b. NewPost - allows user to take a picture and add a description. Can add optional location, price, and recipe. 
    c. Profile - shows a user’s profile including their username, profile picture (stretch story?), as well as a list of posts they’ve made
6. User can double tap to like a post (iffy on this), User can scroll to refresh HomeFeed
7. [Here would be stories that incorporate external libraries for visual polish and animations]

**Optional Nice-to-have Stories**

* 1. User can edit their own posts
* 2. User can click on a post for a detailed post view
* 3. User can click on a posts user’s profile picture to go to their profile
* 4. Allow user to choose an image from gallery when making a post

### 2. Screen Archetypes

* LoginActivity
* MainActivity with bottom navigation bar
* HomeFragment
* NewPostFragment1
* NewPostFragment2
* ProfileFragment

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Profile
* New Post
* Home

**Flow Navigation** (Screen to Screen)

* Home
   * Profile
   * New Post
* New Post
    * Camera
    * Home (cancel)
    * New Post2
* New Post 2
    * Home (after posting the post, brings you to home)
* Profile
    * Home 
    * New Post

## Wireframes
[Must be Updated]
[]()
<img src="https://i.imgur.com/Q3num9P.jpg" width=600>

## Schema 
[This section will be completed in Unit 9]
### Models
**Posts**
| Property    | Type   | Description                             |
| ----------- | ------ | --------------------------------------  |
| objectId    | String | unique id for the user's post           |
| author      | Pointer to User |  post author                   |
| image       | File   | image that user takes/posts             |
| description | String | description of food/recipe              |
| tags        | Array  | an array of strings that represent tags |
| homemade    | Boolean | boolean representing if meal homemade or not |
| price       | Number | cost/price of the meal                  |
| likeCount   | Number | amount of likes for post                |
| location    | JSON Object | location of the meal               |
| createdAt   | DateTime | date when post is created             | 
| updatedAt   | DateTime | date when post is updated             |

**User**
| Property  | Type     | Description                                   |
| --------- | -------- | --------------------------------------------- |
| objectId  | String   | unique id of the user                         |
| username  | String   | unique username of user                       |
| password  | String   | hidden password of user                       |
| createdAt | DateTime | date account was created                      |
| followers | Array | array of pointers to users that follow the users |
| following | Array | array of pointers to users that the user follows |
| profileImage | File | user's profile picture                         |


### Networking

* Home Feed Screen:
    * (GET) Query posts from User's following list
    ```java
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER, Post.getCurrentUser().getFollowing());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post " + post.getDescription() +  ", username " + post.getUser().getUsername());
                }
                postAdapter.addAll(posts);
            }
        });``` 
