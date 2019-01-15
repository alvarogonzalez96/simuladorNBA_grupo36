package datos;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import negocio.*;

public class BD {

	/**
	 * Clase desde la que se manejara la base de datos,
	 * todas las consultas y conexiones se realizaran desde esta clase.
	 * */

	static final String DIRECTORIO = "data/database.db";
	
	static SimpleDateFormat sdf; 

	static {
		sdf = new SimpleDateFormat("dd/MM/yyyy");
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
						   + "NOMBRE_USUARIO STRING NOT NULL REFERENCES USUARIO(NOMBRE),"
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
						   + "MVP INTEGER REFERENCES JUGADOR(ID),"
						   + "ROY INTEGER REFERENCES JUGADOR(ID),"
						   + "DPOY INTEGER REFERENCES JUGADOR(ID),"
						   + "SMOY INTEGER REFERENCES JUGADOR(ID),"
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
						   + "ANYO NOT NULL REFERENCES TEMPORADA(ANYO_INICIO),"
						   + "NOMBRE_USUARIO STRING NOT NULL REFERENCES USUARIO(NOMBRE),"
						   + "TID INTEGER,"
						   + "ANYOS_CON_REST INTEGER,"
						   + "SALARIO INTEGER,"
						   + "PUNTOS INTEGER,"
						   + "REBOTES INTEGER,"
						   + "ASISTENCIAS INTEGER,"
						   + "PARTIDOS_JUGADOS INTEGER,"
						   + "PRIMARY KEY (ID_J,ANYO, NOMBRE_USUARIO))");
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
			if(j.getTid() < -1) return false;
			
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO JUGADOR VALUES"
															+ "(?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1,j.getID());
			pst.setString(2, LigaManager.usuario.getNombre());
			pst.setString(3, j.getNombre());
			pst.setInt(4, j.getAnyoNac());
			pst.setInt(5, j.getPosicion().ordinal());
			pst.setInt(6, j.getOverall());
			pst.setInt(7, j.getRebote());
			pst.setInt(8, j.getTiroLibre());
			pst.setInt(9, j.getTiroCerca());
			pst.setInt(10, j.getTiroLejos());
			pst.setInt(11, j.getDefensa());
			pst.setInt(12, j.getAsistencia());
			
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
	
	public static void guardarTemporada() {
		try {
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO TEMPORADA VALUES "												+ "(?,?,?,?,?,?,?);");
			pst.setInt(1, LigaManager.anyo);
			pst.setString(2, LigaManager.usuario.getNombre());
			pst.setInt(3, LigaManager.campeon.getTid());
			pst.setString(4, LigaManager.mvp.getNombre());
			pst.setString(5, LigaManager.roy.getNombre());
			pst.setString(6, LigaManager.dpoy.getNombre());
			pst.setString(7, LigaManager.sextoHombre.getNombre());
			
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void guardarJuega(Jugador j) {
		try {
			if(j.getTid() < -1) return;
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO JUEGA VALUES "
															+ "(?,?,?,?,?,?,?,?,?,?)");
			System.out.println(j.getID());
			pst.setInt(1, j.getID());
			pst.setInt(2, LigaManager.anyo);
			pst.setString(3, LigaManager.usuario.getNombre());
			pst.setInt(4, j.getTid());
			pst.setInt(5, j.getAnyosContratoRestantes());
			pst.setInt(6, j.getSalario());
			pst.setInt(7, j.getPuntosTemporada());
			pst.setInt(8, j.getRebotesTemporada());
			pst.setInt(9, j.getAsistenciasTemporada());
			pst.setInt(10, j.getPartidosJugadosTemporada());
			
			pst.executeUpdate();
		} catch (SQLException e) {
			System.out.println(j.getNombre()+","+j.getID());
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Jugador> cargaJugadoresBD() {
		ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
		
		try {
			PreparedStatement pst = conexion.prepareStatement("SELECT * FROM JUGADOR WHERE NOMBRE_USUARIO=?");
			pst.setString(1, LigaManager.usuario.getNombre());
			ResultSet rs = pst.executeQuery();
			
			String nombre;
			int id, anyoNac, overall, rebote, tiroLibre, tiroCerca, tiroLejos, defensa, asistencia;
			int pos;
			
			
			PreparedStatement pst2 = conexion.prepareStatement("SELECT * FROM JUEGA WHERE NOMBRE_USUARIO=? "
															 + "AND ID_J=? ORDER BY ANYO DESC");
			while(rs.next()) {
				id = rs.getInt(1);
				nombre = rs.getString(3);
				anyoNac = rs.getInt(4);
				pos = rs.getInt(5);
				overall = rs.getInt(6);
				rebote = rs.getInt(7);
				tiroLibre = rs.getInt(8);
				tiroCerca = rs.getInt(9);
				tiroLejos = rs.getInt(10);
				defensa = rs.getInt(11);
				asistencia = rs.getInt(12);
				Jugador j = new Jugador(nombre, id, anyoNac, pos, overall, rebote, tiroLibre, 
						tiroCerca, tiroLejos, defensa, asistencia);
				
				try {
					pst2.setString(1, LigaManager.usuario.getNombre());
					pst2.setInt(2, id);
					ResultSet rs2 = pst2.executeQuery();
					boolean primero = true;
					while(rs2.next()) {
						int anyo = rs2.getInt(2);
						int tid = rs2.getInt(4);
						int puntos = rs2.getInt(7);
						int rebotes = rs2.getInt(8);
						int asistencias = rs2.getInt(9);
						int partidos = rs2.getInt(10);
						
						Estadistica e = new Estadistica(anyo,tid,puntos,rebotes,asistencias,partidos);
						j.guardaEstadistica(anyo, e);
						
						if(primero) {
							int anyosCont = rs2.getInt(5);
							int salario = rs2.getInt(6);
							System.out.println(tid+" de "+j.getNombre()+": "+tid);
							j.setTid(tid);
							j.salario = salario;
							j.anyosContratoRestantes = anyosCont;
							primero = false;
						} 
					}
				} catch(SQLException e2) {
					e2.printStackTrace();
				}
				jugadores.add(j);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return jugadores;
	}
	
	public static HashMap<Integer, Temporada> cargarTemporadasPasadas() {
		HashMap<Integer, Temporada> temps = new HashMap<>();
		
		try {
			PreparedStatement pst = conexion.prepareStatement("SELECT * FROM TEMPORADA WHERE NOMBRE_USUARIO=?");
			pst.setString(1, LigaManager.usuario.getNombre());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				int anyo = rs.getInt(1);
				int tidGana = rs.getInt(3);
				String mvp = rs.getString(4);
				String roy = rs.getString(5);
				String dpoy = rs.getString(6);
				String smoy = rs.getString(7);
				Temporada t = new Temporada();
				t.nombreCampeon = LigaManager.getNombreEquipoPorTid(tidGana);
				t.mvp = mvp;
				t.roy = roy;
				t.dpoy = dpoy;
				t.sextoHombre = smoy;
				
				temps.put(anyo, t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return temps;
	}
	
	public static void main(String[] args) {
		conectar();
		crearTablas();
	}
}
