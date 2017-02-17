package controll;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import model.Cluster;
import model.Data;

public class CentroidController {

	private int numClusters = 5;
	
	private ArrayList<Data> datalist;
	private ArrayList<Cluster> listOfClusters;
	private ArrayList<Cluster> specialCluster;
	private ArrayList<String> label = new ArrayList<String>();
	
	
	private final String[] labels = { "sehr_wenig", "wenig", "mittel", "viel",
			"sehr_viel" };
	private final String[] specialLabels = { "kaum_vorhanden",
			"max_Konzentration" };

	
	public CentroidController(ArrayList<Data> points) {
		this.datalist = points;
		this.listOfClusters = new ArrayList<Cluster>();
		this.specialCluster = new ArrayList<Cluster>();

		for (int i = 0; i < labels.length; i++) {
			label.add(labels[i]);
		}
	}

	public CentroidController() {
		this.datalist = new ArrayList<Data>();
		this.listOfClusters = new ArrayList<Cluster>();
		this.specialCluster = new ArrayList<Cluster>();

	}

	public ArrayList<Cluster> getSpecialCluster() {
		return specialCluster;
	}

	public ArrayList<Data> getListOfData() {
		return datalist;
	}

	public void setListOfData(ArrayList<Data> listOfData) {
		datalist = listOfData;
	}

	public void calculate() {
		boolean finish = false;
		int counter = 0;

		while (!finish) {
			// sonst haben wir Duplikate in der Liste
			clearClustersData();

			ArrayList<Data> lastCentroids = getCentroidsAsPoint();

			mapDataToCluster();

			calculateNewCentroids();

			counter++;

			ArrayList<Data> currentCentroids = getCentroidsAsPoint();

			double distanceBetweenCentroids = 0;
			for (int i = 0; i < lastCentroids.size(); i++) {
				distanceBetweenCentroids += Data.distance(lastCentroids.get(i),
						currentCentroids.get(i));
			}
			
			// Konvergenz überprüfen
			if (distanceBetweenCentroids == 0) {
				finish = true;
			}
		}
		labelingClusters();

		/************** ConcurrentModificationException ****************/
		ArrayList<String> list = new ArrayList<String>();
		for (Cluster c : listOfClusters) {
			if (c.getPoints().size() == 0) {
				list.add(c.getLabel());
			}
		}

		for (String s : list) {
			System.out.println("Methode: changeKandLabels wird aufgerufen!");
			if (numClusters > 1) {
				changeKandLabels(s);
				calculate();
			}
		}

		/************** ConcurrentModificationException ****************/
		Collections.sort(listOfClusters);
	}

	public String[] getLabels() {
		return labels;
	}

	private void changeKandLabels(String labelFromCluster) {

		numClusters--;
		System.out.println("NUM_CLUSTERS" + numClusters);
		label.remove(labelFromCluster);

		/************** ConcurrentModificationException ****************/
		ArrayList<Cluster> list = new ArrayList<Cluster>();

		for (Cluster c : listOfClusters) {
			if (c.getLabel().equals(labelFromCluster)) {
				list.add(c);
			}
		}
		listOfClusters.removeAll(list);
		/************** ConcurrentModificationException ****************/
	}

	public void init() {
		createSpecialClusterInit();
		createClustersInit();
	}

	public ArrayList<Double> withoutDuplicates(){
		ArrayList<Double> list = new ArrayList<Double>();
		for(int i = 0; i<datalist.size() - 1; i++){
			
			if(datalist.get(i + 1).getX() > datalist.get(i).getX() ){
				list.add(datalist.get(i).getX());
			}
		}
		return list;
	}
	
	public void createSpecialClusterInit() {
		ArrayList<Data> collectMin = new ArrayList<Data>();
		ArrayList<Data> collectMax = new ArrayList<Data>();
		Cluster clusterFarLeft = new Cluster(numClusters);
		Cluster clusterFarRight = new Cluster(numClusters + 1);
		ArrayList<Double> noDuplicates = withoutDuplicates();

		double min = noDuplicates.get((int) (noDuplicates.size() * 0.18));
		double max = noDuplicates.get((int) (noDuplicates.size() * 0.98));

		for (int i = 0; i < datalist.size(); i++) {
			if (datalist.get(i).getX() <= min) {
				collectMin.add(datalist.get(i));
			}
			if (datalist.get(i).getX() >= max) {
				collectMax.add(datalist.get(i));
			}
		}
		clusterFarLeft.setPoints(collectMin);
		clusterFarRight.setPoints(collectMax);
		if (specialCluster == null) {
			System.out.println("null");
		}
		specialCluster.add(clusterFarLeft);
		specialCluster.add(clusterFarRight);

		clusterFarLeft
				.setCentroid(new Data(
						(clusterFarLeft.getMaximum() + clusterFarLeft
								.getMinimum()) / 2));
		clusterFarRight
				.setCentroid(new Data(
						(clusterFarRight.getMaximum() + clusterFarRight
								.getMinimum()) / 2));

		datalist.removeAll(collectMin);
		datalist.removeAll(collectMax);
	}

	
	public void createClustersInit() {
		Random r = new Random();

		for (int i = 0; i < numClusters; i++) {
			Cluster cluster = new Cluster(i);

			Data centroid = new Data(r.nextDouble()
					* datalist.get(datalist.size() - 1).getX());

			cluster.setCentroid(centroid);
			listOfClusters.add(cluster);
		}
	}

	public void createFile(String input, String name) throws IOException {
		FileWriter fw = null;
		System.out.println(name);

		try {
			fw = new FileWriter(name);

		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);

		try {
			bw.write(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bw.close();
		fw.close();
	}

	public void labelingClusters() {
		ArrayList<Double> centroidWithX = getCentroidsAsX();

		Collections.sort(centroidWithX);
		int i = 0;

		for (Double x : centroidWithX) {
			for (Cluster c : listOfClusters) {
				if (c.getCentroid().getX() == x) {
					c.setLabel(labels[i]);
					i++;
				}
			}

		}
		for (int j = 0; j < specialCluster.size(); j++) {
			specialCluster.get(j).setLabel(specialLabels[j]);
		}
	}

	public void clearClustersData() {
		for (Cluster cluster : listOfClusters) {
			cluster.clear();
		}
	}

	public ArrayList<Data> getCentroidsAsPoint() {
		ArrayList<Data> centroids = new ArrayList<Data>(numClusters);
		for (Cluster cluster : listOfClusters) {
			centroids.add(new Data(cluster.getCentroid().getX()));
		}
		return centroids;
	}

	public ArrayList<Double> getCentroidsAsX() {
		ArrayList<Data> centroidAsPoint = getCentroidsAsPoint();
		ArrayList<Double> centroidWithX = new ArrayList<Double>();

		for (Data p : centroidAsPoint) {
			centroidWithX.add(p.getX());
		}
		return centroidWithX;
	}

	public void mapDataToCluster() {
		double max = Double.MAX_VALUE;
		double min = max;

		int clusterPosition = 0;
		double distance = 0.0;

		for (Data point : datalist) {
			min = max;
			for (int i = 0; i < numClusters; i++) {
				Cluster c = listOfClusters.get(i);
				distance = Data.distance(point, c.getCentroid());
				if (distance < min) {
					min = distance;
					clusterPosition = i;
				}
			}
			point.setCluster(clusterPosition);
			listOfClusters.get(clusterPosition).addPoint(point);
		}
	}

	public void calculateNewCentroids() {
		for (Cluster cluster : listOfClusters) {
			ArrayList<Data> list = cluster.getPoints();
			double sum = 0.0;

			for (Data point : list) {
				sum += point.getX();
			}
			int anzahl = list.size();

			Data centroid = cluster.getCentroid();

			if (anzahl != 0) {
				double newX = sum / anzahl;
				centroid.setX(newX);
			}
		}
	}

	public ArrayList<Cluster> getCluster() {
		return listOfClusters;
	}

	
	private static BigDecimal truncateDecimal(double x,int numberofDecimals)
	{
	    if ( x > 0) {
	        return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
	    } else {
	        return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
	    }
	}

	public String printFuzzy(String selected) {
		String result = "";

		ArrayList<Double> centroidWithX = getCentroidsAsX();
		ArrayList<Cluster> specialCluster = this.getSpecialCluster();
		Collections.sort(centroidWithX);

		for (int i = 0; i < centroidWithX.size(); i++) {
			for (Cluster c : getCluster()) {
				if (centroidWithX.get(i) == c.getCentroid().getX()) {
					if (i == 0) {
						if (centroidWithX.size() == 1) {
							BigDecimal calculatedMin = truncateDecimal((specialCluster.get(0)
									.getMaximum() + 0.01),4);
							result += selected + ":" + c.getLabel() + ":("
									+ calculatedMin + " | 0.0)" + " ("
									+ truncateDecimal(c.getMinimum(),4) + " | 1.0) " + "("
									+ truncateDecimal(c.getMaximum(),4) + " | 1.0) " + "("
									+ truncateDecimal(specialCluster.get(1).getMinimum(),4)
									+ " | 0.0)" + "\n";
						} else {
							result += selected
									+ ":"
									+ c.getLabel()
									+ ":("
									+ truncateDecimal((specialCluster.get(0).getMaximum() + 0.01),4)
									+ " | 0.0)" + " (" + truncateDecimal(c.getMinimum(),4)
									+ " | 1.0) " + "(" + truncateDecimal(c.getMaximum(),4)
									+ " | 1.0) " + "("
									+ truncateDecimal(centroidWithX.get(i + 1),4) + " | 0.0)"
									+ "\n";
						}
					} else if (i == centroidWithX.size() - 1) {
						result += selected + ":" + c.getLabel() + ":("
								+ truncateDecimal((centroidWithX.get(i - 1) - 0.01),4)
								+ " | 0.0) " + "(" + truncateDecimal(c.getMinimum(),4)
								+ " | 1.0) " + "(" + truncateDecimal(c.getMaximum(),4)
								+ " | 1.0) " + "("
								+ truncateDecimal(specialCluster.get(1).getMinimum(),4)
								+ " | 0.0)" + "\n";
					} else {
						result += selected + ":" + c.getLabel() + ":("
								+ truncateDecimal((centroidWithX.get(i - 1) + 0.01),4)
								+ " | 0.0) " + "(" + truncateDecimal(c.getMinimum(),4)
								+ " | 1.0) " + "(" + truncateDecimal(c.getMaximum(),4)
								+ " | 1.0) " + "(" + truncateDecimal(centroidWithX.get(i + 1),4)
								+ " | 0.0)" + "\n";

					}
				}
			}
		}
		for (Cluster c : this.getSpecialCluster()) {
			result += selected + ":" + c.getLabel() + ":(" + truncateDecimal(c.getMinimum(),4)
					+ " | 0.0) " + "(" + truncateDecimal(c.getCentroid().getX(),4) + " | 1.0) "
					+ "(" + truncateDecimal(c.getMaximum(),4) + " | 0.0)" + "\n";

		}
		return result;
	}

}