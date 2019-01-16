package negocio;

import java.util.Comparator;
import java.util.HashMap;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import datos.BD;
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
	protected int anyoNac;
	protected int overall;
	protected int id;

	//atributos variables (de cada temporada)
	public Rol rol;
	int tid;
	protected int puntosTemporada; //puntos anotados en la temporada actual
	protected int asistenciasTemporada;//asistencias que lleva en la temporada actual
	protected int rebotesTemporada;//rebotes que lleva en la temporada actual
	public int partidosJugadosTemporada; //numero de partidos jugados en la temporada actual

	public int salario;
	public int anyosContratoRestantes;

	protected double valoracion;

	protected boolean rookie; //Al crear un nuevo jugador, siempre sera rookie

	//Atributos para la simulacion de partidos
	protected int minutos;
	protected int tiempoJugado;
	protected int puntosPartido; //puntos que anota en el partido actual
	protected int asistenciasPartido;//asistencias en el partido actual
	protected int rebotesPartido;//rebotes en el partido actual

	//Atributos para calcular la media
	protected int hgt, stre, spd, jmp, endu, ins, dnk, oiq, drb;

	protected HashMap<Integer, Estadistica> statsTemporadas;

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
		this.minutos = 0;
		this.tiempoJugado = 0;
		this.anyoNac = 1992;
		this.tid = -1;	
		this.rookie = true;
		statsTemporadas = new HashMap<>();
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
		do { //evitar que haya dos jugadores con el mismo nombre
			this.nombre = GeneradorNombres.getNombreCompleto();
		} while(LigaManager.getJugadorConNombre(this.nombre) != null);
		this.posicion = a.posicion;
		int rand = 18 + (int)(Math.random()*3);
		this.anyoNac = LigaManager.anyo - rand; 
		this.rebote = (a.rebote+b.rebote)/2;
		this.tiroLibre = (a.tiroLibre+b.tiroLibre)/2;
		this.tiroCerca = (a.tiroCerca+b.tiroCerca)/2;
		this.tiroLejos = (a.tiroLejos+b.tiroLejos)/2;
		this.defensa = (a.defensa+b.defensa)/2;
		this.asistencia = (a.asistencia+b.asistencia)/2;
		this.hgt = (a.hgt + b.hgt)/2;
		this.stre = (a.stre + b.stre)/2;
		this.spd = (a.spd + b.spd)/2;
		this.jmp = (a.spd + b.spd)/2;
		this.endu = (a.endu + b.endu)/2;
		this.ins = (a.ins + b.ins)/2;
		this.dnk = (a.dnk + b.dnk)/2;
		this.oiq = (a.oiq + b.oiq)/2;
		this.drb = (a.drb + b.drb)/2;
		this.tid = -1;
		this.rookie = true;
		this.overall = cargarOverallJugador();
		statsTemporadas = new HashMap<>();
		this.id = BD.getPrimerIdLibreJugador();
		BD.guardarJugador(this);
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
		statsTemporadas = new HashMap<>();
		this.id = BD.getPrimerIdLibreJugador();
		BD.guardarJugador(this);
	}

	public Jugador(String nombre, int id, int anyoNac, int pos, int overall, int rebote, int tiroLibre,
			int tiroCerca, int tiroLejos, int defensa, int asistencia) {
		this.nombre = nombre;
		this.id = id;
		this.anyoNac = anyoNac;
		this.overall = overall;
		this.rebote = rebote;
		this.tiroLibre = tiroLibre;
		this.tiroCerca = tiroCerca;
		this.tiroLejos = tiroLejos;
		this.defensa = defensa;
		this.asistencia = asistencia;
		for(Posicion p: Posicion.values()) {
			if(p.ordinal() == pos) {
				this.posicion = p;
				break;
			}
		}
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
			anyosContratoRestantes = json.getJSONObject("contract").getInt("exp")-2017; //evitar 0 anyos de contrato
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

		anyoNac = json.getJSONObject("born").getInt("year");
		overall = cargarOverallJugador();

		this.id = BD.getPrimerIdLibreJugador();
		BD.guardarJugador(this);
	}

	private int cargarOverallJugador() {
		int ov = (int) ((((rebote + tiroLibre + tiroCerca + tiroLejos + defensa + asistencia + hgt + stre + spd + jmp + endu + ins + dnk + oiq + drb)/15) * 99)/76);
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

	public double getPuntosPorPartido() {
		if(partidosJugadosTemporada <= 0) {
			return 0;
		} else {
			return (double) Math.round(100 * (double) puntosTemporada / partidosJugadosTemporada)/100;
		}
	}

	public double getAsistenciasPorPartido() {
		if(partidosJugadosTemporada <= 0) {
			return 0;
		} else {
			return (double) Math.round(100 * (double) asistenciasTemporada / partidosJugadosTemporada)/100;
		}
	}

	public double getRebotesPorPartido() {
		if(partidosJugadosTemporada <= 0) {
			return 0;
		} else {
			return (double) Math.round(100 * (double) rebotesTemporada / partidosJugadosTemporada)/100;
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

	public void setPuntosTemporada(int puntosTemporada) {
		this.puntosTemporada = puntosTemporada;
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

	public int getEdad() {
		return LigaManager.anyo - anyoNac;
	}

	public int getOverall() {
		return overall;
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

	public int getPuntosTemporada() {
		return puntosTemporada;
	}

	public int getAsistenciasTemporada() {
		return asistenciasTemporada;
	}

	public int getRebotesTemporada() {
		return rebotesTemporada;
	}

	public double getValoracion() {
		return ((double) (Math.round((this.getPuntosTemporada() + this.getAsistenciasTemporada() + this.getRebotesTemporada())*100))/100);

	}

	public int getSalario() {
		return salario;
	}

	public int getAnyosContratoRestantes() {
		return anyosContratoRestantes;
	}

	public int getID() {
		return id;
	}

	public int getAnyoNac() {
		return anyoNac;
	}

	@Override
	public String toString() {
		double min = (double) minutos/60 ;

		return "Jugador [nombre=" + nombre + ", posicion=" + posicion + ", rol=" + rol + ", tiroCerca=" + tiroCerca
				+ ", tiroLejos=" + tiroLejos + ", asistencia=" + asistencia + ", rebote=" + rebote
				+ ", defensa="
				+ defensa +  ", segundos=" + minutos + ", minutos= "+ min + ", m="+ tiempoJugado + "]";
	}

	public void guardaStatsTemporada() {
		this.statsTemporadas.put(LigaManager.anyo, new Estadistica(this));
	}
	
	public void guardaEstadistica(int anyo, Estadistica e) {
		if(this.statsTemporadas == null) statsTemporadas = new HashMap<>();
		statsTemporadas.put(anyo, e);
	}

	protected String getAbrevEquipo() {
		for(Equipo e: LigaManager.equipos) {
			if(e.tid == this.tid) {
				return e.getAbrev();
			}
		}
		return "";
	}

	public int getPartidosJugadosTemporada() {
		return partidosJugadosTemporada;
	}

	private class ModeloTablaTemporadas implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener arg0) {}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public String getColumnName(int col) {
			switch(col) {
			case 0:
				return "Temporada";
			case 1:
				return "Equipo";
			case 2:
				return "Partidos jugados";
			case 3:
				return "PPP";
			case 4:
				return "APP";
			case 5:
				return "RPP";
			default: return "";
			}
		}

		@Override
		public int getRowCount() {
			return statsTemporadas.size()+1;
		}

		@Override
		public Object getValueAt(int row, int col) {
			if(row == statsTemporadas.size()) {
				//esta pidiendo informacion sobre la temporada actual
				switch(col) {
				case 0: return LigaManager.anyo+"/"+(""+(LigaManager.anyo+1)).substring(2);
				case 1: return getAbrevEquipo();
				case 2: return partidosJugadosTemporada;
				case 3: return getPuntosPorPartido();
				case 4: return getAsistenciasPorPartido();
				case 5: return getRebotesPorPartido();
				}
			}
			System.out.println(row);
			Estadistica e = statsTemporadas.get(row+2018);
			switch(col) {
			case 0: return e.anyo+"/"+(""+(e.anyo+1)).substring(2);
			case 1: return e.getAbrevEquipo();
			case 2: return e.partidosJugados;
			case 3: return e.getPuntosPorPartido();
			case 4: return e.getAsistenciasPorPartido();
			case 5: return e.getRebotesPorPartido();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener arg0) {}

		@Override
		public void setValueAt(Object arg0, int arg1, int arg2) {}

	}
	
	private ModeloTablaTemporadas modeloTablaTemporadas = null;

	public ModeloTablaTemporadas getModeloTablaTemporadas() {
		if(modeloTablaTemporadas == null) modeloTablaTemporadas = new ModeloTablaTemporadas();
		return modeloTablaTemporadas;
	}
}

class OrdenadorJugadores implements Comparator<Jugador> {

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

class OrdenadorAgenciaLibre implements Comparator<Jugador> {
	@Override
	public int compare(Jugador a, Jugador b) {
		if(a.posicion == b.posicion) {
			return b.overall-a.overall;
		} else {
			return a.posicion.ordinal()-b.posicion.ordinal();
		}
	}
}