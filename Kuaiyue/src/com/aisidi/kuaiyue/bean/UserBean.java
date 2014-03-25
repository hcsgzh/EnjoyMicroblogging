package com.aisidi.kuaiyue.bean;

import java.io.Serializable;

public class UserBean implements Serializable{

	private String userId;
	private String screen_name;
	private String remark;
	private String profile_image_url;
	private String avatar_large;
	private int statuses_count;
	private int followers_count;
	private int friends_count;
	private int favourites_count;
	private boolean following;
	private String location;
	private String description;
	private String gender;
	private boolean verified;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProfile_image_url() {
		return profile_image_url;
	}
	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}
	public String getAvatar_large() {
		return avatar_large;
	}
	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}
	public int getStatuses_count() {
		return statuses_count;
	}
	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}
	public int getFollowers_count() {
		return followers_count;
	}
	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}
	public int getFriends_count() {
		return friends_count;
	}
	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}
	public int getFavourites_count() {
		return favourites_count;
	}
	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}
	public boolean isFollowing() {
		return following;
	}
	public void setFollowing(boolean following) {
		this.following = following;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	
}
