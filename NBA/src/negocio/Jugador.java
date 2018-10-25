package negocio;

import java.util.Comparator;

import org.json.*;

public class Jugador {
	
	protected String nombre;
	protected Posicion posicion;
	protected int rebote;//reb
	protected Rol rol;
	protected int tiroLibre;//ft
	protected int tiroCerca;//fg
	protected int tiroLejos;//tp
	protected int defensa; //diq
	protected int asistencia;//pss
	protected int condicionFisica;
	protected int minutos;
	protected int tiempoJugado;
	protected int anyoNac;//born->year
	protected int overall;
	int tid;
		
	public Jugador(String nombre, Posicion posicion, int rebote, Rol rol, int tiroLibre, int tiroCerca, int tiroLejos, int defensa,
			int asistencia, int anyoNac, int tid) {
		super();
		this.nombre = nombre;
		this.posicion = posicion;
		this.rebote = rebote;
		this.rol = rol;
		this.tiroLibre = tiroLibre;
		this.tiroCerca = tiroCerca;
		this.tiroLejos = tiroLejos;
		this.defensa = defensa;
		this.asistencia = asistencia;
		this.condicionFisica = 100;
		this.minutos = 0;
		this.tiempoJugado = 0;
		this.anyoNac = anyoNac;
		this.tid = tid;
	}
	
	public Jugador() {
		super();
		this.nombre = "Desconocido";
		this.posicion = Posicion.BASE;
		this.rebote = 0;
		this.rol = null;
		this.tiroLibre = 0;
		this.tiroCerca = 0;
		this.tiroLejos = 0;
		this.defensa = 0;
		this.asistencia = 0;
		this.condicionFisica = 0;
		this.minutos = 0;
		this.tiempoJugado = 0;
		this.anyoNac = 0;
		this.tid = -1;	
	}
	
	public void cargarJugador(JSONObject json) { 
		//se le pasa todo el objeto JSON correspondiente
		//al jugador actual
		nombre = json.getString("name");
		tid = json.getInt("tid");
		posicion = seleccionarPosicion(json.getString("pos"));
		rebote = json.getJSONArray("ratings").getJSONObject(0).getInt("reb");
		tiroLibre = json.getJSONArray("ratings").getJSONObject(0).getInt("ft");
		tiroCerca = json.getJSONArray("ratings").getJSONObject(0).getInt("fg");
		tiroLejos = json.getJSONArray("ratings").getJSONObject(0).getInt("tp");
		defensa = json.getJSONArray("ratings").getJSONObject(0).getInt("diq");
		asistencia = json.getJSONArray("ratings").getJSONObject(0).getInt("pss");
		anyoNac = json.getJSONObject("born").getInt("year");
		overall = cargarOverallJugador();
	}
	
	private int cargarOverallJugador() {
		int ov = (int) ((rebote + tiroLibre + (tiroCerca + tiroLejos) + defensa + asistencia)/5);
		
		return ov;
	}
	
	private Posicion seleccionarPosicion(String atJson) {
		switch (atJson) {
		case "FC": case "PF":
			this.posicion = Posicion.ALAPIVOT;
			break;
		case "PG": case "G":
			this.posicion = Posicion.BASE;
			break;
		case "SF": case "GF":
			posicion = Posicion.ALERO;
			break;
		case "C":
			posicion = Posicion.PIVOT;
			break;
		case "SG":
			posicion = Posicion.ESCOLTA;
			break;
		}
		return posicion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Posicion getPosicion() {
		return posicion;
	}

	public void setPosicion(Posicion posicion) {
		this.posicion = posicion;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public int getTiroLibre() {
		return tiroLibre;
	}

	public void setTiroLibre(int tiroLibre) {
		this.tiroLibre = tiroLibre;
	}

	public int getAnyoNac() {
		return anyoNac;
	}

	public void setAnyoNac(int anyoNac) {
		this.anyoNac = anyoNac;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public void setDefensa(int defensa) {
		this.defensa = defensa;
	}

	public int getTiroCerca() {
		return tiroCerca;
	}

	public void setTiroCerca(int tiroCerca) {
		this.tiroCerca = tiroCerca;
	}

	public int getTiroLejos() {
		return tiroLejos;
	}

	public void setTiroLejos(int tiroLejos) {
		this.tiroLejos = tiroLejos;
	}

	public int getAsistencia() {
		return asistencia;
	}

	public void setAsistencia(int asistencia) {
		this.asistencia = asistencia;
	}

	public int getRebote() {
		return rebote;
	}

	public void setRebote(int rebote) {
		this.rebote = rebote;
	}

	public int getCondicionFisica() {
		return condicionFisica;
	}

	public void setCondicionFisica(int condicionFisica) {
		this.condicionFisica = condicionFisica;
	}

	public int getDefensa() {
		return defensa;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}

	public int getTiempoJugado() {
		return tiempoJugado;
	}

	public void setTiempoJugado(int tiempoJugado) {
		this.tiempoJugado = tiempoJugado;
	}

	@Override
	public String toString() {
		double min = (double) minutos/60 ;
		
		return "Jugador [nombre=" + nombre + ", posicion=" + posicion + ", rol=" + rol + ", tiroCerca=" + tiroCerca
				+ ", tiroLejos=" + tiroLejos + ", asistencia=" + asistencia + ", rebote=" + rebote
				+ ", condicionFisica=" + condicionFisica  + ", defensa="
				+ defensa +  ", segundos=" + minutos + ", minutos= "+ min + ", m="+ tiempoJugado + "]";
	}
}



class OrdenadorJugadores implements Comparator<Jugador>{

	@Override
	public int compare(Jugador a, Jugador b) {
		if(a.overall > b.overall) {
			return -1;
		} else if(b.overall > a.overall) {
			return 1;
		} 
		return 0;
	}
	
}
