package datos;

import java.sql.*;

public class BD {

	/**
	 * Clase desde la que se manejara la base de datos,
	 * todas las consultas y conexiones se realizaran desde esta clase.
	 * */
	
	static final String DIRECTORIO = "data/database.db";
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	static Connection conexion;
	
	/**
	 * Metodo que se conecta con la base de datos.
	 * @return true si la conexion se establece correctamente, false si hay algun problema.
	 * */
	public static boolean conectar() {
		try {
			conexion = DriverManager.getConnection("jdbc:sqlite:"+DIRECTORIO);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
