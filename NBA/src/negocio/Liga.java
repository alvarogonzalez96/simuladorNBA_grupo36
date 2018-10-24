package negocio;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import datos.ParseadorJSON;



public class Liga {

	protected Equipo[] equipos;
	protected ArrayList<Jugador> jugadores;
	protected ArrayList<Jugador> agentesLibres;
	//protected Partido partido;
	
	public Liga() {
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		cargarJugadores();
		equipos = crearEquipos(jugadores);
		cargarAgentesLibres();
		
		//partido = new Partido(equipos[0], equipos[1]);
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
		JSONObject all = ParseadorJSON.getObjetoPrimario("jugadores.json");
		JSONArray jugadoresJSON = all.getJSONArray("players");
		jugadores = ParseadorJSON.aArrayListJugador(jugadoresJSON);
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
				l.cargarJugadores();
				for(Equipo e: l.equipos) {
					System.out.println("-----"+e.tid);
					for(Jugador j: e.jugadores) {
						System.out.println(j.nombre + ", juega de " + j.posicion + ", nació en el anyo " + j.anyoNac + " y tiene una habilidad tirando triples de " + j.tiroLejos);
					}
					System.out.println();
				}
			}
		});
	}
}
