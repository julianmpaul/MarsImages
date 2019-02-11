package org.nasa.api.mars.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataImageSet 
{
	private String date;
	private String error;
	private final List<String> images = new ArrayList<String>();
	private File folder;
	private boolean complete;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean getComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public File getFolder() {
		return folder;
	}
	public void setFolder(File folder) {
		this.folder = folder;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<String> getImages() {
		return images;
	}
	public void addImage(String imageUrl) {
		images.add(imageUrl);
	}
}
