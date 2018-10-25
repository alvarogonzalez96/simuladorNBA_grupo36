package negocio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONObject;

public class Equipo {
	
	private static OrdenadorJugadores ordenador;
	{
		ordenador = new OrdenadorJugadores();
	}

	protected String nombre;
	protected String abrev;
	protected ArrayList<Jugador> jugadores;
	protected int ataque;
	protected int defensa;
	protected int overall;
	int tid;
	
	/*public Equipo(String nombre, Jugador[] jugadores) {
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
	}*/
	
	public Equipo(int tid, ArrayList<Jugador> jugadores) {
		super();
		//this.nombre = nombre;
		//this.jugadores = jugadores;
		int at = 0;
		int def = 0;
		int ov = 0;
		this.tid = tid;
		this.jugadores = new ArrayList<>();
		
		for (Jugador j: jugadores) {
			if (j.tid == tid) {
				this.jugadores.add(j);
			}
		}
		
		ordenarJugadores();
		asignarRoles();
		
		this.ataque = (at/10);
		this.defensa = (def/10);
		this.overall = (ov/10);
	}
	
	public Equipo(JSONObject json) {
		this.tid = json.getInt("tid");
		this.nombre = json.getString("region")+" "+json.getString("name");
		this.abrev = json.getString("abbrev");
		this.jugadores = new ArrayList<>();
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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
	
	protected void asignarRoles() {
		ordenarJugadores();
		boolean titBase, titEsc, titAlero, titAlap, titPivot;
		titBase = titEsc = titAlero = titAlap = titPivot = false;
		Jugador estrella1 = asignarEstrellaAlMejor();
		switch(estrella1.posicion) {
		case BASE: titBase = true; break;
		case ESCOLTA: titEsc = true; break;
		case ALERO: titAlero = true; break;
		case ALAPIVOT: titAlap = true; break;
		default:
			titPivot = true;
		}
		Jugador estrella2 = asignarEstrellaAlSegundoMejor(estrella1);
		switch(estrella2.posicion) {
		case BASE: titBase = true; break;
		case ESCOLTA: titEsc = true; break;
		case ALERO: titAlero = true; break;
		case ALAPIVOT: titAlap = true; break;
		default:
			titPivot = true;
		}
		for(Jugador j: jugadores) {
			if(j.rol != Rol.ESTRELLA) {
				if(!titBase && j.posicion == Posicion.BASE) {
					j.rol = Rol.TITULAR;
					titBase = true;
				}  else if(!titEsc && j.posicion == Posicion.ESCOLTA) {
					j.rol = Rol.TITULAR;
					titEsc = true;
				} else if(!titAlero && j.posicion == Posicion.ALERO) {
					j.rol = Rol.TITULAR;
					titAlero = true;
				} else if(!titAlap && j.posicion == Posicion.ALAPIVOT) {
					j.rol = Rol.TITULAR;
					titAlap = true;
				} else if(!titPivot && j.posicion == Posicion.PIVOT) {
					j.rol = Rol.TITULAR;
					titPivot = true;
				} else {
					j.rol = Rol.SUPLENTE;
				}
			}
		}
	}
	
	private Jugador asignarEstrellaAlMejor() {
		Jugador mejor = new Jugador();
		for(Jugador j: jugadores) {
			if(j.rol != Rol.ESTRELLA && j.overall > mejor.overall) {
				mejor = j;
			}
		}
		mejor.rol = Rol.ESTRELLA;
		return mejor;
	}
	
	private Jugador asignarEstrellaAlSegundoMejor(Jugador mejor) {
		Jugador segmejor = new Jugador();
		for(Jugador j: jugadores) {
			if(j.rol != Rol.ESTRELLA && j.posicion != mejor.posicion && j.overall > segmejor.overall) {
				segmejor = j;
			}
		}
		segmejor.rol = Rol.ESTRELLA;
		return segmejor;
	}
	
	public void ordenarJugadores() {
		jugadores.sort(ordenador);
	}

	@Override
	public String toString() {
		return "Equipo [nombre=" + nombre + ", jugadores="  /*Arrays.toString(jugadores)*/ + ", ataque=" + ataque
				+ ", defensa=" + defensa + ", overall=" + overall + "]";
	}
}
