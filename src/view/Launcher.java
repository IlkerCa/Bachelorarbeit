package view;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import model.Data;
import controll.CentroidController;
import controll.DatenbankController;


public class Launcher {
	private CentroidController centroidController;
	private DatenbankController datenbankController;

	public static void main(String[] args){
		Launcher launcher = new Launcher();

		launcher.init();
		launcher.window();
	}
	
	public void init(){
		centroidController = new CentroidController();
		try {
			datenbankController = new DatenbankController();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"KEINE VERBINDUNG ZUR DATENBANK!");
		}
	}
	
	public void window(){
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			new Frame(centroidController, datenbankController);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
