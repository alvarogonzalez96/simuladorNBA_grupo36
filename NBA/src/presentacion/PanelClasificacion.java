package presentacion;

import negocio.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

@SuppressWarnings("serial")
public class PanelClasificacion extends JPanel {

	private JPanel panelIzquierda;
		private JPanel panelIzquierdaArriba, panelIzquierdaAbajo;
	private JPanel panelDerecha;
		private JPanel panelDerechaArriba, panelDerechaAbajo;
	
	private HashMap<String, Clasificacion> clasificaciones;	
	private HashMap<String, JTable> tablas;
	
	public PanelClasificacion() {
		super();
		setLayout(new GridLayout(1,2));
		setBorder(new EmptyBorder(10,10,10,10));
		crearTablas();
		crearPanel();
	}
	
	/**
	 * Metodo para crear las tablas a partir de las clasificaciones de la clase Liga.
	 * La clase Clasificacion permite ser convertido en tabla (getTableModel).
	 * */
	private void crearTablas() {
		clasificaciones = clasifPrueba(); //en realidad esta lista deberia ser 
		//la misma que la de la clase Liga
		tablas = new HashMap<>();
		for(String c: clasificaciones.keySet()) {
			tablas.put(c, new JTable(clasificaciones.get(c).getTableModel()));
			tablas.get(c).getColumnModel().getColumn(0).setMinWidth(150);
			
			if(!c.equals("ESTE") && !c.equals("OESTE")) {
				tablas.get(c).setFont(new Font("Arial", Font.PLAIN, 20));
				tablas.get(c).setRowHeight(60);
			} else {
				tablas.get(c).setFont(new Font("Arial", Font.PLAIN, 15));
				tablas.get(c).setRowHeight(40);
			}
		}
	}
	
	private void crearPanel() {
		panelIzquierda = new JPanel(new GridLayout(2,1));
		panelDerecha = new JPanel(new GridLayout(2,1));
		panelIzquierdaArriba = new JPanel();
		panelIzquierdaAbajo = new JPanel(new GridLayout(1,3));
		panelDerechaArriba = new JPanel();
		panelDerechaAbajo = new JPanel(new GridLayout(1,3));
		
		panelIzquierda.add(panelIzquierdaArriba);
		panelIzquierda.add(panelIzquierdaAbajo);
		panelDerecha.add(panelDerechaArriba);
		panelDerecha.add(panelDerechaAbajo);
		
		add(panelIzquierda);
		add(panelDerecha);
		
		insertarTablas();
	}
	
	private void insertarTablas() {
		addTabla(panelIzquierdaArriba, "OESTE");
		addTabla(panelIzquierdaAbajo, "PACIFICO");
		addTabla(panelIzquierdaAbajo, "SUROESTE");
		addTabla(panelIzquierdaAbajo, "NOROESTE");
		
		addTabla(panelDerechaArriba, "ESTE");
		addTabla(panelDerechaAbajo, "ATLANTICO");
		addTabla(panelDerechaAbajo, "CENTRAL");
		addTabla(panelDerechaAbajo, "SURESTE");
	}
	
	private void addTabla(JPanel destino, String tabla) {
		JScrollPane scroll = new JScrollPane(tablas.get(tabla));
		scroll.getViewport().setBackground(Color.WHITE);
		destino.add(scroll);
	}
	
	private HashMap<String, Clasificacion> clasifPrueba() {
		HashMap<String, Clasificacion> clasif = new HashMap<>();
		
		for(int i = 0; i < 8; i++) {
			String n = "";
			switch(i) {
			case 0: n = "OESTE"; break;
			case 1: n = "ESTE"; break;
			case 2: n = "ATLANTICO"; break;
			case 3: n = "CENTRAL"; break;
			case 4: n = "SURESTE"; break;
			case 5: n = "PACIFICO"; break;
			case 6: n = "SUROESTE"; break;
			case 7: n = "NOROESTE"; break;
			}
			int tope;
			if(i < 2) tope = 15;
			else tope = 5;
			
			Equipo[] equipos = new Equipo[tope];
			
			for(int j = 0; j < tope; j++) {
				equipos[j] = new Equipo(n+" "+j, 5, 6);
			}
			clasif.put(n, new Clasificacion(equipos));
		}
		return clasif;
	}
	
}
