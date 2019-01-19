package datos;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import negocio.*;

public class BD {

	/**
	 * Clase desde la que se manejará la base de datos,
	 * todas las consultas y conexiones se realizarán desde esta clase.
	 * */
	
	private static Logger logger;
	
	private static boolean existeBD;

	static final String DIRECTORIO = "data/database.db";
	
	static SimpleDateFormat sdf; 

	static {
		logger = Logger.getLogger("logger-BD");
		try {
			FileHandler f = new FileHandler("log/logBD.log", true);
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.ALL);
			f.setLevel(Level.ALL);
			logger.addHandler(f);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "No se ha podido cargar el logger.", e);
		}
		sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Class.forName("org.sqlite.JDBC");
			logger.log(Level.INFO, "Driver cargado correctamente");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al cargar el driver", e);
		}
	}

	static Connection conexion;
	static Statement st;

	/**
	 * Método que se conecta con la base de datos.
	 * @return true si la conexión se establece correctamente | false si hay algún problema
	 * */
	public static boolean conectar() {
		File f = new File(DIRECTORIO);
		existeBD = f.exists();
		try {
			conexion = DriverManager.getConnection("jdbc:sqlite:"+DIRECTORIO);
			conexion.setAutoCommit(false);
			st = conexion.createStatement();
			logger.log(Level.INFO, "Conexion a la BD exitosa");
			if(!existeBD || !comprobarBDEsCorrecta()) crearTablas();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al conectar con la BD", e);
			return false;
		}
	}
	
	public static void desconectar() {
		try {
			st.close();
			conexion.close();
			logger.log(Level.INFO, "Desconexion de la BD exitosa");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al desconectarse de la BD", e);
		}
	}
	
	public static void commit() {
		try {
			conexion.commit();
			logger.log(Level.INFO, "Commit realizado correctamente");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al realizar commit", e);
		}
	}
	
	public static void rollback() {
		try {
			conexion.rollback();
			logger.log(Level.INFO, "Rollback ejecutado correctamente");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al ejecutar rollback", e);
		}
	}
	
	private static boolean comprobarBDEsCorrecta() {
		try {
			PreparedStatement pst = conexion.prepareStatement("SELECT name FROM sqlite_master WHERE type=? AND name=?;");
			pst.setString(1, "table");
			String[] noms = new String[] {"USUARIO", "TEMPORADA", "JUGADOR", "JUEGA"};
			ResultSet rs;
			for(int i = 0; i < noms.length; i++) {
				pst.setString(2, noms[i]);
				rs = pst.executeQuery();
				if(!rs.next()) {
					rs.close();
					return false;
				}
				rs.close();
			}
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "Error al comprobar BD existente", e);
			return false;
		}
		
		return true;
	}

	// Metodos de registro / login

	/**
	 * Método que registra un usuario nuevo en la BD.
	 * @return int >= 0 si todo va bien (ese número será el id de usuario) |
	 * -1 si el nombre de usuario elegido ya existe |
	 * -2 si ha habido algún otro tipo de error
	 * */
	public static int registrar(String username, String pass, int teamID) {
		try {
			ResultSet rs = st.executeQuery("SELECT NOMBRE FROM USUARIO");
			while(rs.next()) {
				String n = rs.getString("NOMBRE");
				if(rs.getString("NOMBRE").equalsIgnoreCase(username)) {
					rs.close();
					return -1;
				}
			}
			rs.close();
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO USUARIO VALUES (?,?,?);");
			pst.setString(1, username);
			pst.setString(2, pass);
			pst.setInt(3, teamID);
			pst.executeUpdate();
			pst.close();
			
			commit();
		} catch(Exception e) {
			e.printStackTrace();
			return -2;
		}
		return 0;
	}

	/**
	 * Método para verificar la identidad del usuario.
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
			int ret = -1;
			if(rs.next()) {
				ret = rs.getInt(3);
				rs.close();
				pst.close();
				return ret;
			}
			rs.close();
			pst.close();
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
		commit();
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
			logger.log(Level.SEVERE, "Error al crear la tabla USUARIO", e);
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
						   + "ASISTENCIA INTEGER,"
						   + "DRAFT INTEGER,"
						   + "HGT INTEGER,"
						   + "STRE INTEGER,"
						   + "SPD INTEGER,"
						   + "JMP INTEGER,"
						   + "ENDU INTEGER,"
						   + "INS INTEGER,"
						   + "DNK INTEGER,"
						   + "OIQ INTEGER,"
						   + "DRB INTEGER)");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al crear la tabla JUGADOR", e);
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
			logger.log(Level.SEVERE, "Error al crear la tabla TEMPORADA", e);
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
			logger.log(Level.SEVERE, "Error al crear la tabla JUEGA", e);
			return false;
		}
		return true;
	}
	
	public static int getPrimerIdLibreJugador() {
		try {
			ResultSet rs = st.executeQuery("SELECT MAX(ID) FROM JUGADOR");
			int u = 0;
			while(rs.next()) {
				u = rs.getInt(1)+1;
				rs.close();
				return u;
			}
			rs.close();
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al averiguar el primer ID libre de JUGADOR", e);
		}
		return -1;
	}
	
	public static boolean guardarJugador(Jugador j) {
		try {
			if(j.getTid() < -1) return false;
			
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO JUGADOR VALUES"
															+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
			pst.setInt(13, j.anyoDraft);
			pst.setInt(14, j.getHgt());
			pst.setInt(15, j.getStre());
			pst.setInt(16, j.getSpd());
			pst.setInt(17, j.getJmp());
			pst.setInt(18, j.getEndu());
			pst.setInt(19, j.getIns());
			pst.setInt(20, j.getDnk());
			pst.setInt(21, j.getOiq());
			pst.setInt(22, j.getDrb());
			
			
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al guardar jugador", e);
			return false;
		}
		return true;
	}
	
	public static void borrarJugador(Jugador j) {
		try {
			if(j.getTid() > -2) return;
			PreparedStatement pst = conexion.prepareStatement("DELETE FROM JUGADOR WHERE ID=?");
			pst.setInt(1, j.getID());
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al borrar jugador", e);
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
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al guardar temporada", e);
		}
	}
	
	public static void guardarJuega(Jugador j) {
		try {
			if(j.getTid() < -1) return;
			PreparedStatement pst = conexion.prepareStatement("INSERT INTO JUEGA VALUES "
															+ "(?,?,?,?,?,?,?,?,?,?)");
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
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al guardar juega", e);
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
			int pos, draft;
			int hgt, stre, spd, jmp, endu, ins, dnk, oiq, drb;
			
			PreparedStatement pst2 = conexion.prepareStatement("SELECT * FROM JUEGA WHERE NOMBRE_USUARIO=? "
															 + "AND ID_J=? ORDER BY ANYO DESC");
			while(rs.next()) {
				id = rs.getInt(1);
				nombre = rs.getString(3);
				anyoNac = rs.getInt(4);
				pos = rs.getInt(5);
				overall = rs.getInt(6); //if(overall < 40) System.err.println(nombre); 
				rebote = rs.getInt(7);
				tiroLibre = rs.getInt(8);
				tiroCerca = rs.getInt(9);
				tiroLejos = rs.getInt(10);
				defensa = rs.getInt(11);
				asistencia = rs.getInt(12);
				draft = rs.getInt(13);
				hgt = rs.getInt(14);
				stre = rs.getInt(15);
				spd = rs.getInt(16);
				jmp = rs.getInt(17);
				endu = rs.getInt(18);
				ins = rs.getInt(19);
				dnk = rs.getInt(20);
				oiq = rs.getInt(21);
				drb = rs.getInt(22);
				
				Jugador j = new Jugador(nombre, id, anyoNac, pos, overall, rebote, tiroLibre, 
						tiroCerca, tiroLejos, defensa, asistencia, draft);
				j.cargarAtributos(hgt, stre, spd, jmp,endu,ins,dnk,oiq,drb);
				
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
							j.setTid(tid);
							j.salario = salario;
							j.anyosContratoRestantes = anyosCont;
							primero = false;
						} 
					}
					rs2.close();
				} catch(SQLException e2) {
					e2.printStackTrace();
					logger.log(Level.SEVERE, "Error al cargar JUEGA de jugador con ID="+j.getID(), e2);
				}
				jugadores.add(j);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al cargar jugadores", e);
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
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al cargar temporadas pasadas", e);
		}
		
		return temps;
	}

	public static void actualizarOverallJugadores() {
		try {
			PreparedStatement pst = conexion.prepareStatement("UPDATE JUGADOR SET OVERALL=?,"
					+ "HGT=?,STRE=?,SPD=?,JMP=?,ENDU=?,INS=?,DNK=?,OIQ=?,DRB=? WHERE ID=?;");
			for(Jugador j: LigaManager.jugadores) {
				pst.setInt(1, j.getOverall());
				pst.setInt(2, j.getHgt());
				pst.setInt(3, j.getStre());
				pst.setInt(4, j.getSpd());
				pst.setInt(5, j.getJmp());
				pst.setInt(6, j.getEndu());
				pst.setInt(7, j.getIns());
				pst.setInt(8, j.getDnk());
				pst.setInt(9, j.getOiq());
				pst.setInt(10, j.getDrb());
				pst.setInt(11, j.getID());
				pst.executeUpdate();
			}
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error al actualizar overall de jugadores", e);
		}
	}
	
	public static void main(String[] args) {
		conectar();
		crearTablas();
	}
}
