package com.gary.model;

import java.io.Serializable;

public class UserModel implements Serializable {
	private static final long serialVersionUID = -8682747338103651103L;
	
	private String id;
	private String name;
	private boolean sex;
	
	public UserModel() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return id + "(" + name + ")" + (sex ? "男" : "女") ;
	}
	
}
