package negocio;

//Clase interna para almacenar los datos de
//temporadas pasadas de cada jugador
public class Estadistica {
	int anyo;
	int idEquipo; //equipo con el que comienza la temporada
	int puntos;
	int asistencias;
	int rebotes;
	int partidosJugados; 

	public Estadistica(Jugador j) {
		this.anyo = LigaManager.anyo;
		this.idEquipo = j.tid;
		this.puntos = j.puntosTemporada;
		this.asistencias = j.asistenciasTemporada;
		this.rebotes = j.rebotesTemporada;
		this.partidosJugados = j.partidosJugadosTemporada;
	}

	public Estadistica(int anyo, int tid, int p, int r, int a, int part) {
		this.anyo = anyo;
		this.idEquipo = tid;
		this.puntos = p;
		this.rebotes = r;
		this.asistencias = a;
		this.partidosJugados = part;
	}

	protected String getAbrevEquipo() {
		for(Equipo e: LigaManager.equipos) {
			if(e.tid == idEquipo) {
				return e.getAbrev();
			}
		}
		return "";
	}

	protected double getPuntosPorPartido() {
		if(partidosJugados == 0) return 0;
		return Math.round(100*puntos*1.0/partidosJugados)/100.0;
	}

	protected double getAsistenciasPorPartido() {
		if(partidosJugados == 0) return 0;
		return Math.round(100*asistencias*1.0/partidosJugados)/100.0;
	}

	protected double getRebotesPorPartido() {
		if(partidosJugados == 0) return 0;
		return Math.round(100*rebotes*1.0/partidosJugados)/100.0;
	}
}

