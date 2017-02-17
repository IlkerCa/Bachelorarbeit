package controll;

import java.awt.geom.Rectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import model.Cluster;
import model.Data;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class FreeChartController extends JPanel {
	private ArrayList<Data> points = new ArrayList<Data>();

	private DatenbankController datenbankController;
	private CentroidController centroidController;
	private String naehrstoffAuswahl;
	private boolean cluster;
	private boolean fuzzy;
	private boolean einrueckung;

	public FreeChartController(DatenbankController datenbankController,
			CentroidController centroidController, String naehrstoffAuswahl,
			boolean cluster, boolean fuzzy, boolean einrueckung)
			throws SQLException {

		this.datenbankController = datenbankController;
		this.centroidController = centroidController;
		this.naehrstoffAuswahl = naehrstoffAuswahl;
		this.cluster = cluster;
		this.fuzzy = fuzzy;
		this.einrueckung = einrueckung;

		createContent();

	}

	public void createContent() throws SQLException {
		final ChartPanel chart = buildPanel();
		this.add(chart);
	}

	public double count(Double value, ArrayList<Data> list) {
		Double result = 1.0;

		for (Data d : list) {
			if (value == d.getX()) {
				result++;
			}
		}
		return result;
	}

	public ChartPanel buildPanel() throws SQLException {
		XYPlot plot = new XYPlot();

		final NumberAxis domainAxis = new NumberAxis("milligramm");
		final ValueAxis rangeAxis = new NumberAxis("Häufigkeit");
		final ValueAxis fuzzyAxis = new NumberAxis("Fuzzy-Wert");
		fuzzyAxis.setRange(0.0, 2.0);
		fuzzyAxis.setRangeWithMargins(0.0, 2.0);

		XYDataset collection = getPlotData();
		XYLineAndShapeRenderer scatterRenderer = new XYLineAndShapeRenderer(
				false, true);
		scatterRenderer.setSeriesShapesVisible(0, true);
		scatterRenderer.setSeriesShape(0, new Rectangle2D.Double(-1.0, -1.0,
				1.0, 1.0));

		plot.setDataset(0, collection);
		plot.setDomainAxis(0, domainAxis);
		plot.setRangeAxis(0, rangeAxis);
		plot.setRangeAxis(1, fuzzyAxis);
		plot.setRenderer(0, scatterRenderer);
		plot.mapDatasetToDomainAxis(0, 0);
		plot.mapDatasetToRangeAxis(0, 0);

		XYDataset collectionArea = null;

		if (cluster) {
			if (einrueckung) {
				collectionArea = getClusterData();
			} else {
				collectionArea = getClusterDataNotSpread();
			}

			XYAreaRenderer areaRender = new XYAreaRenderer();
			plot.setDataset(1, collectionArea);
			plot.setRenderer(1, areaRender);
			plot.mapDatasetToDomainAxis(1, 0);
			plot.mapDatasetToDomainAxis(1, 0);
		} else if (fuzzy) {
			XYDataset collectionFuzzy = getFuzzyClustersData();
			XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(
					true, false);
			plot.setDataset(2, collectionFuzzy);
			plot.setRenderer(2, lineRenderer);
			plot.mapDatasetToDomainAxis(2, 0);
			plot.mapDatasetToRangeAxis(2, 1);
		}

		JFreeChart chart = new JFreeChart("Data",
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);

		ChartPanel chartPanel = new ChartPanel(chart, 1100, 690, 1100, 690,
				1100, 690, false, false, false, false, true, false);
		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.repaint();

		return chartPanel;
	}

	private XYDataset getFuzzyClustersData() {
		XYSeries serie = null;
		XYSeriesCollection dataset = new XYSeriesCollection();
		ArrayList<Double> centroidWithX = centroidController.getCentroidsAsX();
		ArrayList<Cluster> listSpecial = centroidController.getSpecialCluster();

		Collections.sort(centroidWithX);

		for (int i = 0; i < centroidWithX.size(); i++) {
			for (Cluster c : centroidController.getCluster()) {
				if (centroidWithX.get(i) == c.getCentroid().getX()) {
					if (i == 0) {
						serie = new XYSeries(c.getLabel());

						if (check1Value(c)) {
							System.out.println("Check: " + i);
							Double valueMax = centroidController.getCluster()
									.get(i + 1).getMinimum();

							serie.add(listSpecial.get(0).getMaximum(),
									new Double(0.0));
							serie.add(c.getMinimum(), new Double(1.0));
							serie.add(valueMax, new Double(1.0));
							serie.add(centroidWithX.get(i + 1), new Double(0.0));

						} else {
							serie.add(listSpecial.get(0).getMaximum(),
									new Double(0.0));
							serie.add(c.getMinimum(), new Double(1.0));
							serie.add(c.getMaximum(), new Double(1.0));
							serie.add(centroidWithX.get(i + 1), new Double(0.0));

							dataset.addSeries(serie);
						}
					} else if (i == centroidWithX.size() - 1) {
						serie = new XYSeries(c.getLabel());
						if (check1Value(c)) {
							System.out.println("Check: " + i);
							Double valueMin = centroidController.getCluster()
									.get(i - 1).getMaximum();

							serie.add(centroidWithX.get(i - 1), new Double(0.0));
							serie.add(valueMin, new Double(1.0));
							serie.add(c.getMaximum(), new Double(1.0));
							serie.add(listSpecial.get(1).getMinimum(),
									new Double(0.0));
						} else {
							serie.add(centroidWithX.get(i - 1), new Double(0.0));
							serie.add(c.getMinimum(), new Double(1.0));
							serie.add(c.getMaximum(), new Double(1.0));
							serie.add(listSpecial.get(1).getMinimum(),
									new Double(0.0));
						}
						dataset.addSeries(serie);
					} else {
						serie = new XYSeries(c.getLabel());
						if (check1Value(c)) {
							System.out.println("Check: " + i);
							int position = 0;
							String[] labels = centroidController.getLabels();
							for (int j = 0; j < labels.length; j++) {
								if (c.getLabel().equals(labels[j])) {
									position = j;
								}
							}
							Double valueMin = centroidController.getCluster()
									.get(position - 1).getMaximum();
							Double valueMax = centroidController.getCluster()
									.get(position + 1).getMinimum();

							serie.add(
									centroidController.getCluster()
											.get(position - 1).getCentroid()
											.getX(), new Double(0.0));
							serie.add(valueMin, new Double(1.0));
							serie.add(valueMax, new Double(1.0));
							serie.add(
									centroidController.getCluster()
											.get(position + 1).getCentroid()
											.getX(), new Double(0.0));
						} else {
							serie.add(centroidWithX.get(i - 1), new Double(0.0));
							serie.add(c.getMinimum(), new Double(1.0));
							serie.add(c.getMaximum(), new Double(1.0));
							serie.add(centroidWithX.get(i + 1), new Double(0.0));
						}
						dataset.addSeries(serie);
					}
				}
			}
		}

		for (int j = 0; j < listSpecial.size(); j++) {
			serie = new XYSeries(listSpecial.get(j).getLabel());

			serie.add(listSpecial.get(j).getMinimum(), new Double(0.0));
			serie.add(listSpecial.get(j).getCentroid().getX(), new Double(1.0));
			serie.add(listSpecial.get(j).getMaximum(), new Double(0.0));
			dataset.addSeries(serie);
		}

		return dataset;
	}

	private XYDataset getClusterData() {
		XYSeries serie = null;
		XYSeriesCollection dataset = new XYSeriesCollection();
		ArrayList<Cluster> list = centroidController.getCluster();
		ArrayList<Cluster> listSpecial = centroidController.getSpecialCluster();

		for (int i = 0; i < list.size(); i++) {
			serie = new XYSeries(list.get(i).getLabel());
			Double highestCount = highestCount(list.get(i));

			if (i == 0) {
				serie.add((listSpecial.get(0).getMaximum() + list.get(i)
						.getMinimum()) / 2, highestCount);
				serie.add((list.get(i).getMaximum() + list.get(i + 1)
						.getMinimum()) / 2, highestCount);
				dataset.addSeries(serie);

			} else if (i == list.size() - 1) {
				serie.add((list.get(i).getMinimum() + list.get(i - 1)
						.getMaximum()) / 2, highestCount);
				serie.add((list.get(i).getMaximum() + listSpecial.get(1)
						.getMinimum()) / 2, highestCount);
				dataset.addSeries(serie);

			} else {
				serie.add((list.get(i).getMinimum() + list.get(i - 1)
						.getMaximum()) / 2, highestCount);
				serie.add((list.get(i).getMaximum() + list.get(i + 1)
						.getMinimum()) / 2, highestCount);
				dataset.addSeries(serie);
			}

		}
		// specialclusters
		for (int j = 0; j < listSpecial.size(); j++) {
			serie = new XYSeries(listSpecial.get(j).getLabel());
			Double highestCount = highestCount(listSpecial.get(j));

			if (j == 0) {

				serie.add(listSpecial.get(j).getMinimum(), highestCount);
				serie.add((listSpecial.get(j).getMaximum() + list.get(0)
						.getMinimum()) / 2, highestCount);
				dataset.addSeries(serie);
			} else if (j == listSpecial.size() - 1) {
				serie.add(
						(listSpecial.get(j).getMinimum() + list.get(
								list.size() - 1).getMaximum()) / 2,
						highestCount);
				serie.add(listSpecial.get(j).getMaximum(), highestCount);
				dataset.addSeries(serie);
			}
		}

		return dataset;
	}

	private XYDataset getClusterDataNotSpread() {
		XYSeries serie = null;
		XYSeriesCollection dataset = new XYSeriesCollection();
		ArrayList<Cluster> list = centroidController.getCluster();
		ArrayList<Cluster> listSpecial = centroidController.getSpecialCluster();

		for (int i = 0; i < list.size(); i++) {
			serie = new XYSeries(list.get(i).getLabel());
			Double highestCount = highestCount(list.get(i));

			if (i == 0) {
				serie.add(list.get(0).getMinimum(), highestCount);
				serie.add(list.get(0).getMaximum(), highestCount);
				dataset.addSeries(serie);

			} else if (i == list.size() - 1) {
				serie.add(list.get(i).getMinimum(), highestCount);
				serie.add(list.get(i).getMaximum(), highestCount);
				dataset.addSeries(serie);

			} else {
				serie.add(list.get(i).getMinimum(), highestCount);
				serie.add(list.get(i).getMaximum(), highestCount);
				dataset.addSeries(serie);
			}

		}
		// specialclusters
		for (int j = 0; j < listSpecial.size(); j++) {
			serie = new XYSeries(listSpecial.get(j).getLabel());
			Double highestCount = highestCount(listSpecial.get(j));

			if (j == 0) {

				serie.add(listSpecial.get(j).getMinimum(), highestCount);
				serie.add((listSpecial.get(j).getMaximum() + list.get(0)
						.getMinimum()) / 2, highestCount);
				dataset.addSeries(serie);
			} else if (j == listSpecial.size() - 1) {
				serie.add(
						(listSpecial.get(j).getMinimum() + list.get(
								list.size() - 1).getMaximum()) / 2,
						highestCount);
				serie.add(listSpecial.get(j).getMaximum(), highestCount);
				dataset.addSeries(serie);
			}
		}

		return dataset;
	}

	private boolean check1Value(Cluster c) {
		Set<Double> sort = new HashSet<Double>();
		for (Data d : c.getPoints()) {
			sort.add(d.getX());
		}

		if (sort.size() == 1) {
			return true;
		}

		return false;
	}

	private XYDataset getPlotData() {
		XYSeries series = new XYSeries("Datenpunkte");
		XYSeriesCollection dataset = new XYSeriesCollection();
		points = datenbankController.getNaehrstoffe(naehrstoffAuswahl);

		centroidController.setListOfData(points);
		centroidController.init();
		centroidController.calculate();

		for (Cluster c : centroidController.getCluster()) {
			ArrayList<Data> list = c.getPoints();
			for (Data d : list) {
				series.add(d.getX(), count(d.getX(), list));
			}
		}
		for (Cluster c : centroidController.getSpecialCluster()) {
			ArrayList<Data> list = c.getPoints();
			for (Data d : list) {
				series.add(d.getX(), count(d.getX(), list));
			}
		}

		dataset.addSeries(series);

		return dataset;
	}

	private Double highestCount(Cluster cluster) {
		Double max = null;

		ArrayList<Data> data = cluster.getPoints();
		max = count(data.get(0).getX(), data);

		for (Data d : data) {
			if (count(d.getX(), data) > max) {
				max = count(d.getX(), data);

			}
		}

		return max;
	}
}
