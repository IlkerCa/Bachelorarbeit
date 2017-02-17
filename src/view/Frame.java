package view;

import javax.swing.JFrame;

import controll.CentroidController;
import controll.DatenbankController;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JPanel;

public class Frame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CentroidController centroidController;
	private DatenbankController datenbankController;
	
	private PanelMenu panelMenu;
	private JPanel panelPlot;
	
	private static final int width = 1366;
	private static final int height = 768;
	private static final String title = "Datenplotter";
	
	public Frame(CentroidController centroidController, DatenbankController datenbankController) throws SQLException{
		this.centroidController = centroidController;
		this.datenbankController = datenbankController;
		setTitle(title);
		setSize(width, height);
		createContent();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		
		setVisible(true);
		
	}

	private void createContent() throws SQLException {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension minimumSize = new Dimension(1000, 700);
		setMinimumSize(minimumSize);
		getContentPane().setBackground(Color.WHITE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		createMenu();
	}

	private void createMenu() throws SQLException {
		panelMenu = new PanelMenu(this, datenbankController, centroidController);
		GridBagConstraints gbc_panelMenu = new GridBagConstraints();
		gbc_panelMenu.fill = GridBagConstraints.BOTH;
		gbc_panelMenu.insets = new Insets(5, 5, 5, 5);
		gbc_panelMenu.gridx = 0;
		gbc_panelMenu.gridy = 0;
		getContentPane().add(panelMenu, gbc_panelMenu);
	}
}
