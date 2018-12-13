package negocio;

import java.util.Comparator;

import javax.swing.table.TableModel;

import datos.GeneradorNombres;

import org.json.*;

public class Jugador {
	
	//atributos fijos (nunca cambian para un mismo jugador)
	protected String nombre;
	protected Posicion posicion;
	protected int rebote;//reb
	protected int tiroLibre;//ft
	protected int tiroCerca;//fg
	protected int tiroLejos;//tp
	protected int defensa; //diq
	protected int asistencia;//pss
	protected int condicionFisica;
	protected int edad;//2018 - born year
	protected int overall;
	
	//atributos variables (pueden cambiar)
	protected Rol rol;
	int tid;
	protected double puntosPartido; //puntos que anota por partido
	protected int nPuntos; //puntos que mete en el partido
	protected double asistenciasPartido;//asistencias por partido
	protected int nAsistencias;//asistencias en el partido
	protected double rebotesPartido;//rebotes por partido
	protected int nRebotes;//rebotes en un partido
	public int partidosJugadosTemporada; //numero de partidos jugados en una temporada
	
	protected int salario;
	protected int anyosContratoRestantes;
	
	protected double valoracion;
	
	protected boolean rookie; //Al crear un nuevo jugador, siempre sera rookie
	
	//Atributos para la simulacion de partidos
	protected int minutos;
	protected int tiempoJugado;
	
	//Atributos para calcular la media
	protected int hgt, stre, spd, jmp, endu, ins, dnk, oiq, drb;
	
	private TableModel modelo;
	
	public Jugador(String nombre, Posicion posicion, int rebote, Rol rol, int tiroLibre, int tiroCerca, int tiroLejos, int defensa,
			int asistencia, int edad, int tid) {
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
		this.edad = edad;
		this.tid = tid;
		this.rookie = true;
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
		this.edad = 0;
		this.tid = -1;	
		this.rookie = true;
	}
	
	/**
	 * Construye un jugador a partir de la combinacion 
	 * de tributos de otros dos jugadores.
	 * Se usa para la creacion de nuevos jugadores 
	 * para el draft.
	 * 
	 * Los dos jugadores pasados como parametro 
	 * deben jugar en la misma posicion.
	 * */
	public Jugador(Jugador a, Jugador b) {
		if(a.posicion != b.posicion) {
			System.err.println("Error en el constructor de jugador.");
		}
		this.nombre = GeneradorNombres.getNombreCompleto();
		this.posicion = a.posicion;
		int rand = (int)(Math.random()*3);
		this.edad = 18 + rand; 
		this.rebote = (a.rebote+b.rebote)/2;
		this.tiroLibre = (a.tiroLibre+b.tiroLibre)/2;
		this.tiroCerca = (a.tiroCerca+b.tiroCerca)/2;
		this.tiroLejos = (a.tiroLejos+b.tiroLejos)/2;
		this.defensa = (a.defensa+b.defensa)/2;
		this.asistencia = (a.asistencia+b.asistencia)/2;
		this.condicionFisica = (a.condicionFisica+b.condicionFisica)/2;
		this.hgt = (a.hgt + b.hgt)/2;
		this.stre = (a.stre + b.stre)/2;
		this.spd = (a.spd + b.spd)/2;
		this.jmp = (a.spd + b.spd)/2;
		this.endu = (a.endu + b.endu)/2;
		this.ins = (a.ins + b.ins)/2;
		this.dnk = (a.dnk + b.dnk)/2;
		this.oiq = (a.oiq + b.oiq)/2;
		this.drb = (a.drb + b.drb)/2;
		this.edad = 0;
		this.tid = -1;
		this.rookie = true;
		this.overall = cargarOverallJugador();
	}
	
	public Jugador(Jugador j) {
		this.nombre = j.nombre;
		this.posicion = j.posicion;
		this.rebote = j.rebote;
		this.tiroLibre = j.tiroLibre;
		this.tiroCerca = j.tiroCerca;
		this.tiroLejos = j.tiroLejos;
		this.defensa = j.defensa;
		this.asistencia = j.asistencia;
		this.condicionFisica = j.condicionFisica;
	
	}
	
	public void cargarJugador(JSONObject json) { 
		//se le pasa todo el objeto JSON correspondiente
		//al jugador actual
		nombre = json.getString("name");
		tid = json.getInt("tid");
		posicion = seleccionarPosicion(json.getString("pos"));
		rebote = json.getJSONArray("ratings").getJSONObject(0).getInt("reb");
		tiroLibre = json.getJSONArray("ratings").getJSONObject(0).getInt("ft");
		if(tid >= 0) {
			salario = json.getJSONObject("contract").getInt("amount");
			anyosContratoRestantes = json.getJSONObject("contract").getInt("exp")-2018;
		}
		/*
		 * En el json el atributo ins significa la calidad de tiro interior; por lo que,
		 * para los pivots, utilizaremos dicho atributo, y para el resto, el tiro de
		 * media distancia
		 */
		
		if(posicion == Posicion.PIVOT ) {
			tiroCerca = json.getJSONArray("ratings").getJSONObject(0).getInt("ins");
			ins = json.getJSONArray("ratings").getJSONObject(0).getInt("fg");
		} else {
			tiroCerca = json.getJSONArray("ratings").getJSONObject(0).getInt("fg");
			ins = json.getJSONArray("ratings").getJSONObject(0).getInt("ins");
		}
		
		if(json.getJSONObject("draft").getInt("year") == 2018 ) {
			rookie = true;
		} else {
			rookie = false;
		}
		tiroLejos = json.getJSONArray("ratings").getJSONObject(0).getInt("tp");
		defensa = json.getJSONArray("ratings").getJSONObject(0).getInt("diq");
		asistencia = json.getJSONArray("ratings").getJSONObject(0).getInt("pss");
		//Atributos para calcular la media
		//protected int hgt, stre, spd, jmp, endu, ins, dnk, oiq, drb;
		hgt = json.getJSONArray("ratings").getJSONObject(0).getInt("hgt");
		stre = json.getJSONArray("ratings").getJSONObject(0).getInt("stre");
		spd = json.getJSONArray("ratings").getJSONObject(0).getInt("spd");
		jmp = json.getJSONArray("ratings").getJSONObject(0).getInt("jmp");
		endu = json.getJSONArray("ratings").getJSONObject(0).getInt("endu");
		dnk = json.getJSONArray("ratings").getJSONObject(0).getInt("dnk");
		oiq = json.getJSONArray("ratings").getJSONObject(0).getInt("oiq");
		drb = json.getJSONArray("ratings").getJSONObject(0).getInt("drb");
		
		edad = 2018 - json.getJSONObject("born").getInt("year");
		overall = cargarOverallJugador();
	}
	
	private int cargarOverallJugador() {
		int ov = (int) ((((rebote + tiroLibre + tiroCerca + tiroLejos + defensa + asistencia + hgt + stre + spd + jmp + endu + ins + dnk + oiq + drb)/15) * 99)/74);
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
		case "SF": case "F":
			posicion = Posicion.ALERO;
			break;
		case "C":
			posicion = Posicion.PIVOT;
			break;
		case "SG": case "GF":
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
	
	public double getAsistenciasPartido() {
		return asistenciasPartido;
	}
	
	public double getRebotesPartido() {
		return rebotesPartido;
	}
	
	public void setNPuntos(int nPuntos) {
		this.nPuntos = nPuntos;
	}
	
	public void setNAsistencias(int nAsistencias) {
		this.nAsistencias = nAsistencias;
	}
	
	public void setNRebotes(int nRebotes) {
		this.nRebotes = nRebotes;
	}
	
	public double getPuntosPorPartido() {
		if(partidosJugadosTemporada <= 0) {
			return 0;
		} else {
			return (double) Math.round(100 * puntosPartido / partidosJugadosTemporada)/100;
		}
	}
	
	public double getAsistenciasPorPartido() {
		if(partidosJugadosTemporada <= 0) {
			return 0;
		} else {
			return (double) Math.round(100 * asistenciasPartido / partidosJugadosTemporada)/100;
		}
	}
	
	public double getRebotesPorPartido() {
		if(partidosJugadosTemporada <= 0) {
			return 0;
		} else {
			return (double) Math.round(100 * rebotesPartido / partidosJugadosTemporada)/100;
		}
	}
	
	public int getTiroLibre() {
		return tiroLibre;
	}

	public void setTiroLibre(int tiroLibre) {
		this.tiroLibre = tiroLibre;
	}
	public double getPuntosPartido() {
		return puntosPartido;
	}

	public void setPuntosPartido(double puntosPartido) {
		this.puntosPartido = puntosPartido;
	}
	
	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
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

	public int getOverall() {
		return overall;
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
	
	public double getValoracion() {	
		return (this.getPuntosPartido() + this.getAsistenciasPartido() + this.getRebotesPartido());
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
		if(a.posicion == b.posicion) {
			if(a.overall > b.overall) {
				return -1;
			} else if(b.overall > a.overall) {
				return 1;
			} 
			return 0;
		} else {
			if(a.posicion.ordinal() < b.posicion.ordinal()) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}

