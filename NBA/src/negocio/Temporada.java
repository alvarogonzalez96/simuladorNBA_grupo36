package negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Temporada {

	private Equipo[] equipos;
	private ArrayList<Jugador> jugadores;
	private Calendario calendario;
	private HashMap<String, Clasificacion> clasificaciones;
	private int anyo;
	
	public Temporada(int anyo, Equipo[] equipos, ArrayList<Jugador> jugadores, Calendario calendario) {
		this.anyo = anyo;
		this.equipos = equipos;
		this.jugadores = jugadores;
		this.calendario = new Calendario(calendario, anyo);
		simularPretemporada();
		inicializarClasificaciones();
	}
	
	public boolean simularDia() {
		Date diaActual = calendario.diaActual;
		if(calendario.calendario.containsKey(diaActual)) {
			for(Partido p: calendario.calendario.get(diaActual)) {
				p.jugar();
			}			
		}
		calendario.avanzarDia();
		ordenarClasificaciones();
		try {
			if(calendario.diaActual.equals(new SimpleDateFormat("yyyy-MM-dd").parse("2016-04-14"))) {
				System.out.println("fin de temporada");
				return false; //se ha terminado el ultimo dia de temporada regular
			}
		} catch (ParseException e) {e.printStackTrace();}
		return true;
	}
	
	public void simularSemana() {
		for(int i = 0; i < 7; i++) {
			simularDia();
		}
	}
	
	public void simularMes() {
		for(int i = 0; i < 30; i++) {
			simularDia();
		}
	}
	
	private void simularPretemporada() {
		// draft
		// retirar jugadores
	}
	
	private void ordenarClasificaciones() {
		for(Clasificacion c: clasificaciones.values()) {
			c.ordenar();
		}
	}
	
	private void inicializarClasificaciones() {
		clasificaciones = new HashMap<>();
		ArrayList<Equipo> este, oeste, atl, cent, sure, pac, suro, noro;
		este = new ArrayList<>();
		oeste = new ArrayList<>();
		atl = new ArrayList<>();
		cent = new ArrayList<>();
		sure = new ArrayList<>();
		pac = new ArrayList<>();
		suro = new ArrayList<>();
		noro = new ArrayList<>();
		
		for(Equipo e: equipos) {
			if(e.conferencia == Conferencia.ESTE) {
				este.add(e);
			} else {
				oeste.add(e);
			}
			switch(e.division) {
			case ATLANTICO: atl.add(e); break;
			case CENTRAL: cent.add(e); break;
			case SURESTE: sure.add(e); break;
			case PACIFICO: pac.add(e); break;
			case SUROESTE: suro.add(e); break;
			case NOROESTE: noro.add(e); break;
			}
		}
		clasificaciones.put("GENERAL", new Clasificacion(equipos));
		clasificaciones.put("ESTE", new Clasificacion(este));
		clasificaciones.put("OESTE", new Clasificacion(oeste));
		clasificaciones.put("ATLANTICO", new Clasificacion(atl));
		clasificaciones.put("CENTRAL", new Clasificacion(cent));
		clasificaciones.put("SURESTE", new Clasificacion(sure));
		clasificaciones.put("PACIFICO", new Clasificacion(pac));
		clasificaciones.put("NOROESTE", new Clasificacion(noro));
		clasificaciones.put("SUROESTE", new Clasificacion(suro));
	}
}
