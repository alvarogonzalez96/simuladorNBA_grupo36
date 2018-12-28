package presentacion;

import negocio.*;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.awt.event.*;

public class PanelPlantilla extends PanelTab {

	/**
	 * En este panel (pestanya) se mostrara una tabla con la
	 * plantilla (jugadores) del equipo seleccionado.
	 **/
	
	private Equipo[] equipos;
	
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JComboBox<String> combo;
	
	public PanelPlantilla() {
		super();
	}
	
	protected void crearPaneles() {
		equipos = LigaManager.equipos;
		panelSeleccion = new JPanel();
	}
	
	protected void setListeners() {
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarEquipo();
			}
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
	}
	
	private void lanzaVentanaJugador(Jugador j) {
		new VentanaJugador(this, j);
	}
	
	private void actualizarEquipo() {
		Equipo equipo = getEquipoSeleccionado(combo.getSelectedIndex());
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
		addEquipos();
		JLabel label = new JLabel("Equipo: ");
		
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
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);
	}

	@Override
	protected void seleccionado() {
		//actualizar tablas para edad de rookies
		actualizarEquipo();
		repaint();
	}
	
}