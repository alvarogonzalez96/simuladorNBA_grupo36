package presentacion;

import negocio.*;

import javax.swing.*;
import java.awt.event.*;

import javax.swing.border.EmptyBorder;

import java.awt.*;

@SuppressWarnings("serial")
public class PanelHome extends JPanel {

	private JPanel panelIzq, panelDer; 
	private JPanel panelDerArriba, panelDerAbajo;
	private JPanel panelDerAbajoDer;
	private JScrollPane sp;
	private JPanel panelBalance, panelBotones;
	
	private JButton botonDia, botonSem, botonMes;
	
	private JLabel balance;
	
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
		setListeners();
	}
	
	private void initComponentes() {
		initClasificacion();
		initBotones();
		initUltimos4();
		initBalance();
		initTabla();
	}
	
	private void setListeners() {
		botonDia.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(LigaManager.fase) {
				case 0: //temp regular
					((JButton) (e.getSource())).setEnabled(false);
					LigaManager.simularDia();
					break;
				}
				((JButton) (e.getSource())).setEnabled(true);
				//actualizarPanel();
				Equipo eq = LigaManager.usuario.getEquipo();
				balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
				System.out.println(eq.getVictorias());
				repaint();
			}
		});
	}
	
	private void initTabla() {
		tabla = new JTable(LigaManager.usuario.getEquipo().getTableModel());
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.setFillsViewportHeight(true);
		tabla.setRowHeight(60);
		sp = new JScrollPane(tabla);
		sp.setPreferredSize(new Dimension(1000,100));
		panelDerAbajo.add(sp, BorderLayout.CENTER);
	}
	
	private void initBotones() {
		botonDia = new JButton("Simular partido");
		botonSem = new JButton("Simular semana");
		botonMes = new JButton("Simular mes");
		botonDia.setFont(new Font("Arial", Font.PLAIN, 15));
		botonMes.setFont(new Font("Arial", Font.PLAIN, 15));
		botonSem.setFont(new Font("Arial", Font.PLAIN, 15));
		
		panelBotones.add(botonDia);
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
		JLabel equipo;
		equipo = new JLabel("Mi equipo: "+LigaManager.usuario.getEquipo().getNombre());
		equipo.setFont(new Font("Arial", Font.PLAIN, 22));
		Equipo e = LigaManager.usuario.getEquipo();
		balance = new JLabel("Balance: "+e.getVictorias()+"-"+e.getDerrotas());
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
