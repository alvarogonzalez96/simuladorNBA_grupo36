package negocio;

import java.util.ArrayList;
import java.util.Comparator;

public class Clasificacion {

	static OrdenadorClasificacion ord;
	static {
		ord = new OrdenadorClasificacion();
	}
	
	ArrayList<Equipo> equipos;
	
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
