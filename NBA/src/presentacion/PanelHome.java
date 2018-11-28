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
	
	private JTable clasificacion;
	
	/*
	private JList<String> ultimosPartidos;
	private DefaultListModel<String> modeloUltimos4;*/
	private JTable ultimosPartidos;
	
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
		initUltimosPartidos();
		initBalance();
		initPlantilla();
	}
	
	private void setListeners() {
		botonDia.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(LigaManager.fase) {
				case 0: //temp regular
					botonDia.setEnabled(false);
					LigaManager.simularDia();
					break;
				}
				botonDia.setEnabled(true);
				//actualizarPanel();
				Equipo eq = LigaManager.usuario.getEquipo();
				balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
				repaint();
			}
		});
		
		botonSem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				botonSem.setEnabled(false);
				boolean b;
				for(int i = 0; i < 7; i++) {
					b = LigaManager.simularDia();
					if(b) break;
				}
				botonSem.setEnabled(true);
				Equipo eq = LigaManager.usuario.getEquipo();
				balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
				repaint();
			}
		});
		
		botonMes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				botonMes.setEnabled(true);
				boolean b;
				for(int i = 0; i < 30; i++) {
					b = LigaManager.simularDia();
					if(b) break;
				}
				botonMes.setEnabled(true);
				Equipo eq = LigaManager.usuario.getEquipo();
				balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
				repaint();
			}
		});
	}
	
	private void initPlantilla() {
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
	
	private void initUltimosPartidos() {
		ultimosPartidos = new JTable(LigaManager.calendario.getModelo());
		ultimosPartidos.setFont(new Font("Arial", Font.PLAIN, 15));
		ultimosPartidos.setRowHeight(25);
		//panelDerAbajoDer.add(new JScrollPane(ultimosPartidos));
		JScrollPane s = new JScrollPane(ultimosPartidos);
		s.setPreferredSize(new Dimension(200, getHeight()));
		panelDerAbajoDer.add(s);
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
		clasificacion = new JTable(LigaManager.clasificaciones.get("GENERAL").getTableModel());
		clasificacion.setFont(new Font("Arial", Font.PLAIN, 20));
		clasificacion.setRowHeight(25);
		JScrollPane scrollIzq = new JScrollPane(clasificacion);
		scrollIzq.setPreferredSize(new Dimension(250,getHeight()));
		panelIzq.add(scrollIzq, BorderLayout.WEST);
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
