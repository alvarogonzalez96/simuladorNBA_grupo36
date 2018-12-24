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
	public String[] nombresEquipos;
	public int[] victorias, derrotas;
	
	public String mvp, roy, dpoy, sextoHombre;
	
	public String nombreCampeon;
	public int victoriasUsuario, derrotasUsuario;
	
	public Temporada() {
		
	}
	
	public void guardaClasificacion(Equipo[] eq) {
		nombresEquipos = new String[30];
		victorias = new int[30];
		derrotas = new int[30];
		for(int i = 0; i < 30; i++) {
			nombresEquipos[i] = eq[i].getNombre();
			victorias[i] = eq[i].getVictorias();
			derrotas[i] = eq[i].getDerrotas();
		}
	}
	
	public void guardaCampeon(Equipo campeon) {
		this.nombreCampeon = campeon.getNombre();
	}
	
	public void guardaBalanceUsuario(int v, int d) {
		victoriasUsuario = v;
		derrotasUsuario = d;
	}
	
	public void setMVP(String j) {
		this.mvp = new String(j);
	}
	
	public void setROY(String j) {
		roy = new String(j);
	}
	
	public void setSextoHombre(String j) {
		sextoHombre = new String(j);
	}
	
	public void setDPOY(String j) {
		dpoy = new String(j);
	}
}
