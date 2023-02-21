package org.openhds.hdsscapture.wrapper;

import java.util.List;

public class DataWrapper<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public DataWrapper() {
	}

	public DataWrapper(List<T> data) {
		this.data = data;
	}

}
