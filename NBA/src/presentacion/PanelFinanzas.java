package presentacion;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import negocio.*;

@SuppressWarnings("serial")
public class PanelFinanzas extends PanelTab {

	private Equipo[] equipos;
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JComboBox<String> combo;
	private JLabel labelSalarioTotal;
	private JButton botonMiEquipo;
	
	private int indiceSeleccionado;
	
	public PanelFinanzas() {
		super();
		indiceSeleccionado = LigaManager.usuario.getEquipo().getTid();
	}
	
	@Override
	protected void crearPaneles() {
		equipos = LigaManager.equipos;
		panelSeleccion = new JPanel(new FlowLayout());
	}
	
	@Override
	protected void setListeners() {
		combo.addActionListener(
				(ActionEvent e) -> {
					Equipo equipo = getEquipoSeleccionado(combo.getSelectedIndex());
					indiceSeleccionado = combo.getSelectedIndex();
					tabla.setModel(equipo.getModeloFinanzas());
					tabla.getColumnModel().getColumn(0).setMinWidth(200);
					labelSalarioTotal.setText("Salario total: "+getEquipoSeleccionado(combo.getSelectedIndex()).calcSalarioTotal());
					repaint();
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
					seleccionarEquipo(LigaManager.usuario.getEquipo().getTid());
				});
	}
	
	private void lanzaVentanaJugador(Jugador j) {
		new VentanaJugador(this, j);
	}
	
	private Equipo getEquipoSeleccionado(int n) {
		return equipos[n];
	}

	@Override
	protected void initComponentes() {
		combo = new JComboBox<>();
		combo.setFont(new Font("Arial", Font.PLAIN, 15));
		for(Equipo e: equipos) {
			combo.addItem(e.getNombre());
		}
		JLabel label = new JLabel("Equipo: ");
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		
		botonMiEquipo = new JButton("Mi equipo");
		
		tabla = new JTable();
		tabla.setRowHeight(50);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollTabla = new JScrollPane(tabla);
		tabla.setModel(equipos[0].getModeloFinanzas());
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollTabla.getViewport().setBackground(Color.WHITE);
		
		labelSalarioTotal = new JLabel("Salario total: "+getEquipoSeleccionado(combo.getSelectedIndex()).calcSalarioTotal());
		JLabel labelLimite = new JLabel("Límite salarial: "+Equipo.limiteSalarial);
		labelSalarioTotal.setFont(new Font("Arial", Font.PLAIN, 20));
		labelLimite.setFont(new Font("Arial", Font.PLAIN, 20));
		panelSeleccion.add(labelLimite);
		panelSeleccion.add(labelSalarioTotal);
		
		panelSeleccion.add(label);
		panelSeleccion.add(combo);
		panelSeleccion.add(botonMiEquipo);
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);	
	}

	@Override
	protected void seleccionado() {
		tabla.setModel(equipos[combo.getSelectedIndex()].getModeloFinanzas());
		int n = indiceSeleccionado;
		n++;
		if(n >= 30) n -= 30;
		combo.setSelectedIndex(n);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {}
		if(n == 0) n = 30;
		else n--;
		indiceSeleccionado = n;
		combo.setSelectedIndex(indiceSeleccionado);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {}
		labelSalarioTotal.setText("Salario total: "+getEquipoSeleccionado(combo.getSelectedIndex()).calcSalarioTotal());
		tabla.repaint();
		repaint();
	}
	
	public void seleccionarEquipo(int n) {
		combo.setSelectedIndex(n);
	}
}
