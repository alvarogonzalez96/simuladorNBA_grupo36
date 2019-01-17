package negocio;

import java.util.*;

public class Playoffs {

	public HashMap<Series, SeriePlayoffs> series;
	public int fase; //0: cuartos, 1: semis, 2: finales conf, 3: finales
	public Equipo campeon; //campeón de la NBA
	
	public Playoffs() {
		fase = 0;
		campeon = null;
		inicializar();
	}
	
	/**
	 * Método que inicializa la lista de series.
	 * Solo se cargarán las series de cuartos.
	 * */
	private void inicializar() {
		Clasificacion e = LigaManager.clasificaciones.get("ESTE");
		Clasificacion o = LigaManager.clasificaciones.get("OESTE");
		
		series = new HashMap<>();
		
		series.put(Series.CUARTOS_1_ESTE, new SeriePlayoffs(e.equipos.get(0), e.equipos.get(7)));
		series.put(Series.CUARTOS_2_ESTE, new SeriePlayoffs(e.equipos.get(1), e.equipos.get(6)));
		series.put(Series.CUARTOS_3_ESTE, new SeriePlayoffs(e.equipos.get(2), e.equipos.get(5)));
		series.put(Series.CUARTOS_4_ESTE, new SeriePlayoffs(e.equipos.get(3), e.equipos.get(4)));
		
		series.put(Series.CUARTOS_1_OESTE, new SeriePlayoffs(o.equipos.get(0), o.equipos.get(7)));
		series.put(Series.CUARTOS_2_OESTE, new SeriePlayoffs(o.equipos.get(1), o.equipos.get(6)));
		series.put(Series.CUARTOS_3_OESTE, new SeriePlayoffs(o.equipos.get(2), o.equipos.get(5)));
		series.put(Series.CUARTOS_4_OESTE, new SeriePlayoffs(o.equipos.get(3), o.equipos.get(4)));
	}
	
	/**
	 * Método que se usa para avanzar un partido en cada serie.
	 * Si todas las series terminan, se crean las series correspondientes 
	 * a la siguiente ronda (fase).
	 * */
	public void avanzar() {
		for(SeriePlayoffs s: series.values()) {
			s.jugarPartido();
		}
		
		if(finDeFase()) {
			fase++;
			switch(fase) {
			case 1: //semis
				crearSemis();
				break;
			case 2: //finales conferencia
				crearFinalesConferencia();
				break;
			case 3: //finales
				crearFinales();
				break;
			case 4: //ha terminado la temporada
				campeon = series.get(Series.FINAL).getGanador();
				LigaManager.campeon = campeon;
				LigaManager.fase++;
				
				//
				
				break;
			}
		}
	}
	
	private void crearSemis() {
		Equipo a, b;
		
		// Este 1
		a = series.get(Series.CUARTOS_1_ESTE).getGanador();
		b = series.get(Series.CUARTOS_4_ESTE).getGanador();
		series.put(Series.SEMIS_1_ESTE, new SeriePlayoffs(a,b));
		
		// Este 2
		a = series.get(Series.CUARTOS_2_ESTE).getGanador();
		b = series.get(Series.CUARTOS_3_ESTE).getGanador();
		series.put(Series.SEMIS_2_ESTE, new SeriePlayoffs(a,b));

		// Oeste 1
		a = series.get(Series.CUARTOS_1_OESTE).getGanador();
		b = series.get(Series.CUARTOS_4_OESTE).getGanador();
		series.put(Series.SEMIS_1_OESTE, new SeriePlayoffs(a,b));

		// Oeste 2
		a = series.get(Series.CUARTOS_2_OESTE).getGanador();
		b = series.get(Series.CUARTOS_3_OESTE).getGanador();
		series.put(Series.SEMIS_2_OESTE, new SeriePlayoffs(a,b));
	}
	
	private void crearFinalesConferencia() {
		Equipo a, b;
		
		// Este
		a = series.get(Series.SEMIS_1_ESTE).getGanador();
		b = series.get(Series.SEMIS_2_ESTE).getGanador();
		series.put(Series.FINAL_ESTE, new SeriePlayoffs(a,b));

		// Oeste
		a = series.get(Series.SEMIS_1_OESTE).getGanador();
		b = series.get(Series.SEMIS_2_OESTE).getGanador();
		series.put(Series.FINAL_OESTE, new SeriePlayoffs(a,b));
	}
	
	private void crearFinales() {
		Equipo a, b;
		
		a = series.get(Series.FINAL_ESTE).getGanador();
		b = series.get(Series.FINAL_OESTE).getGanador();
		series.put(Series.FINAL, new SeriePlayoffs(a,b));
	}
	
	/**
	 * Método que indica si una fase ha terminado.
	 * @return si todas las series de la ronda(fase) han terminado
	 * */
	private boolean finDeFase() {
		for(SeriePlayoffs s: series.values()) {
			if(!s.terminada) {
				return false;
			}
		}
		return true;
	}
	
	public Equipo getCampeon() {
		return campeon;
	}
	
	/**
	 * Clase utilizada para guardar la información de 
	 * cada serie de playoffs.
	 * */
	public class SeriePlayoffs {
		Equipo a, b;
		Partido[] partidos;
		int proximoPartido;
		int victoriasA, victoriasB;
		boolean terminada;
		
		public SeriePlayoffs(Equipo a, Equipo b) {
			this.a = a;
			this.b = b;
			victoriasA = victoriasB = 0;
			proximoPartido = 0;
			terminada = false;
			partidos = new Partido[7];
			for(int i = 0; i < 7; i++) {
				partidos[i] = new Partido(a,b);
			}
		}
		
		/**
		 * Método que simula un partido de playoffs
		 * @return si la serie ha terminado o no
		 * */
		public void jugarPartido() {
			if(!terminada) { //si la serie ha terminado no hace nada
				Partido p = partidos[proximoPartido];
				p.jugar(true);
				if(p.getGanador().equals(a)) {
					victoriasA++;
				} else {
					victoriasB++;
				}
				if(victoriasA == 4 || victoriasB == 4) {
					terminada = true;
				}
			}
		}
		
		public int getVictoriasA() {
			return victoriasA;
		}
		
		public int getVictoriasB() {
			return victoriasB;
		}
		
		public Equipo getA() {
			return a;
		}
		
		public Equipo getB() {
			return b;
		}
		
		/**
		 * Método que devuelve el equipo ganador de la serie una vez terminada
		 * */
		public Equipo getGanador() {
			if(terminada) {
				if(victoriasA > victoriasB) {
					return a;
				}
				return b;
			}
			return null;
		}
	}
	
	// Todas las diferentes series que hay
	public enum Series {
		CUARTOS_1_ESTE, CUARTOS_2_ESTE, CUARTOS_3_ESTE, CUARTOS_4_ESTE,
		CUARTOS_1_OESTE, CUARTOS_2_OESTE, CUARTOS_3_OESTE, CUARTOS_4_OESTE,
		SEMIS_1_OESTE, SEMIS_2_OESTE,
		SEMIS_1_ESTE, SEMIS_2_ESTE,
		FINAL_ESTE, FINAL_OESTE, 
		FINAL
	}
}
