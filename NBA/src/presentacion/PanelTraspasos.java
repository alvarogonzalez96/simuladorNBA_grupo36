package presentacion;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import negocio.*;
import negocio.Equipo.ModeloFinanzasEquipo;

public class PanelTraspasos extends PanelTab{
	
	private Equipo[] equipos;
	private Equipo equipoSeleccionado;
	private Equipo equipoUsuario;
	
	private ArrayList<Jugador> jugadoresOfreceUsuario;
	private ArrayList<Jugador> jugadoresOfreceLiga;
	
	private JComboBox<String> comboJugadoresUsuario; //Jugadores del equipo que utiliza el usuario
	private JComboBox<String> comboEquiposLiga; //Equipos de la liga (exceptuando el del usuario)
	
	private JButton aceptarOferta;
	private JButton borrarOferta;
	private JButton buscarOferta;
	
	private JTable tablaUsuario; //Tabla con los jugadores que quiere transferir el usuario
	private JTable tablaLiga; //Tabla con los jugadores que ofrecen al usuario
	private JScrollPane scrollUsuario, scrollLiga;
	
	private JPanel panelPrincipal, panelIzqArriba, panelIzqAbajo, panelCentroArriba, panelCentroAbajo;
	private JPanel izq, dcha;
	private JPanel auxArribaNorte, auxArribaSur, auxAbajo;
	
	private TableModel modeloJugadoresUsuario, modeloJugadoresLiga;
	
	
	public PanelTraspasos() {
		super();
	}

	@Override
	protected void initComponentes() {
		equipoUsuario = LigaManager.usuario.getEquipo();
		equipos = LigaManager.equipos;
		
		jugadoresOfreceUsuario = new ArrayList<Jugador>();
		jugadoresOfreceLiga = new ArrayList<Jugador>();
		
		initComboBox();
		initBotones();
		initTablas();
		anyadirPaneles();
	}
	
	private void initComboBox() {
		comboJugadoresUsuario = new JComboBox<String>();
		for (Jugador j : equipoUsuario.getJugadores()) {
			comboJugadoresUsuario.addItem(j.getNombre());
		}
		
		comboEquiposLiga = new JComboBox<String>();
		for (Equipo e : equipos) {
			if(!e.equals(equipoUsuario)) {
				comboEquiposLiga.addItem(e.getNombre());
			}
		}
	}
	
	private void initBotones() {
		aceptarOferta = new JButton("ACEPTAR OFERTA");
		borrarOferta = new JButton("BORRAR");
		buscarOferta = new JButton("BUSCAR OFERTA");
	}
	
	private void initTablas() {
		tablaUsuario = new JTable();
		tablaUsuario.setRowHeight(50);
		tablaUsuario.setFont(new Font("Arial", Font.PLAIN, 20));
		tablaUsuario.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollUsuario = new JScrollPane(tablaUsuario);
		tablaUsuario.setModel(getModeloUsuario());
		tablaUsuario.getColumnModel().getColumn(0).setMinWidth(200);
		scrollUsuario.getViewport().setBackground(Color.WHITE);
		
		tablaLiga = new JTable();
		tablaLiga.setRowHeight(50);
		tablaLiga.setFont(new Font("Arial", Font.PLAIN, 20));
		tablaLiga.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollLiga = new JScrollPane(tablaLiga);
		tablaLiga.setModel(getModeloLiga());
		tablaLiga.getColumnModel().getColumn(0).setMinWidth(200);
		scrollLiga.getViewport().setBackground(Color.WHITE);
	}

	private void anyadirPaneles() {
		panelIzqArriba.add(comboJugadoresUsuario);
		
		auxArribaNorte.add(borrarOferta);
		panelCentroArriba.add(auxArribaNorte);
		auxArribaSur.add(aceptarOferta);
		panelCentroArriba.add(auxArribaSur);
		
		panelIzqAbajo.add(comboEquiposLiga);
		
		auxAbajo.add(buscarOferta);
		panelCentroAbajo.add(auxAbajo, BorderLayout.CENTER);
		
		izq.add(panelIzqArriba);
		izq.add(panelCentroArriba);
		dcha.add(scrollUsuario);
		izq.add(panelIzqAbajo);
		izq.add(panelCentroAbajo);
		dcha.add(scrollLiga);
		
		panelPrincipal.add(izq);
		panelPrincipal.add(dcha);
		add(panelPrincipal);
	}
	
	@Override
	protected void crearPaneles() {
		panelPrincipal = new JPanel(new GridLayout(1, 2));
		izq = new JPanel(new GridLayout(2, 2));
		dcha = new JPanel(new GridLayout(2, 1));
		panelIzqArriba = new JPanel(new FlowLayout());
		panelCentroArriba = new JPanel(new GridLayout(2, 1));
		panelIzqAbajo = new JPanel(new FlowLayout());
		panelCentroAbajo = new JPanel(new BorderLayout());
		auxArribaNorte = new JPanel(new FlowLayout());
		auxArribaSur = new JPanel(new FlowLayout());
		auxAbajo = new JPanel(new FlowLayout());
	}

	@Override
	protected void setListeners() {
		comboJugadoresUsuario.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Jugador j : equipoUsuario.getJugadores()) {
					if(j.getNombre().equals(comboJugadoresUsuario.getSelectedItem())) {
						jugadoresOfreceUsuario.add(j);
					}
				}
				actualizarTablaUsuario();
			}
		});
		
		comboEquiposLiga.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Equipo equipo : equipos) {
					if(equipo.getNombre().equals(comboEquiposLiga.getSelectedItem())) {
						equipoSeleccionado = equipo;
					}
				}
				actualizarTablaLiga();
			}
		});	
	}

	private void actualizarTablaUsuario() {
		modeloJugadoresUsuario = null;
		tablaUsuario.setModel(getModeloUsuario());
		tablaUsuario.getColumnModel().getColumn(0).setMinWidth(200);
		repaint();
	}
	
	private void actualizarTablaLiga() {
		modeloJugadoresLiga = null;
		calcularOferta();
		tablaLiga.setModel(getModeloLiga());
		tablaLiga.getColumnModel().getColumn(0).setMinWidth(200);
	}
	
	private void calcularOferta() {
		int dineroDisponibleUsuario, dineroDisponibleLiga;
		int salarioOfrecido = 0;
		double overallTotal;
		Integer[] contadorLiga, contadorUsuario;
		contadorLiga = new Integer[5];
		contadorUsuario = new Integer[5];
		jugadoresOfreceLiga = null;
		
		for (Jugador j : jugadoresOfreceUsuario) {
			salarioOfrecido = salarioOfrecido + j.getSalario();
		}
		
		for (int i = 0; i < contadorUsuario.length; i++) {
			contadorLiga[i] = 0;
			contadorUsuario[i] = 0;
		}
		contadorUsuario = LigaManager.contarPosiciones(equipoUsuario, contadorUsuario);
		contadorLiga = LigaManager.contarPosiciones(equipoSeleccionado, contadorLiga);
		
		overallTotal = calcularOverallTotal(jugadoresOfreceUsuario);
		dineroDisponibleUsuario = calcularDineroDisponible(equipoUsuario, jugadoresOfreceUsuario);
		dineroDisponibleLiga = calcularDineroDisponible(equipoSeleccionado, jugadoresOfreceLiga);
		
		if(dineroDisponibleUsuario >= 1000) {
			//El usuario tiene dinero disponible
			if(dineroDisponibleLiga - salarioOfrecido > 0) {
				//Liga tiene dinero
				for (Jugador j : equipoSeleccionado.getJugadores()) {
					if(j.getOverall() > overallTotal - 3 && j.getOverall() < overallTotal + 5) {
						
					}
				}
				
			} else {
				//Liga no tiene dinero
				
			}
		} else {
			//El usuario no tiene dinero disponible
		}
			
	}
	
	private double calcularOverallTotal(ArrayList<Jugador> jugadores) {
		double overall = 0;
		for (Jugador j : jugadores) {
			overall = overall + j.getValoracion();
		}
		return overall;
	}
	
	private int calcularDineroDisponible(Equipo e, ArrayList<Jugador> jugadores) {
		int disponible = 0;
		int salarioTotal = 0;
		for (Jugador j : e.getJugadores()) {
			if(!jugadores.contains(j)) {
				salarioTotal = salarioTotal + j.getSalario();	
			}
		}
	
		disponible = Equipo.limiteSalarial - salarioTotal;
		return disponible;
	}
	
	@Override
	protected void seleccionado() {
		// TODO Auto-generated method stub
		
	}
	
	public TableModel getModeloLiga() {
		if(modeloJugadoresLiga == null) {
			modeloJugadoresLiga = new ModeloJugadoresLiga();
		}
		return modeloJugadoresLiga;
	}
	
	public TableModel getModeloUsuario() {
		if(modeloJugadoresUsuario == null) {
			modeloJugadoresUsuario = new ModeloJugadoresUsuario();
		}
		return modeloJugadoresUsuario;
	}
	
	public class ModeloJugadoresUsuario implements TableModel{
		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Posicion";
			case 2: return "Overall";
			case 3: return "Salario";
			case 4: return "Anyos de Contrato";
			case 5: return "Valoracion";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return jugadoresOfreceUsuario.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Jugador j = jugadoresOfreceUsuario.get(rowIndex);
			switch(columnIndex) {
			case 0: return j.getNombre();
			case 1: return j.getEdad();
			case 2: return j.getOverall();
			case 3: return j.getSalario();
			case 4: return j.getAnyosContratoRestantes();
			case 5: return j.getValoracion();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}	
	}
	
	public class ModeloJugadoresLiga implements TableModel{
		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Posicion";
			case 2: return "Overall";
			case 3: return "Salario";
			case 4: return "Anyos de Contrato";
			case 5: return "Valoracion";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return jugadoresOfreceLiga.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Jugador j = jugadoresOfreceLiga.get(rowIndex);
			switch(columnIndex) {
			case 0: return j.getNombre();
			case 1: return j.getEdad();
			case 2: return j.getOverall();
			case 3: return j.getSalario();
			case 4: return j.getAnyosContratoRestantes();
			case 5: return j.getValoracion();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}	
	}

}
