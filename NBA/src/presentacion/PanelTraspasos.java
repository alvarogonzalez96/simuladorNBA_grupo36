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
	private static Equipo equipoUsuario;
	
	private ArrayList<Jugador> jugadoresOfreceUsuario;
	private ArrayList<Jugador> jugadoresOfreceLiga;
	
	private static JComboBox<String> comboJugadoresUsuario; //Jugadores del equipo que utiliza el usuario
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
	
	private 	Integer[] contadorLiga, contadorUsuario;
	
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
		equipoSeleccionadoPorDefecto();
		
		initComboBox();
		initBotones();
		initTablas();
		anyadirPaneles();
	}
	
	private void equipoSeleccionadoPorDefecto() {
		ArrayList<Jugador> auxiliar = new ArrayList<Jugador>();
		auxiliar.add(new Jugador());
		equipoSeleccionado = new Equipo(-8, auxiliar);
	}
	
	private void initComboBox() {
		comboJugadoresUsuario = new JComboBox<String>();
		comboJugadoresUsuario.addItem("Elige un jugador");
		for (Jugador j : equipoUsuario.getJugadores()) {
			comboJugadoresUsuario.addItem(j.getNombre());
		}
	
		comboEquiposLiga = new JComboBox<String>();
		comboEquiposLiga.addItem("Elige un equipo");
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
			}
		});	
		
		buscarOferta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(equipoSeleccionado.getTid() != -8) {
					jugadoresOfreceLiga.clear();
					calcularOferta();
					actualizarTablaLiga();	
				} else {
					JOptionPane.showMessageDialog(null, "Elige un equipo al que ofrecer el traspaso");
				}
			}
		});
		
		borrarOferta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jugadoresOfreceLiga.clear();
				jugadoresOfreceUsuario.clear();
				actualizarTablaUsuario();
				actualizarTablaLiga();
			}
		});
		
		aceptarOferta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//hasta que el draft no haya terminado no se puede hacer nada
				if(LigaManager.draftEnCurso) {
					JOptionPane.showMessageDialog(null, "No puedes realizar ninguna gestion hasta que haya concluido el draft", "Aviso", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if(jugadoresOfreceUsuario.size() > 0 && jugadoresOfreceLiga.size() > 0) {
					recontarPosiciones();
					if(contadorUsuario[0] >= 2 && contadorUsuario[1] >= 2 && contadorUsuario[2] >= 2 && contadorUsuario[3] >= 2 && contadorUsuario[4] >= 2) {
						traspasarJugadores();
					} else {
						JOptionPane.showMessageDialog(null, "No tienes suficientes jugadores cubriendo todas las posiciones");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Faltan jugadores para completar el traspaso");	
				}
				resetearContadores();
			}
		});
	}
	
	private void resetearContadores() {
		for (int i = 0; i < contadorLiga.length; i++) {
			contadorUsuario[i] = 0;
		}
		
		contadorUsuario = LigaManager.contarPosiciones(equipoUsuario, contadorUsuario);
	}
	
	private void recontarPosiciones() {
		for (Jugador j : jugadoresOfreceUsuario) {
			if(j.getPosicion().equals(Posicion.BASE)) {
				contadorUsuario[0]--;
			} else if(j.getPosicion().equals(Posicion.ESCOLTA)) {
				contadorUsuario[1]--;
			} else if(j.getPosicion().equals(Posicion.ALERO)) {
				contadorUsuario[2]--;
			} else if(j.getPosicion().equals(Posicion.ALAPIVOT)) {
				contadorUsuario[3]--;
			} else if(j.getPosicion().equals(Posicion.PIVOT)) {
				contadorUsuario[4]--;
			}
		}
		
		for (Jugador j : jugadoresOfreceLiga) {
			if(j.getPosicion().equals(Posicion.BASE)) {
				contadorUsuario[0]++;
			} else if(j.getPosicion().equals(Posicion.ESCOLTA)) {
				contadorUsuario[1]++;
			} else if(j.getPosicion().equals(Posicion.ALERO)) {
				contadorUsuario[2]++;
			} else if(j.getPosicion().equals(Posicion.ALAPIVOT)) {
				contadorUsuario[3]++;
			} else if(j.getPosicion().equals(Posicion.PIVOT)) {
				contadorUsuario[4]++;
			}
		}
	}
	
	private void traspasarJugadores() {
		ArrayList<Jugador> borrar = new ArrayList<>();
		for (Jugador j : jugadoresOfreceUsuario) {
			j.setTid(equipoSeleccionado.getTid());
			borrar.add(j);
		}

		for (Jugador j : borrar) {
			if(equipoUsuario.getJugadores().contains(j)) {
				equipoUsuario.getJugadores().remove(j);
				jugadoresOfreceUsuario.remove(j);
				equipoSeleccionado.getJugadores().add(j);
			}
		}

		borrar.clear();

		for (Jugador j : jugadoresOfreceLiga) {
			j.setTid(equipoUsuario.getTid());
			borrar.add(j);
		}

		for (Jugador j : borrar) {
			if(equipoSeleccionado.getJugadores().contains(j)) {
				equipoSeleccionado.getJugadores().remove(j);
				jugadoresOfreceLiga.remove(j);
				equipoUsuario.getJugadores().add(j);
			}
		}
		equipoUsuario.ordenarJugadores();
		equipoUsuario.asignarRoles();
		equipoSeleccionado.ordenarJugadores();
		equipoSeleccionado.asignarRoles();
		
		actualizarCombo();
		
		JOptionPane.showMessageDialog(null, "Traspaso completado");

	}
	
	public static void actualizarCombo() {
		comboJugadoresUsuario.removeAllItems();
		equipoUsuario = LigaManager.usuario.getEquipo();
		
		comboJugadoresUsuario.addItem("Elige un jugador");
		for (Jugador j : equipoUsuario.getJugadores()) {
			comboJugadoresUsuario.addItem(j.getNombre());
		}
	}
	
	private void actualizarTablaUsuario() {
		modeloJugadoresUsuario = null;
		tablaUsuario.setModel(getModeloUsuario());
		tablaUsuario.getColumnModel().getColumn(0).setMinWidth(200);
		repaint();
	}
	
	private void actualizarTablaLiga() {
		modeloJugadoresLiga = null;
		tablaLiga.setModel(getModeloLiga());
		tablaLiga.getColumnModel().getColumn(0).setMinWidth(200);
		repaint();
	}
	
	private void calcularOferta() {
		int dineroDisponibleUsuario, dineroDisponibleLiga;
		int salarioOfrecido = 0;
		double overallTotal;

		contadorLiga = new Integer[5];
		contadorUsuario = new Integer[5];

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

		for (Jugador j : equipoSeleccionado.getJugadores()) {
			ArrayList<Jugador> jug = new ArrayList<>();
			jug.add(j);
			dineroDisponibleLiga = calcularDineroDisponible(equipoSeleccionado, jug);
			if((j.getSalario() > salarioOfrecido - 3000 && j.getSalario() < salarioOfrecido + 3000)) {
				if((dineroDisponibleLiga + salarioOfrecido >= 0 && (dineroDisponibleUsuario + j.getSalario()) >= 0)) {
					if(((j.getOverall() > overallTotal - 3) && (j.getOverall() < overallTotal + 3))) {
						//El jugador elegido tiene un overall en torno al overall ofrecido
						if(j.getPosicion().equals(Posicion.BASE) && contadorLiga[0] >= 2) {
							//Es base y no hay carencia de bases
							jugadoresOfreceLiga.add(j);	
							break;
						} else if(j.getPosicion().equals(Posicion.ESCOLTA) && contadorLiga[1] >= 2) {
							//Es escolta y no hay carencia de escoltas
							jugadoresOfreceLiga.add(j);
							break;
						} else if(j.getPosicion().equals(Posicion.ALERO) && contadorLiga[2] >= 2) {
							//Es alero y no hay carencia de aleros
							jugadoresOfreceLiga.add(j);
							break;
						} else if(j.getPosicion().equals(Posicion.ALAPIVOT) && contadorLiga[3] >= 2) {
							//Es alapivot y no hay carencia de alapivots
							jugadoresOfreceLiga.add(j);
							break;
						} else if(j.getPosicion().equals(Posicion.PIVOT) && contadorLiga[4] >= 2) {
							//Es pivot y no hay carencia de pivots
							jugadoresOfreceLiga.add(j);
							break;
						}
					} 
				}
			}
		}
		if(jugadoresOfreceLiga.size() == 0) {
			JOptionPane.showMessageDialog(null, "El equipo " + equipoSeleccionado.getNombre() + " no esta interesado en un traspaso");
		}
	}

	private double calcularOverallTotal(ArrayList<Jugador> jugadores) {
		double overall = 0;
		for (Jugador j : jugadores) {
			overall = overall + j.getOverall();
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
			return 7;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Posicion";
			case 2: return "Edad";
			case 3: return "Overall";
			case 4: return "Salario";
			case 5: return "Anyos de Contrato";
			case 6: return "Valoracion";
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
			case 1: return j.getPosicion();
			case 2: return j.getEdad();
			case 3: return j.getOverall();
			case 4: return j.getSalario();
			case 5: return j.getAnyosContratoRestantes();
			case 6: return j.getValoracion();
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
			return 7;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Posicion";
			case 2: return "Edad";
			case 3: return "Overall";
			case 4: return "Salario";
			case 5: return "Anyos de Contrato";
			case 6: return "Valoracion";
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
			case 1: return j.getPosicion();
			case 2: return j.getEdad();
			case 3: return j.getOverall();
			case 4: return j.getSalario();
			case 5: return j.getAnyosContratoRestantes();
			case 6: return j.getValoracion();
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