package presentacion;

import negocio.*;

import java.util.*;

import javax.swing.*;

import java.awt.*;

@SuppressWarnings("serial")
public class PanelClasificacion extends PanelTab {
	
	private JPanel panelIzquierda;
		private JPanel panelIzquierdaArriba, panelIzquierdaAbajo;
	private JPanel panelDerecha;
		private JPanel panelDerechaArriba, panelDerechaAbajo;
	
	private HashMap<String, Clasificacion> clasificaciones;	
	private HashMap<String, JTable> tablas;
	
	public PanelClasificacion() {
		super();
		setLayout(new GridLayout(1,2));
	}
	
	/**
	 * Metodo para crear las tablas a partir de las clasificaciones de la clase Liga.
	 * La clase Clasificacion permite ser convertido en tabla (getTableModel).
	 * */
	private void crearTablas() {
		tablas = new HashMap<>();
		for(String c: clasificaciones.keySet()) {
			tablas.put(c, new JTable(clasificaciones.get(c).getTableModel()));
			
			if(!c.equals("ESTE") && !c.equals("OESTE")) {
				tablas.get(c).setFont(new Font("Arial", Font.PLAIN, 20));
				tablas.get(c).setRowHeight(60);
			} else {
				tablas.get(c).setFont(new Font("Arial", Font.PLAIN, 15));
				tablas.get(c).setRowHeight(40);
			}
		}
	}
	
	protected void crearPaneles() {
		clasificaciones = LigaManager.clasificaciones;
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

	@Override
	protected void initComponentes() {
		crearTablas();
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

	@Override
	protected void setListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void seleccionado() {
		// TODO Auto-generated method stub
		
	}
	
}