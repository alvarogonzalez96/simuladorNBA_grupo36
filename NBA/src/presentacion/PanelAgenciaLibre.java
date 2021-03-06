package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import negocio.*;

@SuppressWarnings("serial")
public class PanelAgenciaLibre extends PanelTab {
	
	private JPanel panelSeleccion;
	private JScrollPane scrollTabla;
	private JTable tabla;
	private JLabel labelSalarioTotal;
	
	public PanelAgenciaLibre() {
		super();
	}
	
	@Override
	protected void crearPaneles() {
		panelSeleccion = new JPanel();
		labelSalarioTotal = new JLabel("Salario total de mi equipo: "+LigaManager.usuario.getEquipo().calcSalarioTotal());
		labelSalarioTotal.setFont(new Font("Arial", Font.PLAIN, 20));
		
		tabla = new JTable();
		tabla.setRowHeight(50);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		scrollTabla = new JScrollPane(tabla);
		tabla.setModel(LigaManager.getModeloTablaAgenciaLibre());
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
		scrollTabla.getViewport().setBackground(Color.WHITE);
		
		panelSeleccion.add(labelSalarioTotal);
		add(panelSeleccion, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);
	}

	@Override
	protected void initComponentes() {
		
	}

	@Override
	protected void setListeners() {
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

	@Override
	protected void seleccionado() {
		labelSalarioTotal.setText("Salario total de mi equipo: "+LigaManager.usuario.getEquipo().calcSalarioTotal());
		tabla.repaint();
		repaint();
	}
}
