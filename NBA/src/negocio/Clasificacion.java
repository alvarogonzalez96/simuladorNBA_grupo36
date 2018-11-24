package negocio;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.event.TableModelListener;
import javax.swing.table.*;

public class Clasificacion {

	static OrdenadorClasificacion ord;
	static {
		ord = new OrdenadorClasificacion();
	}
	
	ArrayList<Equipo> equipos;
	ModeloTablaClasificacion modelo;
	
	public Clasificacion(Equipo[] equipos) {
		this.equipos = new ArrayList<Equipo>();
		for(Equipo e: equipos) {
			this.equipos.add(e);
		}
		this.equipos.sort(ord);
	}
	
	public Clasificacion(ArrayList<Equipo> equipos) {
		this.equipos = equipos;
		this.equipos.sort(ord);
	}
	
	public Equipo get(int i) {
		return equipos.get(i);
	}
	
	public void ordenar() {
		this.equipos.sort(ord);
	}
	
	public Equipo getGanador() { //es necesario llamar al metodo ordenar antes
		return equipos.get(0);
	}
	
	public ArrayList<Equipo> getEquipos(){
		return this.equipos;
	}
	
	public void imprimir() {
		for(Equipo e: equipos) {
			System.out.println(e.getVictorias()+"-"+e.getDerrotas()+" "+e.getNombre());
		}
	}
	
	public ModeloTablaClasificacion getTableModel() {
		if(modelo==null) {
			modelo = new ModeloTablaClasificacion();
		}
		return modelo;
	}
	
	/*
	 * Clasificacion vista como tabla:
	 * 	Columnas: EQUIPO  |  VICTORIAS  |  DERROTAS
	 * 
	 * */

	private class ModeloTablaClasificacion implements TableModel {
		@Override
		public void addTableModelListener(TableModelListener arg0) {}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int arg0) {
			switch(arg0) {
			case 0: 
				return "Equipo";
			case 1:
				return "Victorias";
			case 2:
				return "Derrotas";
			default: return null;
			}
		}

		@Override
		public int getRowCount() {
			return equipos.size();
		}

		@Override
		public Object getValueAt(int e, int a) {
			Equipo equipo = equipos.get(e);
			switch(a) {
			case 0: return equipo.nombre;
			case 1: return equipo.victorias;
			case 2: return equipo.derrotas;
			default: return null;
			}
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
	
}

class OrdenadorClasificacion implements Comparator<Equipo> {
	@Override
	public int compare(Equipo a, Equipo b) {
		if(a.getVictorias() > b.getVictorias()) {
			return -1;
		} else if(a.getVictorias() < b.getVictorias()) {
			return 1;
		} else {
			if(a.getDerrotas() < b.getDerrotas()) {
				return -1;
			} else if(a.getDerrotas() > b.getDerrotas()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
