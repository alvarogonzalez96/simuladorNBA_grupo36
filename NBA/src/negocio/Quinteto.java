package negocio;

public class Quinteto {

	private Equipo equipo;
	protected Jugador[] jugadores;
	
	public Quinteto(Equipo e) {
		super();
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
		Jugador j = new Jugador();
		Posicion p = elegirPosicion(pos);
		for (int i = 0; i < equipo.jugadores.size(); i++) {
			if(equipo.jugadores.get(i).getPosicion() == p && equipo.jugadores.get(i).getTiempoJugado() > 0) {
				return equipo.jugadores.get(i);
			}
		}
		System.err.println("Error, no quedan jugadores con tiempo disponible");
		cuentaPosiciones();
		System.err.println(p);
		return null;
	}
	
	void cuentaPosiciones() {
		int[] pos = new int[5];
		for(Jugador j: equipo.jugadores) {
			switch(j.posicion) {
			case BASE:
				pos[0]++;
				break;
			case ESCOLTA:
				pos[1]++;
				break;
			case ALERO:
				pos[2]++;
				break;
			case ALAPIVOT:
				pos[3]++;
				break;
			case PIVOT:
				pos[4]++;
				break;
			}
		}
		System.out.println("BASES: "+pos[0]);
		System.out.println("ESCOLTAS: "+pos[1]);
		System.out.println("ALEROS: "+pos[2]);
		System.out.println("ALAPIVOTS: "+pos[3]);
		System.out.println("PIVOTS: "+pos[4]);
	}
	
	protected static Posicion elegirPosicion(int pos) {
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