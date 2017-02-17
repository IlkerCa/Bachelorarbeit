package controll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import model.Data;

public class DatenbankController {

	private Connection connection = null;

	public DatenbankController() throws SQLException {
		init();
	}

	public void init() throws SQLException {
		getConnection();
	}

	/**
	 * Verbindung zur Datenbank herstellen
	 * 
	 * @return Connection - Die Datenbankverbindung
	 ***/
	private Connection getConnection() throws SQLException {
		if (connection == null) {
			// Verbindung zum Driver
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			connection = (Connection) DriverManager
					.getConnection("jdbc:sqlite:MyDatabase.sqlite");
		} else {
			if (connection.isClosed()) {
				connection = null;
				return getConnection();
			}
		}
		return connection;
		
	}

	public ArrayList<String> getNaehrstoffeNamen() {
		ArrayList<String> result = new ArrayList<String>();

		String sql = "SELECT * FROM foodsData";

		try {
			// Statement vorbereiten
			PreparedStatement statement = getConnection().prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();

			// Inhalte aus der Tabelle holen
			for (int i = 4; i <= resultSet.getMetaData().getColumnCount(); i++) {
				result.add(resultSet.getMetaData().getColumnName(i));
			}
			Collections.sort(result);
			result.remove("KCAL");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Data> getNaehrstoffe(String name) {
		ArrayList<String> dbResult = new ArrayList<String>();
		ArrayList<Data> result = new ArrayList<Data>();

		String sql = "SELECT " + name + " FROM foodsData as fd ";

		try {
			// Statement vorbereiten
			PreparedStatement statement = getConnection().prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				dbResult.add(resultSet.getString(name));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Entfernung der nulls
		dbResult = checkNull(dbResult);

		// double umparsen
		ArrayList<Double> listOfDouble = parseToDouble(dbResult);

		Collections.sort(listOfDouble);

		for (Double s : listOfDouble) {
			result.add(new Data(s));
		}

		
		return result;
	}

	private ArrayList<Double> parseToDouble(ArrayList<String> input) {
		ArrayList<Double> list = new ArrayList<Double>();
		for (String s : input) {
			list.add(Double.parseDouble(s));
		}
		return list;
	}


	public ArrayList<String> checkNull(ArrayList<String> list) {
		ArrayList<String> result = new ArrayList<String>();
		int counter = 0;
		for (String s : list) {
			if (s != null) {
				result.add(s);
			}
			if (s == null) {
				counter++;
			}
		}
		return result;
	}
}
