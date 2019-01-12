package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import negocio.*;
import java.util.ArrayList;
import java.util.Collections;

public class VentanaDraft extends JFrame {
	
	private JPanel panelMain;
	private JPanel panelTop, panelTablas;
	private JButton botonComenzar;
	
	private JTable tablaEquipos, tablaJugadores;
	private JScrollPane scrollEquipos, scrollJugadores;
	
	private JSlider sliderVel;
	
	private PanelPlayoffs panelPlayoffs;
	private ArrayList<Jugador> draft;
	private ArrayList<Jugador> seleccion;
	private ArrayList<Equipo> ordenEquipos;
	
	private boolean finalizado;
	private int indice;
	private int posicionUsuario; //posicion en la primera ronda del equipo del usuario
	private boolean turnoUsuario;
	
	private Thread hilo;
	
	public VentanaDraft(PanelPlayoffs panelPlayoffs, ArrayList<Jugador> draft) {
		this.panelPlayoffs = panelPlayoffs;
		this.draft = draft;
		this.finalizado = false;
		this.ordenEquipos = new ArrayList<>(LigaManager.clasificaciones.get("GENERAL").getEquipos());
		Collections.reverse(ordenEquipos);
		this.seleccion = new ArrayList<>();
		this.indice = 0;
		this.turnoUsuario = false;
		for(int i = 0; i < ordenEquipos.size(); i++) {
			if(ordenEquipos.get(i).getTid() == LigaManager.usuario.getEquipo().getTid()) {
				this.posicionUsuario = i;
			}
		}
		
		inicializarPaneles();
		
		setTitle("Draft");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setListeners();
		setVisible(true);
		
		initHilo();
	}
	
	/**
	 * Avanza un paso mas
	 * Por cada paso un equipo elige, hasta que se llega al 
	 * equipo del usuario.
	 * Entonces todo se para hasta que el usuario elige un jugador
	 * Y despues se sigue paso a paso. (se repite con la siguiente ronda)
	 * */
	private void avanzar() {
		int t = indice%30;
		Equipo e = ordenEquipos.get(t);
		Jugador j = LigaManager.elegirMejorDisponible(draft); //esto lo elimina de la lista
		seleccionar(j,e);
		repaint();
		indice++;
	}
	
	private void seleccionar(Jugador j, Equipo e) {
		j.setTid(e.getTid());
		e.getJugadores().add(j);
		LigaManager.jugadores.add(j);
		draft.remove(j);
		seleccion.add(j);
	}
	
	private void inicializarPaneles() {
		sliderVel = new JSlider(1,5,5);
		sliderVel.setPaintTicks(true);
		sliderVel.setMajorTickSpacing(1);
		sliderVel.setSnapToTicks(true);
		sliderVel.setPaintLabels(true);
		
		botonComenzar = new JButton("Comenzar draft");
		
		panelTop = new JPanel(new FlowLayout());
		panelTablas = new JPanel(new BorderLayout());
		
		panelTop.add(botonComenzar);
		panelTop.add(new JLabel("Velocidad de la simulacion: "));
		panelTop.add(sliderVel);
		
		tablaEquipos = new JTable(getModeloEquipos());
		tablaJugadores = new JTable(getModeloJugadores());
		tablaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollEquipos = new JScrollPane(tablaEquipos);
		scrollJugadores = new JScrollPane(tablaJugadores);
		
		panelTablas.add(scrollEquipos, BorderLayout.CENTER);
		panelTablas.add(scrollJugadores, BorderLayout.EAST);
		
		panelMain = new JPanel(new BorderLayout());
		panelMain.setLayout(new BorderLayout());
		panelMain.add(panelTop, BorderLayout.NORTH);
		panelMain.add(panelTablas, BorderLayout.CENTER);
		panelMain.setBorder(new EmptyBorder(10,10,10,10));
		getContentPane().add(panelMain);
	}
	
	private void setListeners() {
		tablaJugadores.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(turnoUsuario) {
					Point p = e.getPoint();
					int row = tablaJugadores.rowAtPoint(p);
					if(tablaJugadores.getSelectedRow() != -1 && e.getClickCount() >= 2) {
						Jugador j = draft.get(row);
						int opcion = JOptionPane.showConfirmDialog(null,  "Deseas seleccionar a "+j.getNombre()+"?");
						if(opcion == JOptionPane.YES_OPTION) {
							seleccionar(j, LigaManager.usuario.getEquipo());
							repaint();
							indice++;
							turnoUsuario = false;
							hilo.interrupt();
							initHilo();
							hilo.start();
						}
					}
				}
			}
		});
		
		botonComenzar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hilo.start();
			}
		});
	}
	
	private void initHilo() {
		hilo = new Thread(new Runnable() {

			@Override
			public void run() {
				while(indice < 60) {
					if(indice != posicionUsuario && indice != posicionUsuario+30) {
						avanzar();
					} else {
						//elige el usuario
						JOptionPane.showMessageDialog(null, "Es tu turno para elegir un jugador. Haz doble click en el jugador que quieras seleccionar (tabla de la derecha)", "Eleccion de draft", JOptionPane.INFORMATION_MESSAGE);
						turnoUsuario = true;
						break;
					}
					
					try {
						Thread.sleep((sliderVel.getMaximum()-sliderVel.getValue()+1)*250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(indice >= 60) {
					JOptionPane.showMessageDialog(null, "El draft ha terminado. Ahora es tiempo de renovaciones, despidos y fichajes de la agencia libre. Cuando estes listo, haz click en comenzar temporada!", "Fin del draft", JOptionPane.INFORMATION_MESSAGE);
					finalizado = true;
					//dispose();
				}
			}
			
		});
	}
	
	@Override
	public void dispose() {
		if(!finalizado) {
			JOptionPane.showMessageDialog(null, "No puedes cerrar esta ventana hasta que hayas elegido a tus dos jugadores", "Aviso", JOptionPane.WARNING_MESSAGE);
		} else {
			LigaManager.draftEnCurso = false;
			super.dispose();
			panelPlayoffs.comenzarFaseGestiones();
		}
		
	}
	
	private TableModel getModeloEquipos() {
		return new ModeloTablaEquipos();
	}
	
	private TableModel getModeloJugadores() {
		return new ModeloTablaJugadores();
	}
	
	private class ModeloTablaEquipos implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener arg0) {}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int c) {
			switch(c) {
			case 0: return "Ronda";
			case 1: return "Posicion";
			case 2: return "Equipo";
			case 3: return "Jugador seleccionado";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return 60;
		}

		@Override
		public Object getValueAt(int row, int col) {
			int i = 1+ (row%30); //posicion relativa dentro de una ronda
			int ronda;
			if(row < 30) {
				ronda = 1;
			} else {
				ronda = 2;
			}
			Equipo e = ordenEquipos.get(i-1);
			switch(col) {
			case 0: return ronda;
			case 1: return i;
			case 2: return e.getAbrev();
			case 3:
				if(seleccion.isEmpty()) return "";
				if(row >= seleccion.size()) return "";
				return seleccion.get(row).getNombre();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener arg0) {}

		@Override
		public void setValueAt(Object arg0, int arg1, int arg2) {}
		
	}
	
	private class ModeloTablaJugadores implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Posicion";
			case 2: return "Calidad";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return draft.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Jugador j = draft.get(rowIndex);
			switch(columnIndex) {
			case 0: return j.getNombre();
			case 1: return j.getPosicion();
			case 2: return j.getOverall();
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
