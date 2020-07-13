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

* 1. User can take a picture with the app
* 2. User can make posts with descriptions
    * Descriptions can be locations (maybe use google places api?)
    * Descriptions can be recipes (links or as text)
    * Descriptions can have price 
    * Descriptions can have a description of the food, maybe what's written in the description can be searchable.
* 3. Users can like other people's posts
    * Posts have buttons that let you "like" them
* 4. Users can search for other people's posts
* 5. Users can view the posts they've made
* 6. Clicking on a post will open up a detailed view
    * Shows posts likes and full description

**Optional Nice-to-have Stories**

* S1. Users can follow other users
* S2. Users have a feed where followed user's stories show up
* S3. Users can edit their posts

### 2. Screen Archetypes

* Home page with buttons that allow you to view your profile, make a new post, and search
   * 5
   * 4
   * 3
   * 1
   * S2
       * 6
* Profile
   * 5
   * S3
* New post
    * 2
    * 1
* Search
    * 6
    * 4
    * 3
    * S1
* Detailed Post view
    * 6
    * 3

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Profile
* New Post
* Search
* Home

**Flow Navigation** (Screen to Screen)

* Home
   * Profile
   * New Post
   * Search
* Search
   * Detailed Post View
   * Home
   * New Post
   * Profile
* New Post
    * Camera
    * Home (new post appears on homepage after making it)
* Profile
    * Detailed Post View
    * Home 
    * New Post
    * Search
* Detailed Post View
    * Home
    * New Post
    * Search
    * Profile

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://i.imgur.com/Q3num9P.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
