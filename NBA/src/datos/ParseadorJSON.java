package datos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.*;

import negocio.Equipo;
import negocio.Jugador;

public class ParseadorJSON {
	
	public static JSONObject getObjetoPrimario(String dir) {
		JSONObject o = null;
		try {
			o = new JSONObject(archivoAString(dir));
		} catch (Exception e) {}
		return o;
	}
	
	public static String archivoAString(String dir) throws IOException {
		return new String(Files.readAllBytes(Paths.get(dir)));
	}
	
	public static ArrayList<Jugador> aArrayListJugador(JSONArray json){
		ArrayList<Jugador> players = new ArrayList<>();
		for(Object j: json) {
			if(j instanceof JSONObject) {
				Jugador jug = new Jugador();
				jug.cargarJugador((JSONObject) j);
				players.add(jug);
			}
		}
		return players;
	}
	
	public static Equipo[] aArrayEquipos(JSONArray json) {
		Equipo[] equipos = new Equipo[30];
		
		int i = 0;
		for(Object o: json) {
			if(o instanceof JSONObject) {
				Equipo e = new Equipo((JSONObject) o);
				equipos[i] = e;
			}
			i++;
		}
		
		return equipos;
	}
	
}
