package NBA;

import java.util.Arrays;

public class Equipo {

	protected String nombre;
	protected Jugador[] jugadores;
	protected int ataque;
	protected int defensa;
	protected int overall;
	
	public Equipo(String nombre, Jugador[] jugadores) {
		super();
		this.nombre = nombre;
		this.jugadores = jugadores;
		int at = 0;
		int def = 0;
		int ov = 0;
		for (int i = 0; i < jugadores.length; i++) {
			at = at + jugadores[i].getAtaque();
			def = def + jugadores[i].getDefensa();
			ov = ov + jugadores[i].getOverall();
		}
		this.ataque = (at/10);
		this.defensa = (def/10);
		this.overall = (ov/10);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Jugador[] getJugadores() {
		return jugadores;
	}

	public void setJugadores(Jugador[] jugadores) {
		this.jugadores = jugadores;
	}

	public int getAtaque() {
		return ataque;
	}

	public int getDefensa() {
		return defensa;
	}

	public int getOverall() {
		return overall;
	}

	@Override
	public String toString() {
		return "Equipo [nombre=" + nombre + ", jugadores=" + Arrays.toString(jugadores) + ", ataque=" + ataque
				+ ", defensa=" + defensa + ", overall=" + overall + "]";
	}
	
	
}
