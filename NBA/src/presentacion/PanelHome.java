package presentacion;

import negocio.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import datos.BD;

import java.awt.*;

@SuppressWarnings("serial")
public class PanelHome extends PanelTab {

	private JPanel panelIzq, panelDer; 
	private JPanel panelDerArriba, panelDerAbajo;
	private JPanel panelDerAbajoDer;
	private JScrollPane sp;
	private JPanel panelBalance, panelBotones;
	
	private JButton botonDia, botonSem, botonMes;
	
	private JLabel balance;
	private JLabel temporada;
	
	private JTable clasificacion;
	
	private JTable ultimosPartidos;
	
	private JTable tabla;
	
	public PanelHome() {
		super();
	}
	
	@Override
	protected void initComponentes() {
		initClasificacion();
		initBotones();
		initUltimosPartidos();
		initBalance();
		initPlantilla();
	}
	
	@Override
	protected void setListeners() {
		botonDia.addActionListener(
				(ActionEvent e) -> {
					if(LigaManager.draftEnCurso) {
						JOptionPane.showMessageDialog(null, "No puedes comenzar una nueva temporada hasta que haya terminado el draft y hayas realizado las gestiones necesarias", "Aviso", JOptionPane.WARNING_MESSAGE);
						return;
					}
					if(LigaManager.finTemporada) {
						int op = LigaManager.comprobarEquipoUsuario();
						if(op == 0) {
							//que el resto de equipos terminen sus gestiones
							ArrayList<Equipo> orden = LigaManager.clasificaciones.get("GENERAL").getEquipos();
							LigaManager.renovaciones(orden, false);
							LigaManager.despedirJugadores(orden, false);
							LigaManager.agenciaLibre(orden, false);
							
							//guardar en BD
							LigaManager.guardarBD();
							
							botonDia.setText("Simular dia");
							botonDia.setEnabled(true);
							botonSem.setEnabled(true);
							botonMes.setEnabled(true);
							LigaManager.finTemporada = false;
							LigaManager.reset();
							clasificacion.setModel(LigaManager.clasificaciones.get("GENERAL").getTableModel());
							temporada.setText(""+LigaManager.anyo+"/"+(LigaManager.anyo+1));
							repaint();
						} else if(op == -2) {
							JOptionPane.showMessageDialog(null, "Antes de continuar, tienes que asegurarte de que tu plantilla no tenga mas de 15 jugadores", "Aviso", JOptionPane.WARNING_MESSAGE);
						} else if(op == -3) {
							JOptionPane.showMessageDialog(null, "Antes de continuar, tienes que asegurarte de que tengas, por lo menos, por cada posicion, dos jugadores", "Aviso", JOptionPane.WARNING_MESSAGE);
						} else if(op == -4) {
							JOptionPane.showMessageDialog(null, "Antes de continuar, tienes que decidir que hacer con los jugadores con 0 anyos de contrato: renovar o cortar?", "Aviso", JOptionPane.WARNING_MESSAGE);
						}
						return;
					}
					boolean fin = false;
					switch(LigaManager.fase) {
					case 0: //temp regular
						botonDia.setEnabled(false);
						LigaManager.simularDia();
						if(LigaManager.fase == 1) {
							JOptionPane.showMessageDialog(null, "Fin de la temporada regular.\nVe a la pesta�a de playoffs.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

							botonDia.setEnabled(false);
							botonSem.setEnabled(false);
							botonMes.setEnabled(false);
							LigaManager.elegirPremiosIndividuales();
							fin = true;
						}
					}
					if(!fin) {
						botonDia.setEnabled(true);
					}
					//actualizarPanel();
					Equipo eq = LigaManager.usuario.getEquipo();
					balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
					repaint();
				});
		
		botonSem.addActionListener(
				(ActionEvent e) -> {
					botonSem.setEnabled(false);
					boolean fin = false;;
					for(int i = 0; i < 7; i++) {
						LigaManager.simularDia();
						if(LigaManager.fase == 1) {
							JOptionPane.showMessageDialog(null, "Fin de la temporada regular.\n Ve a la pesta�a de playoffs.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
							botonDia.setEnabled(false);
							botonSem.setEnabled(false);
							botonMes.setEnabled(false);
							fin = true;
							LigaManager.elegirPremiosIndividuales();
							break;
						}
					}
					if(!fin) {
						botonSem.setEnabled(true);
					}
					Equipo eq = LigaManager.usuario.getEquipo();
					balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
					repaint();
					System.out.println(LigaManager.clasificaciones.get("GENERAL").get(4).getAbrev());
				});
		
		botonMes.addActionListener(
				(ActionEvent e) -> {
					botonMes.setEnabled(true);
					boolean fin = false;
					for(int i = 0; i < 30; i++) {
						LigaManager.simularDia();
						if(LigaManager.fase == 1) {
							JOptionPane.showMessageDialog(null, "Fin de la temporada regular.\n Ve a la pestanya de playoffs.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
							botonDia.setEnabled(false);
							botonSem.setEnabled(false);
							botonMes.setEnabled(false);
							fin = true;
							LigaManager.elegirPremiosIndividuales();
							break;
						}
					}
					if(!fin) {
						botonMes.setEnabled(true);
					}
					Equipo eq = LigaManager.usuario.getEquipo();
					balance.setText("Balance: "+eq.getVictorias()+"-"+eq.getDerrotas());
					repaint();
				} );
		
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
	
	private void initPlantilla() {
		tabla = new JTable(LigaManager.usuario.getEquipo().getTableModel());
		tabla.getColumnModel().getColumn(0).setMinWidth(200);
		tabla.setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 20));
		tabla.setFillsViewportHeight(true);
		tabla.setRowHeight(60);
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		s.getViewport().setBackground(Color.WHITE);
		panelDerAbajoDer.add(s);
	}
	
	private void initBalance() {
		JLabel equipo;
		equipo = new JLabel("Mi equipo: "+LigaManager.usuario.getEquipo().getNombre());
		equipo.setFont(new Font("Arial", Font.PLAIN, 22));
		Equipo e = LigaManager.usuario.getEquipo();
		balance = new JLabel("Balance: "+e.getVictorias()+"-"+e.getDerrotas());
		balance.setFont(new Font("Arial", Font.PLAIN, 22));
		
		int anyo = LigaManager.anyo;
		temporada = new JLabel("Temporada "+anyo+"/"+(anyo+1));
		temporada.setFont(new Font("Arial", Font.PLAIN, 22));
		
		panelBalance.add(temporada);
		panelBalance.add(equipo);
		panelBalance.add(balance);
	}
	
	private void initClasificacion() {
		clasificacion = new JTable(LigaManager.clasificaciones.get("GENERAL").getTableModel());
		clasificacion.setFont(new Font("Arial", Font.PLAIN, 20));
		clasificacion.setRowHeight(25);
		JScrollPane scrollIzq = new JScrollPane(clasificacion);
		scrollIzq.setPreferredSize(new Dimension(250,getHeight()));
		scrollIzq.getViewport().setBackground(Color.WHITE);
		panelIzq.add(scrollIzq, BorderLayout.WEST);
	}
	
	@Override
	protected void crearPaneles() {
		panelIzq = new JPanel(new BorderLayout());
		panelDer = new JPanel(new BorderLayout());
		this.add(panelIzq, BorderLayout.WEST);
		this.add(panelDer, BorderLayout.CENTER);
		panelDer.setPreferredSize(this.getPreferredSize());
		
		panelDerArriba = new JPanel(new BorderLayout());
		panelDerAbajo = new JPanel(new BorderLayout());
		panelDer.add(panelDerArriba, BorderLayout.NORTH);
		panelDer.add(panelDerAbajo, BorderLayout.CENTER);
		
		panelBalance = new JPanel(new GridLayout(3,1));
		panelBalance.setBorder(new EmptyBorder(10,10,10,10));
		panelBotones = new JPanel(new GridLayout(3,1));
		panelDerArriba.add(panelBalance, BorderLayout.CENTER);
		panelDerArriba.add(panelBotones, BorderLayout.EAST);
		
		panelDerAbajoDer = new JPanel(new BorderLayout());
		panelDerAbajo.add(panelDerAbajoDer, BorderLayout.EAST);
	}
	
	@Override
	public void seleccionado() {
		if(LigaManager.finTemporada) {
			botonDia.setText("Comenzar temporada");
			botonDia.setEnabled(true);
			botonSem.setEnabled(false);
			botonMes.setEnabled(false);
		} else {
			if(LigaManager.fase == 0) {
				//temporada regular
				botonDia.setEnabled(true);
				botonSem.setEnabled(true);
				botonMes.setEnabled(true);
			}
			System.out.println(LigaManager.clasificaciones.get("GENERAL").get(0).getNombre());
			//clasificacion.setModel(LigaManager.clasificaciones.get("GENERAL").getTableModel());
			temporada.setText("Temporada "+LigaManager.anyo+"/"+(LigaManager.anyo+1));
		}
		repaint();
	}
}
