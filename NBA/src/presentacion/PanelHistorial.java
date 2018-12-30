package presentacion;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import negocio.LigaManager;
import negocio.Temporada;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelHistorial extends PanelTab {

	private JPanel panelSeleccion, areaDatos;
	private JPanel datosIzq;
	private JScrollPane datosDer;
	
	private JLabel labelCampeon, labelMVP, labelROY, labelSextoHombre, labelDPOY;
	private JTable clasificacion;
	private JComboBox<Integer> comboAnyo;
	
	private int ultimoAnyo;
	
	private ModeloTablaClasificacionHistorial modelo;
	
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
		
		clasificacion = new JTable();
		datosDer = new JScrollPane(clasificacion);
		datosDer.getViewport().setBackground(Color.WHITE);
		
		areaDatos.add(datosIzq, BorderLayout.CENTER);
		areaDatos.add(datosDer, BorderLayout.EAST);
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
			labelCampeon.setText("Campeon: "+temp.nombreCampeon);
			labelMVP.setText("MVP: "+temp.mvp);
			labelROY.setText("ROY: "+temp.roy);
			labelSextoHombre.setText("Sexto hombre: "+temp.sextoHombre);
			labelDPOY.setText("DPOY: "+temp.dpoy);
			
			clasificacion.setModel(getModelo(true));
		} else {
			labelCampeon.setText("Campeon:");
			labelMVP.setText("MVP:");
			labelROY.setText("ROY:");
			labelSextoHombre.setText("Sexto Hombre:");
			labelDPOY.setText("DPOY:");
			
			clasificacion.setModel(getModelo(false));
		}
		
		repaint();
	}

	@Override
	protected void setListeners() {
		comboAnyo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionado();
			}
		});
	}
	
	private ModeloTablaClasificacionHistorial getModelo(boolean anyoValido) {
		if(anyoValido) {
			Temporada temp = LigaManager.temporadasPasadas.get((int) comboAnyo.getSelectedItem());
			modelo = new ModeloTablaClasificacionHistorial(temp.nombresEquipos, temp.victorias, temp.derrotas);
		} else {
			String[] n = new String[30];
			int[] v, d;
			v = d = new int[30];
			for(int i = 0; i < 30; i++) {
				n[i] = "---";
				v[i] = d[i] = 0;
 			}
			modelo = new ModeloTablaClasificacionHistorial(n,v,d);
		}
		
		return modelo;
	}
	
	private class ModeloTablaClasificacionHistorial implements TableModel {

		private String[] nombres;
		private int[] victorias, derrotas;
		
		public ModeloTablaClasificacionHistorial(String[] n, int[] v, int[] d) {
			super();
			nombres = n;
			victorias = v;
			derrotas = d;
		}
		
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
			case 0:
				return "Equipo";
			case 1:
				return "Victorias";
			case 2:
				return "Derrotas";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return 30;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0:
				return nombres[rowIndex];
			case 1:
				return victorias[rowIndex];
			case 2:
				return derrotas[rowIndex];
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
