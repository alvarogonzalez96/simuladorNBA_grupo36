package presentacion;

import negocio.*;

import java.util.*;

import javax.swing.*;

import java.awt.*;

@SuppressWarnings("serial")
public class PanelClasificacion extends PanelTab {
	
	private JPanel panelIzquierda;
		private JPanel panelIzquierdaArriba, panelIzquierdaAbajo;
			private JPanel panelPacifico, panelSuroeste, panelNoroeste;
	private JPanel panelDerecha;
		private JPanel panelDerechaArriba, panelDerechaAbajo;
			private JPanel panelAtlantico, panelCentral, panelSureste;
	
	private HashMap<String, Clasificacion> clasificaciones;	
	private HashMap<String, JTable> tablas;
	
	public PanelClasificacion() {
		super();
		setLayout(new GridLayout(1,2));
	}
	
	/**
	 * Método para crear las tablas a partir de las clasificaciones de la clase Liga.
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
		addTabla(panelPacifico, "PACIFICO");
		addTabla(panelSuroeste, "SUROESTE");
		addTabla(panelNoroeste, "NOROESTE");
		
		addTabla(panelDerechaArriba, "ESTE");
		addTabla(panelAtlantico, "ATLANTICO");
		addTabla(panelCentral, "CENTRAL");
		addTabla(panelSureste, "SURESTE");
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
		panelIzquierdaArriba = new JPanel(new BorderLayout());
		panelIzquierdaAbajo = new JPanel(new GridLayout(1,3));
		panelDerechaArriba = new JPanel(new BorderLayout());
		panelDerechaAbajo = new JPanel(new GridLayout(1,3));
		
		panelPacifico = new JPanel(new BorderLayout());
		panelSuroeste= new JPanel(new BorderLayout());
		panelNoroeste = new JPanel(new BorderLayout());
		
		panelAtlantico = new JPanel(new BorderLayout());
		panelCentral = new JPanel(new BorderLayout());
		panelSureste = new JPanel(new BorderLayout());
		
		panelIzquierdaAbajo.add(panelPacifico);
		panelIzquierdaAbajo.add(panelSuroeste);
		panelIzquierdaAbajo.add(panelNoroeste);
		
		panelDerechaAbajo.add(panelAtlantico);
		panelDerechaAbajo.add(panelCentral);
		panelDerechaAbajo.add(panelSureste);
		
		panelPacifico.add(new JLabel("PACÍFICIO"), BorderLayout.NORTH);
		panelSuroeste.add(new JLabel("SUROESTE"), BorderLayout.NORTH);
		panelNoroeste.add(new JLabel("NOROESTE"), BorderLayout.NORTH);
		
		panelAtlantico.add(new JLabel("ATLÁNTICO"), BorderLayout.NORTH);
		panelCentral.add(new JLabel("CENTRAL"), BorderLayout.NORTH);
		panelSureste.add(new JLabel("SURESTE"), BorderLayout.NORTH);
		
		panelDerechaArriba.add(new JLabel("ESTE"), BorderLayout.NORTH);
		panelIzquierdaArriba.add(new JLabel("OESTE"), BorderLayout.NORTH);
		
		panelIzquierda.add(panelIzquierdaArriba);
		panelIzquierda.add(panelIzquierdaAbajo);
		panelDerecha.add(panelDerechaArriba);
		panelDerecha.add(panelDerechaAbajo);
		
		add(panelIzquierda);
		add(panelDerecha);
		
		insertarTablas();
	}

	@Override
	protected void setListeners() {}

	@Override
	protected void seleccionado() {
		for(String c: tablas.keySet()) {
			tablas.get(c).setModel(LigaManager.clasificaciones.get(c).getTableModel());
		}
	}
	
}