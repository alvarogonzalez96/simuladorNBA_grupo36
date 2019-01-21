package presentacion;

import negocio.*;

import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

@SuppressWarnings("serial")
public class PanelPlantilla extends PanelTab {

	/**
	 * En este panel (pestaña) se mostrará una tabla con la
	 * plantilla (jugadores) del equipo seleccionado.
	 **/
	
	private Equipo[] equipos;
	
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JComboBox<String> combo;
	private JButton botonMiEquipo;
	
	private int indiceSeleccionado;
	
	public PanelPlantilla() {
		super();
		indiceSeleccionado = LigaManager.usuario.getEquipo().getTid();
	}
	
	protected void crearPaneles() {
		equipos = LigaManager.equipos;
		panelSeleccion = new JPanel(new FlowLayout());
	}
	
	protected void setListeners() {
		combo.addActionListener(
				(ActionEvent e) -> {
					actualizarEquipo();
				});
		
		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				int row = tabla.rowAtPoint(p);
				if(e.getClickCount() >= 2 && tabla.getSelectedRow() != -1) {
					lanzaVentanaJugador(LigaManager.getJugadorConNombre((String) tabla.getValueAt(row, 0)));
				}
			}
		});
		botonMiEquipo.addActionListener(
				(ActionEvent e) -> {
					combo.setSelectedIndex(LigaManager.usuario.getEquipo().getTid());
					actualizarEquipo();
				});
	}
	
	private void lanzaVentanaJugador(Jugador j) {
		new VentanaJugador(this, j);
	}
	
	private void actualizarEquipo() {
		Equipo equipo = getEquipoSeleccionado(combo.getSelectedIndex());
		indiceSeleccionado = combo.getSelectedIndex();
		tabla.setModel(equipo.getTableModel());
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
	}
	
	private Equipo getEquipoSeleccionado(int n) {
		return equipos[n];
	}
	
	private void addEquipos() {
		for(Equipo e: equipos) {
			combo.addItem(e.getNombre());
		}
	}

	@Override
	protected void initComponentes() {
		combo = new JComboBox<>();
		combo.setFont(new Font("Arial", Font.PLAIN, 15));
		addEquipos();
		JLabel label = new JLabel("Equipo: ");
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		
		botonMiEquipo = new JButton("Mi equipo");
		
		tabla = new JTable();
		tabla.setRowHeight(50);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollTabla = new JScrollPane(tabla);
		tabla.setModel(equipos[0].getTableModel());
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollTabla.getViewport().setBackground(Color.WHITE);
		
		panelSeleccion.add(label);
		panelSeleccion.add(combo);
		panelSeleccion.add(botonMiEquipo);
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);
	}

	@Override
	protected void seleccionado() {
		tabla.setModel(equipos[combo.getSelectedIndex()].getTableModel());
		int n = indiceSeleccionado;
		n++;
		if(n >= 30) n -= 30;
		combo.setSelectedIndex(n);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {}
		if(n == 0) n = 29;
		else n--;
		indiceSeleccionado = n;
		combo.setSelectedIndex(indiceSeleccionado);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {}
		tabla.repaint();
		repaint();
	}
	
	public void seleccionarEquipo(int n) {
		combo.setSelectedIndex(n);
	}
}