package negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import datos.ParseadorJSON;

public class LigaManager {

	protected static int fase; 
	/**
	 * 	fase 0: temporada regular
		fase 1: playoffs
		fase 2: draft
		fase 3: renovaciones
		fase 4: agencia libre
		volver a empezar
	 * */
	
	protected static Calendario calendario;
	protected static Equipo[] equipos;
	protected static ArrayList<Jugador> jugadores;
	protected static ArrayList<Jugador> agentesLibres;
	protected static HashMap<String, Clasificacion> clasificaciones;
	
	protected static boolean recienCreada;
	protected static int anyo;
	
	public static void inicializar(boolean desdeJSON) {
		recienCreada = desdeJSON;
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		cargarJugadores();
		cargarAgentesLibres();
		cargarEquipos();
		asignarJugadoresAEquipos();
		inicializarClasificaciones();
		//cargar fase
		Date diaActual;
		if(desdeJSON) {
			diaActual = Calendario.PRIMER_DIA;
			calendario = new Calendario(equipos, diaActual); // el calendario es el mismo para todas las temporadas
		} else {
			//de la BD
			diaActual = null; //cargar el dia actual en la BD
		}
	}
	
	/** 
	 *  @return true si ha terminado la temporada (incluidos los playoffs), 
	 *  		false si no ha terminado la temporada
	 * */
	private static boolean simularDia() {
		System.out.println("Simulando "+calendario.getDiaActual());
		if(!calendario.getDiaActual().after(Calendario.ULTIMO_DIA_TEMP_REGULAR)) { //fase = 0
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
				//crearPlayoffs();
				playOffs(); //fase = 1
			}
			return false;
		} else {
			// playoffs / verano
			if(fase == 2) {
				//draft
			} else if(fase == 3) {
				//renovaciones
				renovaciones();
			} else {
				//agencia libre
			}
			
			return true;
		}
	}  
	
	public static void playOffs() {
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
			p.jugar();
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
	
	private static void cargarJugadores() {
		if(recienCreada) { // cargar los jugadores por defecto desde el JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/jugadores.json");
			JSONArray jugadoresJSON = all.getJSONArray("players");
			jugadores = ParseadorJSON.aArrayListJugador(jugadoresJSON);
		} else {
			// cargar los jugadores y sus atributos desde la BD
		}
	}
	
	private static void cargarEquipos() {
		if(recienCreada) { //cargar los equipos por defecto desde JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/equipos.json");
			JSONArray equiposJSON = all.getJSONArray("teams");
			equipos = ParseadorJSON.aArrayEquipos(equiposJSON);
		} else {
			// cargar los equipos y sus atributos desde la BD
		}
	}
	
	private static void asignarJugadoresAEquipos() {
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
	
	public static Equipo[] crearEquipos(ArrayList<Jugador> jugadores) {
		equipos = new Equipo[30];
		for(int i = 0; i < 30; i++) {
			equipos[i] = new Equipo(i, jugadores);
		}
		return equipos;
	}
	

	private static void cargarAgentesLibres() {
		//con la lista jugadores seleccionar los de tid = -1
		for(Jugador j: jugadores) {
			if(j.getTid() == -1) {
				agentesLibres.add(j);
			}
		}
	}
	
	private static void inicializarClasificaciones() {
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
	
	private static void ordenarClasificaciones() {
		for(Clasificacion c: clasificaciones.values()) {
			c.ordenar();
		}
	}
	
	private static void renovaciones() {
		for(Equipo e: equipos) {
			for(Jugador j: e.jugadores) {
				
			}
		}
	}
	
	private static void pasaAnyo() {
		for(Equipo e: equipos) {
			for(Jugador j: e.jugadores) {
				j.anyosContratoRestantes--;
			}
		}
	}
	
}
