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
	
	/**
	 * Metodo que devuelve un nombre completo aleatorio (nombre + apellido).
	 * @return un String con un nombre completo.
	 * */
	public static String getNombreCompleto() {
		return getNombre()+" "+getApellido();
	}
	
	/**
	 * Metodo para conseguir un apellido aleatorio.
	 * @return un String con un apellido de la lista de apellidos
	 * */
	private static String getApellido() {
		return getRandom(apellidos);
	}
	
	/**
	 * Metodo para conseguir un nombre aleatorio.
	 * @return un String con un nombre de la lista de nombres
	 * */
	private static String getNombre() {
		return getRandom(nombres);
	}
	
	/**
	 * Metodo que carga todas las lineas (String) del fichero
	 * indicado en el ArrayList indicado
	 * @param fich, String con la direccion al fichero a leer
	 * @param lista, ArrayList<String> en el que se cargan las lineas del fichero.
	 * */
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
	
	/**
	 * Metodo que devuelve un elemento aleatorio (String) de un ArrayList<String>
	 * @return un String conseguido de manera aleatorio de la lista indicada
	 * @param lineas, ArrayList<String> del que se cogera un elemento aleatorio
	 * */
	private static String getRandom(ArrayList<String> lineas) {
		return lineas.get((int)(Math.random()*lineas.size()));
	}
}


