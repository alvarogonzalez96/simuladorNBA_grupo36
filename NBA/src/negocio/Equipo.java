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
		int basesTit, escTit, alerosTit, alapsTit, pivotsTit;
		basesTit = escTit = alerosTit = alapsTit = pivotsTit = 0;
		Jugador estrella1 = asignarEstrellaAlMejor();
		switch(estrella1.posicion) {
		case BASE: titBase = true; basesTit++; break;
		case ESCOLTA: titEsc = true; escTit++; break;
		case ALERO: titAlero = true; alerosTit++; break;
		case ALAPIVOT: titAlap = true; alapsTit++; break;
		default:
			titPivot = true;
			pivotsTit++;
		}
		Jugador estrella2 = asignarEstrellaAlSegundoMejor(estrella1);
		switch(estrella2.posicion) {
		case BASE: titBase = true; basesTit++; break;
		case ESCOLTA: titEsc = true; escTit++; break;
		case ALERO: titAlero = true; alerosTit++; break;
		case ALAPIVOT: titAlap = true; alapsTit++; break;
		default:
			titPivot = true;
			pivotsTit++;
		}
		for(Jugador j: jugadores) {
			if(j.rol != Rol.ESTRELLA) {
				if(!titBase && j.posicion == Posicion.BASE) {
					j.rol = Rol.TITULAR;
					titBase = true;
					basesTit++;
				}  else if(!titEsc && j.posicion == Posicion.ESCOLTA) {
					j.rol = Rol.TITULAR;
					titEsc = true;
					escTit++;
				} else if(!titAlero && j.posicion == Posicion.ALERO) {
					j.rol = Rol.TITULAR;
					titAlero = true;
					alerosTit++;
				} else if(!titAlap && j.posicion == Posicion.ALAPIVOT) {
					j.rol = Rol.TITULAR;
					titAlap = true;
					alapsTit++;
				} else if(!titPivot && j.posicion == Posicion.PIVOT) {
					j.rol = Rol.TITULAR;
					titPivot = true;
					pivotsTit++;
				} else {
					if(j.posicion == Posicion.BASE) {
						if(basesTit < 2) {
							j.rol = Rol.SUPLENTE;
							basesTit++;
						} else {
							j.rol = Rol.NOJUEGA;
						}
					} else if(j.posicion == Posicion.ESCOLTA) {
						if(escTit < 2) {
							j.rol = Rol.SUPLENTE;
							escTit++;
						} else {
							j.rol = Rol.NOJUEGA;
						}
					} else if(j.posicion == Posicion.ALERO) {
						if(alerosTit < 2) {
							j.rol = Rol.SUPLENTE;
							alerosTit++;
						} else {
							j.rol = Rol.NOJUEGA;
						}
					} else if(j.posicion == Posicion.ALAPIVOT) {
						if(alapsTit < 2) {
							j.rol = Rol.SUPLENTE;
							alapsTit++;
						} else {
							j.rol = Rol.NOJUEGA;
						}
					} else {
						if(pivotsTit < 2) {
							j.rol = Rol.SUPLENTE;
							pivotsTit++;
						} else {
							j.rol = Rol.NOJUEGA;
						}
					}
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
