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
			String n = LigaManager.usuario.getNombre();
			String s = "SELECT JUG.* FROM JUGADOR JUG, JUEGA"
					+ "WHERE JUG.ID=JUEGA.ID_JUGADOR "
					+ "AND JUEGA.NOMBRE_USUARIO="+n+";";
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
			ResultSet rs = st.executeQuery("SELECT NOMBRE FROM USUARIO");
			while(rs.next()) {
				String n = rs.getString("NOMBRE");
				if(rs.getString("NOMBRE").equalsIgnoreCase(username)) {
					return -1;
				}
			}
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO USUARIO VALUES (?,?,?);");
			pst.setString(1, username);
			pst.setString(2, pass);
			pst.setInt(3, teamID);
			pst.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
			return -2;
		}
		return 0;
	}

	/**
	 * Metodo para verificar la identidad del usuario.
	 * @return tid si los valores introducidos son correctos |
	 * -1 si los valores introductidos son incorrectos |
	 * -2 si se da algun error interno
	 * */
	public static int login(String username, String pass) {
		try {
			PreparedStatement pst = conexion.prepareStatement("SELECT * FROM USUARIO WHERE NOMBRE=? AND PASSWORD=?;");
			pst.setString(1, username);
			pst.setString(2, pass);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				//System.out.println(rs.getString(0));
				return rs.getInt(3);
			}
			return -1;
		} catch(Exception e) {
			e.printStackTrace();
			return -2;
		}
	}
	
	static boolean crearTablas() {
		crearTablaUsuario();
		crearTablaJugador();
		crearTablaTemporada();
		crearTablaJuega();
		return true;
	}
	
	static boolean crearTablaUsuario() {
		try {
			st.executeUpdate("DROP TABLE IF EXISTS USUARIO");
			st.executeUpdate("CREATE TABLE USUARIO ("
						   + "NOMBRE STRING NOT NULL PRIMARY KEY,"
						   + "PASSWORD STRING,"
						   + "ID_EQUIPO INTEGER)");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	static boolean crearTablaJugador() {
		try {
			st.executeUpdate("DROP TABLE IF EXISTS JUGADOR");
			st.executeUpdate("CREATE TABLE JUGADOR ("
						   + "ID INTEGER NOT NULL PRIMARY KEY,"
						   + "NOMBRE STRING,"
						   + "ANYO_NAC INTEGER,"
						   + "POSICION INTEGER,"
						   + "OVERALL INTEGER,"
						   + "REBOTE INTEGER,"
						   + "TIRO_LIBRE INTEGER,"
						   + "TIRO_CERCA INTEGER,"
						   + "TIRO_LEJOS INTEGER,"
						   + "DEFENSA INTEGER,"
						   + "ASISTENCIA INTEGER)");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	static boolean crearTablaTemporada() {
		try {
			st.executeUpdate("DROP TABLE IF EXISTS TEMPORADA");
			st.executeUpdate("CREATE TABLE TEMPORADA ("
						   + "ANYO_INICIO INTEGER NOT NULL,"
						   + "NOMBRE_USUARIO STRING NOT NULL REFERENCES USUARIO(NOMBRE),"
						   + "TID_GANA INTEGER,"
						   + "ID_MVP INTEGER REFERENCES JUGADOR(ID),"
						   + "ID_ROY INTEGER REFERENCES JUGADOR(ID),"
						   + "ID_DPOY INTEGER REFERENCES JUGADOR(ID),"
						   + "ID_SMOY INTEGER REFERENCES JUGADOR(ID),"
						   + "PRIMARY KEY(ANYO_INICIO, NOMBRE_USUARIO))");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	static boolean crearTablaJuega() {
		try {
			st.executeUpdate("DROP TABLE IF EXISTS JUEGA");
			st.executeUpdate("CREATE TABLE JUEGA ("
						   + "ID_J INTEGER NOT NULL REFERENCES JUGADOR(ID) ON DELETE CASCADE,"
						   + "FECHA_INICIO DATE NOT NULL,"
						   + "FECHA_FIN DATE,"
						   + "NOMBRE_USUARIO STRING NOT NULL REFERENCES USUARIO(NOMBRE),"
						   + "TID INTEGER,"
						   + "ANYOS_CON_RESET INTEGER,"
						   + "SALARIO INTEGER,"
						   + "PUNTOS INTEGER,"
						   + "REBOTES INTEGER,"
						   + "ASISTENCIAS INTEGER,"
						   + "PARTIDOS_JUGADOS INTEGER,"
						   + "PRIMARY KEY (ID_J,FECHA_INICIO, NOMBRE_USUARIO))");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static int getPrimerIdLibreJugador() {
		try {
			ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM JUGADOR");
			while(rs.next()) {
				return rs.getInt(1)+1;
			}
			return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static boolean guardarJugador(Jugador j) {
		try {
			/*st.executeUpdate("INSERT INTO JUGADOR VALUES ("
						   + ""+j.getID()+","+j.getNombre()+","+j.getAnyoNac()+","+j.getPosicion().ordinal()+","
						   + j.getOverall()+","+j.getRebote()+","+j.getTiroLibre()+","+j.getTiroCerca()+","
						   + j.getTiroLejos()+","+j.getDefensa()+","+j.getAsistencia()+","+j.getCondicionFisica()+")");
			*/
			if(j.getTid() < -1) return false;
			
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO JUGADOR VALUES"
															+ "(?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1,j.getID());
			pst.setString(2, j.getNombre());
			pst.setInt(3, j.getAnyoNac());
			pst.setInt(4, j.getPosicion().ordinal());
			pst.setInt(5, j.getOverall());
			pst.setInt(6, j.getRebote());
			pst.setInt(7, j.getTiroLibre());
			pst.setInt(8, j.getTiroCerca());
			pst.setInt(9, j.getTiroLejos());
			pst.setInt(10, j.getDefensa());
			pst.setInt(11, j.getAsistencia());
			
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void borrarJugador(Jugador j) {
		try {
			PreparedStatement pst = conexion.prepareStatement("DELETE FROM JUGADOR WHERE ID=?");
			pst.setInt(1, j.getID());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		conectar();
		crearTablas();
		
	}
}
