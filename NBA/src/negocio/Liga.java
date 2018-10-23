package negocio;

import javax.swing.SwingUtilities;

public class Liga {

	protected Equipo[] equipos;
	protected Jugador[] jugadores;
	protected Partido partido;
	
	public Liga() {
		jugadores = crearJugadores();
		equipos = crearEquipos(jugadores);
	
		//System.out.println(equipos[0]);
		//System.out.println(equipos[1]);
		
		
		partido = new Partido(equipos[0], equipos[1]);
		
	}
	
	public Equipo[] crearEquipos(Jugador[] jugadores) {
		Equipo[] e = new Equipo[2];
		Jugador[] jugadoresLocal = new Jugador[10];
		Jugador[] jugadoresVisitante = new Jugador[10];
		
		
		for (int i = 0; i < jugadoresLocal.length; i++) {
			jugadoresLocal[i] = jugadores[i];
		}
		
		for (int i = 0; i < jugadoresVisitante.length; i++) {
			jugadoresVisitante[i] = jugadores[i+10];
		}
		
		for (int i = 0; i < jugadoresVisitante.length; i++) {
			System.out.println(jugadoresLocal[i]);
		}
		
		System.out.println();
		
		for (int i = 0; i < jugadoresVisitante.length; i++) {
			System.out.println(jugadoresVisitante[i]);
		}
		
		e[0] = new Equipo("Golden State Warriors", jugadoresLocal);
		e[1] = new Equipo("Los Angeles Lakers", jugadoresVisitante);
		
		return e;
	}
	
	public Jugador[] crearJugadores() {
		Jugador[] j = new Jugador[20] ;
		//Los jugadores estarán ordenados de tal manera que apareceran por orden, tanto de posición como de rol
		//GSW
		j[0] = new Jugador("Stephen Curry", Posicion.BASE, Rol.ESTRELLA, 90, 100, 85, 60, 75, 60, 100);
		j[1] = new Jugador("Klay Thompson", Posicion.ESCOLTA, Rol.TITULAR, 70, 100, 65, 65, 70, 65, 100);
		j[2] = new Jugador("Kevin Durant", Posicion.ALERO, Rol.ESTRELLA, 90, 90, 65, 80, 80, 95, 100);
		j[3] = new Jugador("Dreymond Green", Posicion.ALAPIVOT, Rol.TITULAR, 85, 70, 85, 80, 75, 70, 100);
		j[4] = new Jugador("Demarcus Cousins", Posicion.PIVOT, Rol.TITULAR, 95, 60, 75, 90, 75, 95, 100);
		j[5] = new Jugador("Shaun Livingstone", Posicion.BASE, Rol.SUPLENTE, 90, 30, 80, 70, 60, 75, 100);
		j[6] = new Jugador("Quinn Cook", Posicion.ESCOLTA, Rol.SUPLENTE, 65, 80, 65, 55, 60, 60, 100);
		j[7] = new Jugador("Andre Iguodala", Posicion.ALERO, Rol.SUPLENTE, 75, 70, 70, 75, 75, 70, 100);
		j[8] = new Jugador("Jonas Jerebko", Posicion.ALAPIVOT, Rol.SUPLENTE, 65, 75, 50, 70, 65, 75, 100);
		j[9] = new Jugador("Jordan Bell", Posicion.PIVOT, Rol.SUPLENTE, 80, 20, 50, 85, 85, 75, 100);
		//LAL
		j[10] = new Jugador("Lonzo Ball", Posicion.BASE, Rol.TITULAR, 70, 65, 85, 75, 75, 75, 100);
		j[11] = new Jugador("Josh Hart", Posicion.ESCOLTA, Rol.TITULAR, 65, 75, 55, 65, 80, 65, 100);
		j[12] = new Jugador("Brandon Ingram", Posicion.ALERO, Rol.ESTRELLA, 85, 70, 65, 75, 75, 85, 100);
		j[13] = new Jugador("Lebron James", Posicion.ALAPIVOT, Rol.ESTRELLA, 100, 85, 90, 90, 90, 85, 100);
		j[14] = new Jugador("Javale McGee", Posicion.PIVOT, Rol.TITULAR, 85, 20, 40, 90, 90, 90, 100);
		j[15] = new Jugador("Rajon Rondo", Posicion.BASE, Rol.SUPLENTE, 80, 40, 95, 75, 75, 70, 100);
		j[16] = new Jugador("Kentavius Caldwell-Pope", Posicion.ESCOLTA, Rol.SUPLENTE, 60, 80, 55, 65, 75, 65, 100);
		j[17] = new Jugador("Michael Beasley", Posicion.ALERO, Rol.SUPLENTE, 70, 65, 60, 75, 75,  75, 100);
		j[18] = new Jugador("Kyle Kuzma", Posicion.ALAPIVOT, Rol.SUPLENTE, 70, 75, 50, 75, 75, 75, 100);
		j[19] = new Jugador("Ivica Zubac", Posicion.PIVOT, Rol.SUPLENTE, 70, 30, 30, 75, 65, 85, 100);
		
		return j;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Liga();
			}
		});
	}
}
