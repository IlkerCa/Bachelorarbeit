package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import model.Data;
import controll.CentroidController;
import controll.DatenbankController;
import controll.FreeChartController;

public class PanelMenu extends JPanel {

	private DatenbankController datenbankController;
	private Frame frame;
	private JPanel panelPlot;
	private CentroidController centroidController;

	JComboBox<String> comboBoxNaehrstoffe;

	private String[] naehrstoffeArray;

	public PanelMenu(Frame frame, DatenbankController datenbankController,
			CentroidController centroidController) throws SQLException {
		this.datenbankController = datenbankController;
		this.frame = frame;
		this.centroidController = centroidController;

		naehrstoffeArray = new String[this.datenbankController
				.getNaehrstoffeNamen().size()];

		createContent();
	}

	private void createContent() throws SQLException {
		GridBagLayout gbl_panelMenu = new GridBagLayout();
		gbl_panelMenu.columnWidths = new int[] { 200, 0 };
		gbl_panelMenu.rowHeights = new int[] { 40, 20, 20, 20, 20, 20, 20, 20,
				20, 20, 20, 20, 20, 20, 20, 20, 0 };
		gbl_panelMenu.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelMenu.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		setLayout(gbl_panelMenu);

		// Label fuer die Nährstoffe
		JLabel lblnaehrstoffe = new JLabel("Nährstoffe:");

		GridBagConstraints gbc_lblnaehrstoffeKriterien = new GridBagConstraints();
		gbc_lblnaehrstoffeKriterien.fill = GridBagConstraints.BOTH;
		gbc_lblnaehrstoffeKriterien.insets = new Insets(0, 0, 5, 0);
		gbc_lblnaehrstoffeKriterien.gridx = 0;
		gbc_lblnaehrstoffeKriterien.gridy = 1;
		add(lblnaehrstoffe, gbc_lblnaehrstoffeKriterien);

		comboBoxNaehrstoffe = new JComboBox<String>(datenbankController
				.getNaehrstoffeNamen().toArray(naehrstoffeArray));

		GridBagConstraints gbc_comboBoxNaehrstoffe = new GridBagConstraints();
		gbc_comboBoxNaehrstoffe.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxNaehrstoffe.anchor = GridBagConstraints.SOUTH;
		gbc_comboBoxNaehrstoffe.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxNaehrstoffe.gridx = 0;
		gbc_comboBoxNaehrstoffe.gridy = 2;
		add(comboBoxNaehrstoffe, gbc_comboBoxNaehrstoffe);

		// Label fuer die Darstellungsart
		JLabel lblArt = new JLabel("Darstellungsart:");

		GridBagConstraints gbc_lblArt = new GridBagConstraints();
		gbc_lblArt.fill = GridBagConstraints.BOTH;
		gbc_lblArt.insets = new Insets(0, 0, 5, 0);
		gbc_lblArt.gridx = 0;
		gbc_lblArt.gridy = 4;
		add(lblArt, gbc_lblArt);

		final JComboBox<String> comboBoxArt = new JComboBox<String>();
		comboBoxArt.addItem("RAW");
		comboBoxArt.addItem("Cluster");
		comboBoxArt.addItem("Fuzzy-Cluster");

		GridBagConstraints gbc_comboBoxArt = new GridBagConstraints();
		gbc_comboBoxArt.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxArt.anchor = GridBagConstraints.SOUTH;
		gbc_comboBoxArt.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxArt.gridx = 0;
		gbc_comboBoxArt.gridy = 5;
		add(comboBoxArt, gbc_comboBoxArt);

		JCheckBox checkBoxClusterNeighbor = new JCheckBox("Cluster einrücken");
		GridBagConstraints gbc_checkBoxClusterNeighbor = new GridBagConstraints();
		gbc_checkBoxClusterNeighbor.insets = new Insets(0, 0, 5, 0);
		gbc_checkBoxClusterNeighbor.anchor = GridBagConstraints.SOUTH;
		gbc_checkBoxClusterNeighbor.fill = GridBagConstraints.HORIZONTAL;
		gbc_checkBoxClusterNeighbor.gridx = 0;
		gbc_checkBoxClusterNeighbor.gridy = 7;
		add(checkBoxClusterNeighbor, gbc_checkBoxClusterNeighbor);

		JButton button = new JButton("Zeichnen!");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.anchor = GridBagConstraints.SOUTH;
		gbc_button.fill = GridBagConstraints.HORIZONTAL;
		gbc_button.gridx = 0;
		gbc_button.gridy = 9;
		add(button, gbc_button);

		JButton fuzzyWerte = new JButton("Daten printen");
		GridBagConstraints gbc_fuzzyWerte = new GridBagConstraints();
		gbc_fuzzyWerte.insets = new Insets(0, 0, 5, 0);
		gbc_fuzzyWerte.anchor = GridBagConstraints.SOUTH;
		gbc_fuzzyWerte.fill = GridBagConstraints.HORIZONTAL;
		gbc_fuzzyWerte.gridx = 0;
		gbc_fuzzyWerte.gridy = 11;
		add(fuzzyWerte, gbc_fuzzyWerte);

		JButton dataAllPrint = new JButton("Alle Daten printen");
		GridBagConstraints gbc_dataAllPrint = new GridBagConstraints();
		gbc_dataAllPrint.insets = new Insets(0, 0, 5, 0);
		gbc_dataAllPrint.anchor = GridBagConstraints.SOUTH;
		gbc_dataAllPrint.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataAllPrint.gridx = 0;
		gbc_dataAllPrint.gridy = 15;
		add(dataAllPrint, gbc_dataAllPrint);

		ActionListener listenerDataPrint = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() instanceof JButton) {
					String selected = (String) comboBoxNaehrstoffe
							.getSelectedItem();

					printFunction(selected, fileChooser());
					System.out.println("Fertig geprintet");
				}
			}
		};
		fuzzyWerte.addActionListener(listenerDataPrint);

		ActionListener listenerDataAllPrint = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() instanceof JButton) {
					String selected = (String) comboBoxNaehrstoffe
							.getSelectedItem();
					try {
						printAllFunction(naehrstoffeArray, fileChooser());
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Alle Daten fertig geprintet");
				}
			}
		};
		dataAllPrint.addActionListener(listenerDataAllPrint);

		ActionListener listenerZeichnen = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() instanceof JButton) {
					try {
						String type = (String) comboBoxArt.getSelectedItem();
						boolean selected = checkBoxClusterNeighbor.isSelected();
						if (type.equals("Cluster")) {
							createPlotPanel(
									(String) comboBoxNaehrstoffe
											.getSelectedItem(),
									true, false, selected);
						} else if (type.equals("Fuzzy-Cluster")) {
							createPlotPanel(
									(String) comboBoxNaehrstoffe
											.getSelectedItem(),
									false, true, selected);
						} else {
							createPlotPanel(
									(String) comboBoxNaehrstoffe
											.getSelectedItem(),
									false, false, selected);
							createPlotPanel((String) comboBoxNaehrstoffe
									.getSelectedItem(), false, false, false);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		};
		button.addActionListener(listenerZeichnen);

		//Anfangsplot
		createPlotPanel("EALA", false, false, false);

	}

	private String fileChooser() {
		JFrame parentFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");

		int userSelection = fileChooser.showSaveDialog(parentFrame);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			return fileToSave.getAbsolutePath();
		}
		return "Fehler: FileChooser";
	}

	
	private void createPlotPanel(String selected, boolean cluster,
			boolean fuzzy, boolean einrueckung) throws SQLException {

		if (panelPlot != null)
			frame.getContentPane().remove(panelPlot);
		
		panelPlot = new FreeChartController(datenbankController,
				new CentroidController(), selected, cluster, fuzzy, einrueckung);

		GridBagConstraints gbc_panelPlot = new GridBagConstraints();
		gbc_panelPlot.insets = new Insets(5, 25, 5, 25);
		gbc_panelPlot.fill = GridBagConstraints.BOTH;
		gbc_panelPlot.gridx = 1;
		gbc_panelPlot.gridy = 0;
		frame.getContentPane().add(panelPlot, gbc_panelPlot);
		frame.repaint();
		panelPlot.repaint();

		frame.pack();
	}

	private void printFunction(String selected, String path) {
		ArrayList<Data> points;
		CentroidController cc = null;
		String result = "#FUZZY-KB, V1.2" + "\n" + "#SETS" + "\n";

		try {
			DatenbankController dc = new DatenbankController();
			cc = new CentroidController();
			points = dc.getNaehrstoffe(selected);

			cc.setListOfData(points);
			cc.init();
			cc.calculate();

			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		result += cc.printFuzzy(selected) + "#RULES \n" + "#END";
		try {
			cc.createFile(result, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printAllFunction(String[] array, String path)
			throws IOException {
		ArrayList<Data> points;
		String result = "#FUZZY-KB, V1.2" + "\n" + "#SETS" + "\n";
		CentroidController cc = null;
		for (int i = 0; i < array.length; i++) {
			try {
				DatenbankController dc = new DatenbankController();
				cc = new CentroidController();
				points = dc.getNaehrstoffe(array[i]);

				cc.setListOfData(points);
				cc.init();
				cc.calculate();

				result += cc.printFuzzy(array[i]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		result += "#RULES \n" + "#END";
		cc.createFile(result, path);
	}

}
