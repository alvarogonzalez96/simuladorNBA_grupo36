package negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	protected static HashMap<String, Clasificacion> clasificaciones;
	
	public Usuario usuario;
	
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
		this.usuario = new Usuario();
		usuario.equipo = equipos[9];
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
					//p.jugar();
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
		//Actualizar tablas de la pestanya clasificacion
		//...
	}
	
	public static void main(String[] args) {
		Liga l = new Liga(true);
		System.out.println();
		
		//Eleccion de premios al final de temporada
		System.out.println();
		System.out.println("-----------------------------------------");
		System.out.println("PREMIOS DE LA TEMPORADA REGULAR");
		System.out.println("MVP: " + elegirMVP(l.equipos).getNombre());
		
		System.out.println("-----------------------------------------");
		for (Equipo e : l.equipos) {
			System.out.println("JUGADORES DE "+e.nombre.toUpperCase()+":");
			for (Jugador j : e.jugadores) {
				if(!j.rol.equals(Rol.NOJUEGA)) {
					System.out.println(j.getNombre() + " " + (double)Math.round((j.getPuntosPartido()/82)*100)/100 + " ppp" + " " + (double)Math.round((j.getAsistenciasPartido()/82)*100)/100 + " app" + " " + (double)Math.round((j.getRebotesPartido()/82)*100)/100 + " rpp ; val: "+j.valoracion);
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
		
		//PlayOffs
		System.out.println();
		System.out.println("-----------------------------------------");
		System.out.println("PLAYOFFS");
		playOffs(clasificaciones);
	}
	
	public static void playOffs(HashMap<String, Clasificacion> clasificaciones) {
		Equipo[] oeste = new Equipo[8];
		Equipo[] este = new Equipo[8];
		
		Clasificacion cOeste = clasificaciones.get("OESTE");
		Clasificacion cEste = clasificaciones.get("ESTE");
		
		for (int i = 0; i < oeste.length; i++) {
			oeste[i] = cOeste.get(i);
			este[i] = cEste.get(i);
		}

		//Cuartos de final de conferencia
		Equipo[] semisOeste = new Equipo[4];
		Equipo[] semisEste = new Equipo[4];
		System.out.println();
		System.out.println("*CUARTOS DE FINAL DE CONFERENCIA*");
		int j = 7;
		for (int i = 0; i < semisOeste.length; i++) {
			semisOeste[i] = ganadorSerie(oeste[i], oeste[j]);
			System.out.println("Gana la serie: " + semisOeste[i].getNombre());
			System.out.println();
			semisEste[i] = ganadorSerie(este[i], este[j]);
			System.out.println("Gana la serie: " + semisEste[i].getNombre());	
			System.out.println();
			j--;
		}
		
		//Semifinales de conferencia
		oeste = new Equipo[2];
		este = new Equipo[2];
		j = 3;
		System.out.println();
		System.out.println("*SEMIFINALES DE CONFERENCIA*");
		for (int i = 0; i < oeste.length; i++) {
			oeste[i] = ganadorSerie(semisOeste[i], semisOeste[j]);
			System.out.println("Gana la serie: " + oeste[i].getNombre());
			System.out.println();
			este[i] = ganadorSerie(semisEste[i], semisEste[j]);
			System.out.println("Gana la serie: " + semisEste[i].getNombre());	
			System.out.println();
			j--;
		}
		
		//Final de conferencia
		Equipo[] equiposFinal = new Equipo[2];
		System.out.println();
		System.out.println("*FINALES DE CONFERENCIA*");
		equiposFinal[0] = ganadorSerie(oeste[0], oeste[1]);
		System.out.println("Gana la serie: " + equiposFinal[0].getNombre());
		System.out.println();
		equiposFinal[1] = ganadorSerie(este[0], este[1]);
		System.out.println("Gana la serie: " + equiposFinal[1].getNombre());
		System.out.println();
		
		//Final
		System.out.println("*FINAL DE LA NBA*");
		Equipo ganadorNBA = ganadorSerie(equiposFinal[0], equiposFinal[1]);
		System.out.println("Gana el anillo: " + ganadorNBA.getNombre());
		
	}
	
	public static Equipo ganadorSerie(Equipo e1, Equipo e2) {
		int victorias1 = 0;
		int victorias2 = 0;
		
		while(victorias1 < 4 && victorias2 < 4) {
			Partido p = new Partido(e1, e2);
			//p.jugar();
			if(p.puntosLocal > p.puntosVisitante) {
				victorias1 ++;
			} else {
				victorias2 ++;
			}
		}
		
		if(victorias1 > victorias2) {
			return e1;
		} else {
			return e2;
		}
		
	}
	
	public Calendario getCalendario() {
		return calendario;
	}
	
	public HashMap<String, Clasificacion> getClasificaciones(){
		return clasificaciones;
	}
	
	public Equipo[] getEquipos() {
		return equipos;
	}
	
	public ArrayList<Equipo> getArrayListEquipos(){
		ArrayList<Equipo> eq = new ArrayList<>();
		for(Equipo e: equipos) {
			eq.add(e);
		}
		return eq;
	}
	
	public static Jugador elegirMVP(Equipo[] e) {
		Jugador mvp = new Jugador();
		for (Equipo equipo : e) {
			for (Jugador j : equipo.jugadores) {
				j.valoracion = (j.getPuntosPartido() + j.getAsistenciasPartido() + j.getRebotesPartido());
				if(j.getRol().equals(Rol.ESTRELLA) || j.getRol().equals(Rol.TITULAR)) {
					if(j.valoracion > mvp.valoracion && equipo.getVictorias() > 41) {
						mvp = j;
					}
				}
			}
		}
		return mvp;
	}
}
