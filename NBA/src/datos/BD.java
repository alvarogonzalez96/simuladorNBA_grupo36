package datos;

import java.sql.*;
import java.util.ArrayList;

import negocio.*;

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
	static Statement st;

	/**
	 * Metodo que se conecta con la base de datos.
	 * @return true si la conexion se establece correctamente | false si hay algun problema
	 * */
	public static boolean conectar() {
		try {
			conexion = DriverManager.getConnection("jdbc:sqlite:"+DIRECTORIO);
			st = conexion.createStatement();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static ArrayList<Jugador> cargarJugadores() {
		try {
			int id = LigaManager.usuario.getID();
			String s = "SELECT JUG.* FROM JUGADOR JUG, JUEGA"
					+ "WHERE JUG.ID=JUEGA.ID_JUGADOR "
					+ "AND JUEGA.ID_USUARIO="+id+";";
			ResultSet rs = st.executeQuery(s);

			ArrayList<Jugador> jugadores = new ArrayList<>();

			while(rs.next()) {
				Jugador j = new Jugador();
				jugadores.add(j);
			}

			return jugadores;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Metodos de registro / login

	/**
	 * Metodo que registra un usuario nuevo en la BD.
	 * @return int >= 0 si todo va bien (ese numero sera el id de usuario) |
	 * -1 si el nombre de usuario elegido ya existe |
	 * -2 si ha habido algun otro tipo de error
	 * */
	public static int registrar(String username, String pass, int teamID) {
		try {
			ResultSet rs = st.executeQuery("SELECT ID,NOM_USUARIO FROM USUARIO ORDER BY ID DESC");
			int maxID = 0;
			while(rs.next()) {
				int id = rs.getInt("ID");
				if(id > maxID) {
					maxID = id;
				}
				if(rs.getString("NOM_USUARIO").equalsIgnoreCase(username)) {
					return -1;
				}
			}
			maxID++;
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO USUARIO VALUES (?,?,?,?);");
			pst.setInt(1, maxID);
			pst.setString(2, username);
			pst.setString(3, pass);
			pst.setInt(4, teamID);
			pst.executeUpdate();
			return maxID;
		} catch(Exception e) {
			e.printStackTrace();
			return -2;
		}
	}

	/**
	 * Metodo para verificar la identidad del usuario.
	 * @return 1 si los valores introducidos son correctos |
	 * -1 si los valores introductidos son incorrectos |
	 * -2 si se da algun error interno
	 * */
	public static int login(String username, String pass) {
		try {
			PreparedStatement pst = conexion.prepareStatement("SELECT NOM_USUARIO,PASS WHERE NOM_USUARIO=? AND PASS=?;");
			pst.setString(1, username);
			pst.setString(2, pass);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				return 1;
			}
			return -1;
		} catch(Exception e) {
			return -2;
		}
	}
}
