package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reference {

	@SerializedName("type")
	@Expose
	private final String type;
	
	@SerializedName("id")
	@Expose
	private final String id;

	public Reference(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}