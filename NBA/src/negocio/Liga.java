package negocio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import datos.ParseadorJSON;

public class Liga {

	protected Calendario calendario;
	protected Equipo[] equipos;
	protected ArrayList<Jugador> jugadores;
	protected ArrayList<Jugador> agentesLibres;
	
	protected HashMap<String, Clasificacion> clasificaciones;
	
	public Liga() {
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		cargarJugadores();
		cargarAgentesLibres();
		cargarEquipos();
		asignarJugadoresAEquipos();
		calendario = new Calendario(equipos);
		inicializarClasificaciones();
		
		for(ArrayList<Partido> dia: calendario.calendario.values()) {
			for(Partido p: dia) {
				System.out.println(p.local.nombre+" "+p.visitante.nombre);
				p.jugar();
				//new Scanner(System.in).nextLine();
			}
		}
		System.out.println("Temporada regular finalizada");
		ordenarClasificaciones();
		for(String d: clasificaciones.keySet()) {
			System.out.println("Clasificacion "+d);
			clasificaciones.get(d).imprimir();
			System.out.println();
		}
	}
	
	private void ordenarClasificaciones() {
		for(Clasificacion c: clasificaciones.values()) {
			c.ordenar();
		}
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
	
	private void cargarAgentesLibres() {
		//con la lista jugadores seleccionar los de tid = -1
		for(Jugador j: jugadores) {
			if(j.tid == -1) {
				agentesLibres.add(j);
			}
		}
	}
	
	private void cargarJugadores() {
		JSONObject all = ParseadorJSON.getObjetoPrimario("data/jugadores.json");
		JSONArray jugadoresJSON = all.getJSONArray("players");
		jugadores = ParseadorJSON.aArrayListJugador(jugadoresJSON);
	}
	
	private void cargarEquipos() {
		JSONObject all = ParseadorJSON.getObjetoPrimario("data/equipos.json");
		JSONArray equiposJSON = all.getJSONArray("teams");
		equipos = ParseadorJSON.aArrayEquipos(equiposJSON);
	}
	
	private void asignarJugadoresAEquipos() {
		for(Equipo e: equipos) {
			for(Jugador j: jugadores) {
				if(j.tid == e.tid) {
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
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Liga l = new Liga();
			}
		});
	}
}
