package negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import datos.ParseadorJSON;

public class Liga {
	
	/**
	 * Esta clase se usa para simular la temporada actual.
	 * Cuando una temporada acabe, se guardar�n en la BD los datos de esta (premios, stats de
	 * jugadores, y equipo en el que acab� la temporada cada jugador).
	 * 
	 **/

	protected Calendario calendario;
	protected Equipo[] equipos;
	protected ArrayList<Jugador> jugadores;
	protected ArrayList<Jugador> agentesLibres;
	protected HashMap<String, Clasificacion> clasificaciones;
	
	protected boolean recienCreada;
	protected int anyo;
	
	public Liga(boolean recienCreada) {
		this.recienCreada = recienCreada;
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		cargarJugadores();
		cargarAgentesLibres();
		cargarEquipos();
		asignarJugadoresAEquipos();
		inicializarClasificaciones();
		Date diaActual;
		if(recienCreada) {
			diaActual = Calendario.PRIMER_DIA;
		} else {
			diaActual = null; //cargar el dia actual en la BD
		}
		calendario = new Calendario(equipos, diaActual); // el calendario es el mismo para todas las temporadas
		boolean m = simularDia();
		while(!m) {
			m = simularDia();
		}
	}
	
	/**
	 *  @return true si ha terminado la temporada (incluidos los playoffs), 
	 *  		false si no ha terminado la temporada
	 * */
	private boolean simularDia() {
		System.out.println("Simulando "+calendario.getDiaActual());
		if(!calendario.getDiaActual().after(Calendario.ULTIMO_DIA_TEMP_REGULAR)) {
			// simular dia de temporada regular
			if(calendario.calendario.keySet().contains(calendario.getDiaActual())) {
				for(Partido p: calendario.calendario.get(calendario.getDiaActual())) {
					p.jugar();
				}
				ordenarClasificaciones();
			}
			calendario.avanzarDia();
			if(calendario.getDiaActual().equals(Calendario.ULTIMO_DIA_TEMP_REGULAR)) {
				System.out.println("Fin de la temporada regular");
				crearPlayoffs();
			}
			return false;
		} else {
			// playoffs / verano
			return true;
		}
	}
	
	private void crearPlayoffs() {
		
	}
	
	private void cargarAgentesLibres() {
		//con la lista jugadores seleccionar los de tid = -1
		for(Jugador j: jugadores) {
			if(j.getTid() == -1) {
				agentesLibres.add(j);
			}
		}
	}
	
	private void cargarJugadores() {
		if(recienCreada) { // cargar los jugadores por defecto desde el JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/jugadores.json");
			JSONArray jugadoresJSON = all.getJSONArray("players");
			jugadores = ParseadorJSON.aArrayListJugador(jugadoresJSON);
		} else {
			// cargar los jugadores y sus atributos desde la BD
		}
	}
	
	private void cargarEquipos() {
		if(recienCreada) { //cargar los equipos por defecto desde JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/equipos.json");
			JSONArray equiposJSON = all.getJSONArray("teams");
			equipos = ParseadorJSON.aArrayEquipos(equiposJSON);
		} else {
			// cargar los equipos y sus atributos desde la BD
		}
	}
	
	private void asignarJugadoresAEquipos() {
		for(Equipo e: equipos) {
			for(Jugador j: jugadores) {
				if(j.getTid() == e.getTid()) {
					e.jugadores.add(j);
				}
			}
			e.ordenarJugadores();
			e.asignarRoles();
		}
	}
	
	public Equipo[] crearEquipos(ArrayList<Jugador> jugadores) {
		equipos = new Equipo[30];
		for(int i = 0; i < 30; i++) {
			equipos[i] = new Equipo(i, jugadores);
		}
		return equipos;
	}
	
	private void inicializarClasificaciones() {
		clasificaciones = new HashMap<>();
		ArrayList<Equipo> este, oeste, atl, cent, sure, pac, suro, noro;
		este = new ArrayList<>();
		oeste = new ArrayList<>();
		atl = new ArrayList<>();
		cent = new ArrayList<>();
		sure = new ArrayList<>();
		pac = new ArrayList<>();
		suro = new ArrayList<>();
		noro = new ArrayList<>();
		
		for(Equipo e: equipos) {
			if(e.conferencia == Conferencia.ESTE) {
				este.add(e);
			} else {
				oeste.add(e);
			}
			switch(e.division) {
			case ATLANTICO: atl.add(e); break;
			case CENTRAL: cent.add(e); break;
			case SURESTE: sure.add(e); break;
			case PACIFICO: pac.add(e); break;
			case SUROESTE: suro.add(e); break;
			case NOROESTE: noro.add(e); break;
			}
		}
		clasificaciones.put("GENERAL", new Clasificacion(equipos));
		clasificaciones.put("ESTE", new Clasificacion(este));
		clasificaciones.put("OESTE", new Clasificacion(oeste));
		clasificaciones.put("ATLANTICO", new Clasificacion(atl));
		clasificaciones.put("CENTRAL", new Clasificacion(cent));
		clasificaciones.put("SURESTE", new Clasificacion(sure));
		clasificaciones.put("PACIFICO", new Clasificacion(pac));
		clasificaciones.put("NOROESTE", new Clasificacion(noro));
		clasificaciones.put("SUROESTE", new Clasificacion(suro));
	}
	
	private void ordenarClasificaciones() {
		for(Clasificacion c: clasificaciones.values()) {
			c.ordenar();
		}
	}
	
	public static void main(String[] args) {
		Liga l = new Liga(true);
		System.out.println();
		System.out.println("-----------------------------------------");
		for (Equipo e : l.equipos) {
			System.out.println("JUGADORES DE "+e.nombre.toUpperCase()+":");
			for (Jugador j : e.jugadores) {
				if(!j.rol.equals(Rol.NOJUEGA)) {
					System.out.println(j.getNombre() + " " + (double)Math.round((j.getPuntosPartido()/82)*100)/100 + " ppp" + " " + (double)Math.round((j.getAsistenciasPartido()/82)*100)/100 + " app" + " " + (double)Math.round((j.getRebotesPartido()/82)*100)/100 + " rpp");
				}
			}
			System.out.println();
		}
		
		System.out.println("-----------------------------------------");
		for(String tipoClasif: l.clasificaciones.keySet()) {
			System.out.println();
			System.out.println("CLASIFICACION "+tipoClasif);
			l.clasificaciones.get(tipoClasif).imprimir();
		}
		
		//Eleccion de premios al final de temporada
		System.out.println();
		System.out.println("-----------------------------------------");
		System.out.println("PREMIOS DE LA TEMPORADA REGULAR");
		System.out.println("MVP: " + elegirMVP(l.equipos).getNombre());
		
	}
	
	public static Jugador elegirMVP(Equipo[] e) {
		Jugador mvp = new Jugador();
		double puntos = 0;
		for (Equipo equipo : e) {
			for (Jugador j : equipo.jugadores) {
				if(j.getRol().equals(Rol.ESTRELLA) || j.getRol().equals(Rol.TITULAR)) {
					if((j.getPuntosPartido() + j.getAsistenciasPartido() + j.getRebotesPartido()) > puntos && equipo.getVictorias() > 41) {
						mvp = j;
						puntos = (j.getPuntosPartido() + j.getAsistenciasPartido() + j.getRebotesPartido());
					}
				}
			}
		}
		return mvp;
	}
}
