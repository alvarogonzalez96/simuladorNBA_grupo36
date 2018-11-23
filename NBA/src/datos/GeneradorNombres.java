package datos;

import java.util.*;
import java.io.*;

public class GeneradorNombres {
	
	static ArrayList<String> nombres, apellidos;
	static {
		nombres = new ArrayList<>();
		apellidos = new ArrayList<>();
		cargarLineas("data/nombres.txt", nombres);
		cargarLineas("data/apellidos.txt", apellidos);
	}
	
	public static String getNombreCompleto() {
		return getNombre()+" "+getApellido();
	}
	
	private static String getApellido() {
		return getRandom(apellidos);
	}
	
	private static String getNombre() {
		return getRandom(nombres);
	}
	
	private static void cargarLineas(String fich, ArrayList<String> lista) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fich)));
			String linea = br.readLine();
			while(linea != null) {
				lista.add(linea.substring(0, 1).toUpperCase() + linea.substring(1).toLowerCase());
				linea = br.readLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getRandom(ArrayList<String> lineas) {
		return lineas.get((int)(Math.random()*lineas.size()));
	}
	
	public static void main(String[] args) {
		System.out.println(getNombreCompleto());
	}
}


