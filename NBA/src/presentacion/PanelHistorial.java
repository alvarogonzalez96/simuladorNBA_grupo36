package presentacion;

import javax.swing.*;

import negocio.LigaManager;
import negocio.Temporada;

import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class PanelHistorial extends PanelTab {

	private JPanel panelSeleccion, areaDatos;
	private JPanel datosIzq;
	
	private JLabel labelCampeon, labelMVP, labelROY, labelSextoHombre, labelDPOY;
	private JComboBox<Integer> comboAnyo;
	
	private int ultimoAnyo;
	
	public PanelHistorial() {
		super();
		ultimoAnyo = 2018;
	}
	
	@Override
	protected void crearPaneles() {
		panelSeleccion = new JPanel();
		areaDatos = new JPanel(new BorderLayout());
		
		add(panelSeleccion, BorderLayout.NORTH);
		add(areaDatos, BorderLayout.CENTER);
		
		datosIzq = new JPanel(new GridLayout(5,1));
		
		areaDatos.add(datosIzq, BorderLayout.CENTER);
	}
	
	@Override
	protected void initComponentes() {
		comboAnyo = new JComboBox<>();
		comboAnyo.addItem(2018);
		panelSeleccion.add(comboAnyo);
		
		labelCampeon = new JLabel();
		labelMVP = new JLabel();
		labelROY = new JLabel();
		labelSextoHombre = new JLabel();
		labelDPOY = new JLabel();
		
		datosIzq.add(labelCampeon);
		datosIzq.add(labelMVP);
		datosIzq.add(labelROY);
		datosIzq.add(labelSextoHombre);
		datosIzq.add(labelDPOY);
		
	}
	
	@Override
	protected void seleccionado() {
		int sel = (int) comboAnyo.getSelectedItem();
		while(LigaManager.anyo > ultimoAnyo) {
			ultimoAnyo++;
			comboAnyo.addItem(ultimoAnyo);
		}
		comboAnyo.setSelectedItem(sel);
		
		if(sel >= 2018 && (sel < LigaManager.anyo || (sel == LigaManager.anyo && LigaManager.fase > 1))) {
			Temporada temp = LigaManager.temporadasPasadas.get(sel);
			labelCampeon.setText("Campeón: "+temp.nombreCampeon);
			labelMVP.setText("MVP: "+temp.mvp);
			labelROY.setText("ROY: "+temp.roy);
			labelSextoHombre.setText("Sexto hombre: "+temp.sextoHombre);
			labelDPOY.setText("DPOY: "+temp.dpoy);
		} else {
			labelCampeon.setText("Campeón:");
			labelMVP.setText("MVP:");
			labelROY.setText("ROY:");
			labelSextoHombre.setText("Sexto Hombre:");
			labelDPOY.setText("DPOY:");
		}
		
		repaint();
	}

	@Override
	protected void setListeners() {		
		comboAnyo.addActionListener(
				(ActionEvent e) -> {
					seleccionado();
				});
	}
	
}
