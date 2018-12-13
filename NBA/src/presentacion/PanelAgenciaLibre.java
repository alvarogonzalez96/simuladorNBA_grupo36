package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import negocio.*;

public class PanelAgenciaLibre extends PanelTab {
private ArrayList<Jugador> jugadores;
	
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	
	public PanelAgenciaLibre() {
		super();
	}
	
	@Override
	protected void crearPaneles() {
		panelSeleccion = new JPanel();

		jugadores = LigaManager.agentesLibres;
		tabla = new JTable();
		tabla.setRowHeight(50);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollTabla = new JScrollPane(tabla);
		tabla.setModel(LigaManager.getModeloTablaAgenciaLibre());
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
		scrollTabla.getViewport().setBackground(Color.WHITE);
		
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);
	}

	@Override
	protected void initComponentes() {
		
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
