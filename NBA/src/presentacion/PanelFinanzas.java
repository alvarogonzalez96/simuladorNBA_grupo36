package presentacion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import negocio.*;

public class PanelFinanzas extends JPanel{

	private Equipo[] equipos;
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JComboBox<String> combo;
	
	public PanelFinanzas() {
		super();
		equipos = LigaManager.equipos;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10,10,10,10));
		crearPaneles();
		setListeners();
	}
	
	public void crearPaneles() {
		panelSeleccion = new JPanel();
		combo = new JComboBox<>();
		for(Equipo e: equipos) {
			combo.addItem(e.getNombre());
		}
		JLabel label = new JLabel("Equipo: ");
		
		tabla = new JTable();
		tabla.setRowHeight(50);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollTabla = new JScrollPane(tabla);
		tabla.setModel(equipos[0].getModeloFinanzas());
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
				
				tabla.setModel(equipo.getModeloFinanzas());
				tabla.getColumnModel().getColumn(0).setMinWidth(200);
			}
		});
	}
	
	private Equipo getEquipoSeleccionado(int n) {
		return equipos[n];
	}
}
