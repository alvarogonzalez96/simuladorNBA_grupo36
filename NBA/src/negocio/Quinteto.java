package negocio;

public class Quinteto {

	private Equipo equipo;
	private Jugador[] jugadores;
	
	public Quinteto(Equipo e) {
		this.equipo = e;
	}
	
	public void actualizar(int tiempo) {
		jugadores = new Jugador[5];
		
		for (int i = 0; i < jugadores.length; i++) {
			jugadores[i] = elegir(tiempo, i);
		}
	}
	
	public void actualizarTiempo(int t) {
		for(Jugador j: jugadores) {
			j.setTiempoJugado(j.getTiempoJugado()-t);
		}
	}
	
	private Jugador elegir(int tiempo, int pos) {
		Posicion p = elegirPosicion(pos);
		for (int i = 0; i < jugadores.length; i++) {
			if(jugadores[i].posicion == p && jugadores[i].getTiempoJugado() > 0) {
				return jugadores[i];
			}
		}
		System.err.println("Error, no quedan jugadores con tiempo disponible");
		return null;
	}
	
	private Posicion elegirPosicion(int pos) {
		if(pos == 0) {
			return Posicion.BASE;
		} else if(pos == 1) {
			return Posicion.ESCOLTA;
		} else if(pos == 2) {
			return Posicion.ALERO;
		} else if(pos == 3) {
			return Posicion.ALAPIVOT;
		} else if(pos == 4) {
			return Posicion.PIVOT;
		}
		System.err.println("Error, posicion incorrecta");
		return null;
	}
	
}
