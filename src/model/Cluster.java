package model;

import java.util.ArrayList;
import java.util.Collections;

public class Cluster implements Comparable<Cluster> {
	private int id;
	private ArrayList<Data> data;
	private Data centroid;
	private String label;

	public Cluster(int id) {
		this.id = id;
		this.data = new ArrayList<Data>();
		this.centroid = null;
		this.label = "";
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public ArrayList<Data> getPoints() {
		return data;
	}

	public void addPoint(Data point) {
		data.add(point);
	}

	public void setPoints(ArrayList<Data> data) {
		this.data = data;
	}

	public Data getCentroid() {
		return centroid;
	}

	public void setCentroid(Data centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}

	public void clear() {
		data.clear();
	}

	public Double getMinimum() {
		ArrayList<Double> result = new ArrayList<Double>();
		for (Data d : data) {
			result.add(d.getX());
		}
		Collections.sort(result);
		if (result.size() == 0) {
			result.add(centroid.getX());
		}
		return result.get(0);
	}

	public Double getMaximum() {
		ArrayList<Double> result = new ArrayList<Double>();
		for (Data d : data) {
			result.add(d.getX());
		}
		Collections.sort(result);
		if (result.size() == 0) {
			result.add(centroid.getX());
		}
		return result.get(result.size() - 1);
	}

	@Override
	public int compareTo(Cluster compare) {
		int value = 0;
		if (this.getCentroid().getX() < compare.getCentroid().getX()) {
			value = -1;
		} else {
			value = 1;
		}
		return value;
	}
}