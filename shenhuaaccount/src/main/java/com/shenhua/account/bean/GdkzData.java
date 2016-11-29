package com.shenhua.account.bean;

import java.io.Serializable;

public class GdkzData implements Serializable {

	private static final long serialVersionUID = -9095188441876694036L;
	private String name, money, color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public GdkzData(String name, String money, String color) {
		this.name = name;
		this.money = money;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoney() {
		try {
			String[] i = money.split("\\.");
			if ((i[1].length() == 1)) {
				return money + "0";
			}
		} catch (Exception e) {
		}
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

}
