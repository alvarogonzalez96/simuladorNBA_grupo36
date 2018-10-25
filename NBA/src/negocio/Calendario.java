package negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.*;

import datos.ParseadorJSON;

public class Calendario {

	public Equipo[] equipos;
	public HashMap<Date, ArrayList<Partido>> calendario;
	public Date diaActual;
	
	public Calendario(Equipo[] equipos) {
		this.equipos = equipos;
		calendario = new HashMap<>();
		cargarCalendario();
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
	
	public static void main(String[] args) throws ParseException {
		Date d;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		d = sdf.parse("2016-01-30");
		System.out.println(d);
		
	}
}
