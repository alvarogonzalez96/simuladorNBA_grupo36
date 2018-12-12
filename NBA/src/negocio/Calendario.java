package negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.json.*;

import datos.ParseadorJSON;

public class Calendario {

	public static final Date PRIMER_DIA;
	public static final Date ULTIMO_DIA_TEMP_REGULAR;
	static {
		Date p = null;
		Date u = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			p = sdf.parse("2015-10-27");
			u = sdf.parse("2016-04-13");
		} catch (ParseException e) {}
		PRIMER_DIA = p;
		ULTIMO_DIA_TEMP_REGULAR = u;
	}
	
	public LinkedList<Partido> ultimosPartidosJugados;
	
	private ModeloTablaCalendario modelo;
	
	public int anyo;
	public Equipo[] equipos;
	public HashMap<Date, ArrayList<Partido>> calendario;
	public Date diaActual;
	
	public Calendario(Equipo[] equipos, Date d) {
		this.anyo = 2015;
		this.equipos = equipos;
		this.diaActual = d;
		calendario = new HashMap<>();
		ultimosPartidosJugados = new LinkedList<>();
		cargarCalendario();
		modelo = new ModeloTablaCalendario();
	}
	
	public Calendario(Calendario c, int anyo) {
		this.equipos = c.equipos;
		this.anyo = anyo;
		this.calendario = new HashMap<>();
		try {
			diaActual = new SimpleDateFormat("yyyy-MM-dd").parse("2015-10-27");
		} catch (ParseException e) {
			diaActual = null;
			e.printStackTrace();
		}
		copiarPartidos(c);
		modelo = new ModeloTablaCalendario();
	}
	
	public ModeloTablaCalendario getModelo() {
		return modelo;
	}
	
	public void addPartidoJugado(Partido p) {
		ultimosPartidosJugados.addFirst(p);
		if(ultimosPartidosJugados.size() > 30) {
			ultimosPartidosJugados.removeLast();
		}
	}
	
	private void copiarPartidos(Calendario c) {
		for(Date d: c.calendario.keySet()) {
			if(!calendario.containsKey(d)) {
				calendario.put(d, new ArrayList<>());
			}
			for(Partido p: c.calendario.get(d)) {
				calendario.get(d).add(new Partido(p));
			}
		}
	}
	
	public void addPartido(Date d, Partido p) {
		if(calendario.keySet().contains(d)) {
			calendario.get(d).add(p);
		} else {
			ArrayList<Partido> m = new ArrayList<>();
			m.add(p);
			calendario.put(d, m);
		}
	}
	
	public void avanzarDia() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaActual);
		cal.add(Calendar.DATE, 1);
		diaActual = cal.getTime();
	}
	
	private void cargarCalendario() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JSONArray partidos = ParseadorJSON.getArrayPrimario("data/calendario.json");
		for(Object o: partidos) {
			if(o instanceof JSONObject) {
				JSONObject p = (JSONObject) o;
				Equipo local, visitante;
				int tidLocal, tidVisitante;
				tidLocal = p.getInt("homeTeamId");
				tidVisitante = p.getInt("visitorTeamId");
				local = equipos[tidLocal];
				visitante = equipos[tidVisitante];
				Partido partido = new Partido(local, visitante);
				
				String strFecha = p.getString("gameDateEst").substring(0, 10);
				Date fecha = null;
				try {
					fecha = sdf.parse(strFecha);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (calendario.containsKey(fecha)) {
					calendario.get(fecha).add(partido);
				} else {
					ArrayList<Partido> lista = new ArrayList<>();
					lista.add(partido);
					calendario.put(fecha, lista);
				}
			}
		}
	}
	
	/**
	 * Resetea el calendario.
	 * Reinicia cada partido del calendario, 
	 * con los marcadores a 0, etc;
	 * */
	public void reset() {
		for(ArrayList<Partido> partidos: calendario.values()) {
			for(Partido p: partidos) {
				p = new Partido(p); //nuevo partido con los mismos equipos.
				p.puntosLocal = p.puntosVisitante = 0;
				p.finalizado = false;
			}
		}
	}
	
	public Date getDiaActual() {
		return this.diaActual;
	}
	
	private class ModeloTablaCalendario implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return "�ltimos partidos jugados";
		}

		@Override
		public int getRowCount() {
			return 30;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(rowIndex >= ultimosPartidosJugados.size()) {
				return "---";
			}
			Partido p = ultimosPartidosJugados.get(rowIndex);
			String local = p.local.getAbrev();
			String vis = p.visitante.getAbrev();
			int pLocal = p.puntosLocal;
			int pVis = p.puntosVisitante;
			return vis+" "+pVis+" - "+pLocal+" "+local;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

	}
}
