package negocio;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import datos.ParseadorJSON;

public class Liga {

	protected Calendario calendario;
	protected Equipo[] equipos;
	protected ArrayList<Jugador> jugadores;
	protected ArrayList<Jugador> agentesLibres;
	
	protected Temporada temporada;
	
	protected HashMap<String, Clasificacion> clasificaciones;
	
	public Liga() {
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		cargarJugadores();
		cargarAgentesLibres();
		cargarEquipos();
		asignarJugadoresAEquipos();
		calendario = new Calendario(equipos);
		temporada = new Temporada(2018, equipos, jugadores, calendario);
		boolean continua = temporada.simularDia();
		while(continua) {
			continua = temporada.simularDia();
		}
		
		/*System.out.println("Temporada regular finalizada");
		ordenarClasificaciones();
		for(String d: clasificaciones.keySet()) {
			System.out.println("Clasificacion "+d);
			clasificaciones.get(d).imprimir();
			System.out.println();
		}*/
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
//				for (Equipo e : l.equipos) {
//					for (Jugador j : e.jugadores) {
//						if(!j.rol.equals(Rol.NOJUEGA)) {
//							System.out.println(j.nombre + " " + (double)Math.round((j.puntosPartido/82)*100)/100 + " ppp" + " " + (double)Math.round((j.asistenciasPartido/82)*100)/100 + " app" + " " + (double)Math.round((j.rebotesPartido/82)*100)/100 + " rpp");
//						}
//					}
//					System.out.println();
//				}
			}
		});
	}
}
