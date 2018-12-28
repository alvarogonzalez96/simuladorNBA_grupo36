package presentacion;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import negocio.*;

public class PanelFinanzas extends PanelTab {

	private Equipo[] equipos;
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JComboBox<String> combo;
	private JLabel labelSalarioTotal;
	
	public PanelFinanzas() {
		super();
	}
	
	@Override
	protected void crearPaneles() {
		equipos = LigaManager.equipos;
		panelSeleccion = new JPanel(new FlowLayout());
	}
	
	@Override
	protected void setListeners() {
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Equipo equipo = getEquipoSeleccionado(combo.getSelectedIndex());
				
				tabla.setModel(equipo.getModeloFinanzas());
				tabla.getColumnModel().getColumn(0).setMinWidth(200);
				seleccionado();
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
	
	private Equipo getEquipoSeleccionado(int n) {
		return equipos[n];
	}

	@Override
	protected void initComponentes() {
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
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollTabla.getViewport().setBackground(Color.WHITE);
		
		labelSalarioTotal = new JLabel("Salario total: "+getEquipoSeleccionado(combo.getSelectedIndex()).calcSalarioTotal());
		JLabel labelLimite = new JLabel("Limite salarial: "+Equipo.limiteSalarial);
		panelSeleccion.add(labelLimite);
		panelSeleccion.add(labelSalarioTotal);
		
		panelSeleccion.add(label);
		panelSeleccion.add(combo);
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);	
	}

	@Override
	protected void seleccionado() {
		labelSalarioTotal.setText("Salario total: "+getEquipoSeleccionado(combo.getSelectedIndex()).calcSalarioTotal());
		repaint();
	}
}
