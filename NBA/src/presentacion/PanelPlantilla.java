package presentacion;

import negocio.Equipo;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.awt.event.*;

import negocio.Liga;

public class PanelPlantilla extends JPanel {

	/**
	 * En este panel (pestanya) se mostrara una tabla con la
	 * plantilla (jugadores) del equipo seleccionado.
	 **/
	
	private Equipo[] equipos;
	
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JComboBox<String> combo;
	
	public PanelPlantilla(Liga liga) {
		super();
		equipos = liga.getEquipos();
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10,10,10,10));
		crearPaneles();
		setListeners();
	}
	
	private void crearPaneles() {
		panelSeleccion = new JPanel();
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
		scrollTabla.getViewport().setBackground(Color.WHITE);
		
		panelSeleccion.add(label);
		panelSeleccion.add(combo);
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);
	}
	
	private void setListeners() {
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Equipo equipo = getEquipoSeleccionado(combo.getSelectedIndex());
				tabla.setModel(equipo.getTableModel());
				tabla.getColumnModel().getColumn(0).setMinWidth(200);
			}
		});
	}
	
	private Equipo getEquipoSeleccionado(int n) {
		return equipos[n];
	}
	
	private void addEquipos() {
		for(Equipo e: equipos) {
			combo.addItem(e.getNombre());
		}
	}
	
}
