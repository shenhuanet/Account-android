package com.shenhua.account.bean;

import java.io.Serializable;

public class LimitData implements Serializable {

	private static final long serialVersionUID = -4424759660758711393L;
	private String type;
	private String used;
	private String limit;
	private String progress;
	private String fenpei_use;
	private String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFenpei_use() {
		if (fenpei_use == null) {
			return "0";
		}
		return fenpei_use;
	}

	public void setFenpei_use(String fenpei_use) {
		this.fenpei_use = fenpei_use;
	}

	public LimitData(String type, String used, String progress, String limit,
			String fenpei_use, String color) {
		this.type = type;
		this.used = used;
		this.limit = limit;
		this.progress = progress;
		this.fenpei_use = fenpei_use;
		this.color = color;
	}

	public String getProgress() {
		if (progress == null) {
			return "0";
		}
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsed() {
		if (used == null) {
			return "0 ￥";
		}
		return used + "￥";
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getLimit() {
		if (limit == null || limit.equals("0")) {
			return "0 ￥";
		}
		try {
			String[] i = limit.split("\\.");
			if (i[1].length() == 2) {
				return limit + " ￥";
			}
		} catch (Exception e) {
			return limit + "0 ￥";
		}
		return limit + "0 ￥";
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

}
