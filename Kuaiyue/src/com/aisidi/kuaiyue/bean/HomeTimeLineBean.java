package com.aisidi.kuaiyue.bean;

import java.io.Serializable;

public class HomeTimeLineBean implements Serializable {

	protected long id;
	private long cid;
	private String[] pic_urls;
	protected String text;
	protected String source;
	protected String created_at;
	protected int reposts_count;
	protected int comments_count;
	protected int attitudes_count;
	private long re_id;
	protected String re_text;
	private String re_created_at;
	private String re_source;
	private String[] re_pic_urls;
	
	protected UserBean user;
	protected UserBean re_user;
	
	 
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String[] getPic_urls() {
		return pic_urls;
	}
	public void setPic_urls(String[] pic_urls) {
		this.pic_urls = pic_urls;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public int getReposts_count() {
		return reposts_count;
	}
	public void setReposts_count(int reposts_count) {
		this.reposts_count = reposts_count;
	}
	public int getComments_count() {
		return comments_count;
	}
	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}
	public int getAttitudes_count() {
		return attitudes_count;
	}
	public void setAttitudes_count(int attitudes_count) {
		this.attitudes_count = attitudes_count;
	}
	public long getRe_id() {
		return re_id;
	}
	public void setRe_id(long re_id) {
		this.re_id = re_id;
	}
	public String getRe_text() {
		return re_text;
	}
	public void setRe_text(String re_text) {
		this.re_text = re_text;
	}
	public String getRe_created_at() {
		return re_created_at;
	}
	public void setRe_created_at(String re_created_at) {
		this.re_created_at = re_created_at;
	}
	public String getRe_source() {
		return re_source;
	}
	public void setRe_source(String re_source) {
		this.re_source = re_source;
	}
	public String[] getRe_pic_urls() {
		return re_pic_urls;
	}
	public void setRe_pic_urls(String[] re_pic_urls) {
		this.re_pic_urls = re_pic_urls;
	}
	public UserBean getUser() {
		return user;
	}
	public void setUser(UserBean user) {
		this.user = user;
	}
	public UserBean getRe_user() {
		return re_user;
	}
	public void setRe_user(UserBean re_user) {
		this.re_user = re_user;
	}
	
	
}
