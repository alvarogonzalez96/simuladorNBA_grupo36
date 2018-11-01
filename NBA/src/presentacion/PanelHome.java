package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

@SuppressWarnings("serial")
public class PanelHome extends JPanel {

	private JPanel panelIzq, panelDer; 
	private JPanel panelDerArriba, panelDerAbajo;
	private JPanel panelDerAbajoDer;
	private JScrollPane sp;
	private JPanel panelBalance, panelBotones;
	
	private JButton botonPart, botonSem, botonMes;
	
	private JList<String> clasificacion;
	private DefaultListModel<String> modeloClasif;
	
	private JList<String> ultimos4;
	private DefaultListModel<String> modeloUltimos4;
	
	private JTable tabla;
	
	public PanelHome() {
		super();
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10,10,10,10));
		crearPanel();
		initComponentes();
	}
	
	private void initComponentes() {
		initClasificacion();
		initBotones();
		initUltimos4();
		initBalance();
		initTabla();
	}
	
	private void initTabla() {
		String[] columnas = {"Nombre","Posición","Overall","Rebote","Asistencia","Cosa 1", "Cosa 2", "Cosa 3"};
		String[][] datos = {
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
				{"Stephen Curry", "Base", "92", "76", "91", "89", "43", "99"},
		};
		tabla = new JTable(datos, columnas); 
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.setFillsViewportHeight(true);
		tabla.setRowHeight(60);
		sp = new JScrollPane(tabla);
		sp.setPreferredSize(new Dimension(1000,100));
		panelDerAbajo.add(sp, BorderLayout.CENTER);
	}
	
	private void initBotones() {
		botonPart = new JButton("Simular partido");
		botonSem = new JButton("Simular semana");
		botonMes = new JButton("Simular mes");
		botonPart.setFont(new Font("Arial", Font.PLAIN, 15));
		botonMes.setFont(new Font("Arial", Font.PLAIN, 15));
		botonSem.setFont(new Font("Arial", Font.PLAIN, 15));
		
		panelBotones.add(botonPart);
		panelBotones.add(botonSem);
		panelBotones.add(botonMes);
		
		panelBotones.setBorder(new EmptyBorder(0,0,7,0));
	}
	
	private void initUltimos4() {
		modeloUltimos4 = new DefaultListModel<>();
		for(int i = 0; i < 10; i++) {			
			modeloUltimos4.addElement("Derrota vs Houston (113-121)");
		}
		ultimos4 = new JList<>(modeloUltimos4);
		ultimos4.setFont(new Font("Arial", Font.PLAIN, 15));
		panelDerAbajoDer.add(ultimos4);
	}
	
	private void initBalance() {
		JLabel equipo, balance;
		equipo = new JLabel("Mi equipo: Philadelphia 76ers");
		equipo.setFont(new Font("Arial", Font.PLAIN, 22));
		balance = new JLabel("Balance: 69-13");
		balance.setFont(new Font("Arial", Font.PLAIN, 22));
		panelBalance.add(equipo);
		panelBalance.add(balance);
	}
	
	private void initClasificacion() {
		modeloClasif = new DefaultListModel<>();
		for(int i = 0; i < 30; i++) {			
			modeloClasif.addElement("1 Golden State Warriors 73-9");
		}
		clasificacion = new JList<>(modeloClasif);
		clasificacion.setFont(new Font("Arial", Font.PLAIN, 15));
		panelIzq.add(clasificacion);
	}
	
	private void crearPanel() {
		panelIzq = new JPanel(new BorderLayout());
		panelDer = new JPanel(new BorderLayout());
		this.add(panelIzq, BorderLayout.WEST);
		this.add(panelDer, BorderLayout.CENTER);
		panelDer.setPreferredSize(this.getPreferredSize());
		
		panelDerArriba = new JPanel(new BorderLayout());
		panelDerAbajo = new JPanel(new BorderLayout());
		panelDer.add(panelDerArriba, BorderLayout.NORTH);
		panelDer.add(panelDerAbajo, BorderLayout.CENTER);
		
		panelBalance = new JPanel(new GridLayout(2,1));
		panelBalance.setBorder(new EmptyBorder(10,10,10,10));
		panelBotones = new JPanel(new GridLayout(3,1));
		panelDerArriba.add(panelBalance, BorderLayout.CENTER);
		panelDerArriba.add(panelBotones, BorderLayout.EAST);
		
		panelDerAbajoDer = new JPanel(new BorderLayout());
		panelDerAbajo.add(panelDerAbajoDer, BorderLayout.EAST);
	}	
}
