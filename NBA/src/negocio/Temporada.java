package negocio;

import java.util.*;

public class Temporada {

	/**
	 * Clase en la que se guardaran los datos necesarios
	 * sobre las temporadas pasadas:
	 * 	-Plantillas de los equipos
	 * 	-Estadisticas de cada jugador (esta y la anterior se guardaran en un array de equipos)
	 * 	-Campeon
	 * 	-Victorias-derrotas del equipo del usuario
	 * */
	
	/*
	 * En la lista de equipos se guardaran objetos copia
	 * de los equipos, y dentro de cada equipo una lista de jugadores,
	 * para que sus estadisticas de esa temporada permanezcan
	 * intactas.
	 * */
	public Equipo[] equipos;
	
	public Jugador mvp, roy, dpoy, sextoHombre;
	
	public Equipo campeon;
	public int victoriasUsuario, derrotasUsuario;
	
	public Temporada(Equipo[] eq, int v, int d, Equipo campeon) {
		equipos = getCopia(eq);
		victoriasUsuario = v;
		derrotasUsuario = d;
		this.campeon = campeon; //no hacemos copia porque solo nos interesa el nombre del equipo;
	}
	
	public void setMVP(Jugador j) {
		this.mvp = new Jugador(j);
	}
	
	public void setROY(Jugador j) {
		roy = new Jugador(j);
	}
	
	public void setSextoHombre(Jugador j) {
		sextoHombre = new Jugador(j);
	}
	
	public void setDPOY(Jugador j) {
		dpoy = new Jugador(j);
	}
	
	private Equipo[] getCopia(Equipo[] eq) {
		Equipo[] e = new Equipo[30];
		for(int i = 0; i < 30; i++) {
			e[i] = new Equipo(eq[i]);
		}
		return e;
	}
	
	
}
