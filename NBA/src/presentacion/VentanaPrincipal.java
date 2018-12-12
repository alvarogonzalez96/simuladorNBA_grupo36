package presentacion;

import negocio.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.UIManager.*;//Importar para poder usar nimbus look&Feel

public class VentanaPrincipal extends JFrame {
	
	private JTextField texto;
	private JButton simPartido, simSemana, simMes;
	protected JMenuBar barra;
	protected JTabbedPane tabbedPane;
	protected JScrollPane scroll, noticiarioTexto;
	protected JComboBox historial;
	private JTree tree;
	
	PanelHome home;
	
	public VentanaPrincipal() {
		LigaManager.inicializar(true, new Usuario("Pedro", 18, 1));
		//Nimbus Look&Feel
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {}

		Container cp = this.getContentPane();
		cp.setLayout(new GridLayout());
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JMenuBar barra = new JMenuBar();
		JMenu menuDatos = new JMenu("Jugador");
		barra.add(menuDatos);
		this.setJMenuBar(barra);

		//Paneles HOME Principales, ordenados--------------------
		JPanel panelSuperior = new JPanel(new BorderLayout());
		JPanel panelInferior = new JPanel (new BorderLayout() );

		JPanel panelInferiorInferior = new JPanel ();
		JPanel panelInferiorSuperior = new JPanel ();

		JPanel panelSuperiorIzquierda = new JPanel( new FlowLayout());
		JPanel panelSuperiorDerecha = new JPanel(new GridLayout(2,1) );

		JPanel panelSuperiorDerechaSuperior= new JPanel( new BorderLayout());
		JPanel panelSuperiorDerechaInferior= new JPanel(new BorderLayout());
		JPanel panelSuperiorDerechaSuperior2= new JPanel( new GridLayout(3,1));
		
	
		
//Paneles HISTORIAL Principales, ordenados--------------------
		
		JPanel panelIzquierdoHistorial = new JPanel( new BorderLayout());
		JPanel panelIzquierdoSuperiorHistorial = new JPanel();
		JPanel panelIzquierdoInferiorHistorial = new JPanel( );
		
		JPanel panelDerechoHistorial = new JPanel ( new BorderLayout());
			
	
		
		//
		//Panel Agencia Libre-------------------------------------------------------------------

		/*JPanel panelAgencia= new JPanel();
		panelAgencia.setLayout(new GridLayout(1,1));
		
		*/
		
		
		
		
//Panel TRASPASOS-------------------------------------------------------------------

		JPanel trasPanel = new JPanel();
		trasPanel.setLayout(new GridLayout(3, 3));
		JPanel trasArribaIzq = new JPanel(new FlowLayout());
		JPanel trasArribaDcha = new JPanel(new FlowLayout());
		JPanel trasCentroIzq = new JPanel(new BorderLayout());
		JPanel trasCentroDcha = new JPanel(new FlowLayout());
		JPanel trasAbajoIzq = new JPanel(new GridLayout(3, 1));
		JPanel trasAbajoDcha = new JPanel(new FlowLayout());

//Paneles Noticiario Principales, ordenados--------------------
		
		JPanel panelNoticiario = new JPanel(new FlowLayout());
		
//Creamos las pestanias-----------------------------------		
		home = new PanelHome();
		tabbedPane.addTab("Home", null, home, null);

		JPanel calendario = new PanelCalendario();
		tabbedPane.addTab("Calendario", null, calendario, null);
		
		JPanel plantilla = new PanelPlantilla();
		tabbedPane.addTab("Plantilla", null, plantilla, null);
		
		JPanel finanzas = new PanelFinanzas();
		tabbedPane.addTab("Finanzas", null, finanzas, null);
		
		JPanel traspasos = new JPanel();
		tabbedPane.addTab("Traspasos", null, traspasos, null);
		
		JPanel agencialibre = new PanelAgenciaLibre();
		tabbedPane.addTab("Agencia libre", null, agencialibre, null);
		
		JPanel historialLiga = new JPanel();
		tabbedPane.addTab("Historial Liga", null, historialLiga, null);
		
		JPanel clasificacion = new PanelClasificacion();
		tabbedPane.addTab("Clasificacion", null, clasificacion, null);

		JPanel lideres = new JPanel();
		tabbedPane.addTab("Lideres de la liga", null, lideres, null);
		
		JPanel noticiario = new PanelNoticiario();
		tabbedPane.addTab("Noticiario", null, noticiario, null);
		
		JPanel playoffs = new PanelPlayoffs();
		tabbedPane.addTab("Play Offs", null, playoffs, null);
		
		
//Tree------------------------------
		
	/*	
	
		getContentPane().setLayout(new BorderLayout());
		tree = new JTree();
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				 if (node != null) {
					 if (node.isLeaf()) {
						 switch(node.getUserObject().toString().toUpperCase()){
						 	case "HOME":
						 		tabbedPane.setSelectedIndex(0);
						 		break;
						 	case "CALENDARIO":
						 		tabbedPane.setSelectedIndex(1);
						 		break;
						 	case "Plantilla":
						 		tabbedPane.setSelectedIndex(2);
						 		break;
						 	case "Finanzas":
						 		tabbedPane.setSelectedIndex(3);
						 		break;
						 	case "Traspasos":
						 		tabbedPane.setSelectedIndex(4);
						 		break;	
						 	case "Agencia libre":
						 		tabbedPane.setSelectedIndex(5);
						 		break;	
						 	case "Historial Liga":
						 		tabbedPane.setSelectedIndex(6);
						 		break;	
						 	case "Clasificacion":
						 		tabbedPane.setSelectedIndex(7);
						 		break;	
						 	case "Lideres de la liga":
						 		tabbedPane.setSelectedIndex(8);
						 		break;	
						 	case "Noiticiario":
						 		tabbedPane.setSelectedIndex(9);
						 		break;	
						 	case "Play Offs":
						 		tabbedPane.setSelectedIndex(10);
						 		break;	
						 }
						 						 
					 }
				 }
			}
		});
		DefaultMutableTreeNode node_0 = new DefaultMutableTreeNode("Gestión Equipo");
		DefaultMutableTreeNode node_1 = new DefaultMutableTreeNode("Informacion");
		node_1.add(new DefaultMutableTreeNode("Plantilla"));
		node_1.add(new DefaultMutableTreeNode("Traspasos"));
		node_1.add(new DefaultMutableTreeNode("Agencia libre"));
		node_1.add(new DefaultMutableTreeNode("Finanzas"));
		node_0.add(node_1);
		DefaultMutableTreeNode node_2 = new DefaultMutableTreeNode("Gesti\u00F3n");
		node_2.add(new DefaultMutableTreeNode("Consultas"));
		node_0.add(node_2);
		DefaultTreeModel dtmRaiz = new DefaultTreeModel(node_0);
		tree.setModel(dtmRaiz);
		//tree.setBounds(0, 0, 300, 745);
		getContentPane().add(tree, BorderLayout.WEST);
		
		
		*/
	
		
		//----------------------------------
// Aqui creamos los botones para las simulaciones-----------------------------------------------------------------
		simPartido = new JButton ("Simular Partido");
		simSemana = new JButton ("Simular Semana");
		simMes = new JButton ("Simular Mes");
	
		//----------------------------------------------------------------------------
	
//ComboBox---------------------------------
		historial = new JComboBox();
		historial.addItem("Liga");
		historial.addItem("MVP");
		historial.addItem("ROY");
		historial.setSize(new Dimension(800,600));
			
		//----------------------------------------------
		
//Rellenamos los paneles HOME-----------------------------------
		/*
		JScrollPane scrollBalance = new JScrollPane();
		scrollBalance.setPreferredSize(new Dimension(500, 100));
		scrollBalance.setBorder((new TitledBorder("Balance")));
		panelSuperiorDerechaSuperior.add(scrollBalance, BorderLayout.WEST);
		panelSuperiorDerechaSuperior.add(panelSuperiorDerechaSuperior2, BorderLayout.EAST);
		panelSuperiorDerecha.add(panelSuperiorDerechaSuperior);
		panelSuperior.add(panelSuperiorDerecha, BorderLayout.EAST);

		JScrollPane scrollQuinteto = new JScrollPane();
		scrollQuinteto.setPreferredSize(new Dimension(400, 500));
		scrollQuinteto.setBorder((new TitledBorder("Quinteto")));
		panelSuperiorDerechaInferior.add(scrollQuinteto, BorderLayout.WEST);
		panelSuperiorDerecha.add(panelSuperiorDerechaInferior);
		panelSuperior.add(panelSuperiorDerecha, BorderLayout.EAST);

		JScrollPane scrollResultados= new JScrollPane();
		scrollResultados.setPreferredSize(new Dimension(200, 800));
		scrollResultados.setBorder((new TitledBorder("Resultados")));
		panelSuperiorDerechaInferior.add(scrollResultados, BorderLayout.EAST);
		panelSuperiorDerecha.add(panelSuperiorDerechaInferior);
		panelSuperior.add(panelSuperiorDerecha, BorderLayout.EAST);

		JScrollPane scrollClasificacion = new JScrollPane();

		scrollClasificacion.setPreferredSize(new Dimension(200, 400));
		scrollClasificacion.setBorder((new TitledBorder("Clasificacion")));
		panelSuperiorIzquierda.add(scrollClasificacion);
		panelSuperior.add(panelSuperiorIzquierda, BorderLayout.WEST);

		JScrollPane scrollCalendario= new JScrollPane();
		scrollCalendario.setPreferredSize(new Dimension(600, 100));
		scrollCalendario.setBorder((new TitledBorder("Calendario")));
		panelInferiorInferior.add(scrollCalendario);
		panelInferior.add(panelInferiorInferior, BorderLayout.SOUTH);

		panelSuperiorDerechaSuperior2.add(simPartido);
		panelSuperiorDerechaSuperior2.add(simSemana);
		panelSuperiorDerechaSuperior2.add(simMes);
		panelSuperiorDerechaSuperior.add(panelSuperiorDerechaSuperior2, BorderLayout.EAST);

		home.add(panelSuperior);
		home.add(panelInferior);*/
		
		
		//------------------------------------------------

//Rellenamos los paneles HISTORIAL-----------------------------------
		
		JScrollPane scrollHistorial = new JScrollPane();

		scrollHistorial.setPreferredSize(new Dimension(200, 400));
		scrollHistorial.setBorder((new TitledBorder("Historial")));
		scrollHistorial.setBackground(Color.WHITE );
		panelDerechoHistorial.add(scrollHistorial);
		panelIzquierdoSuperiorHistorial.add(historial);
		panelIzquierdoHistorial.add(panelIzquierdoSuperiorHistorial, BorderLayout.NORTH);
		panelIzquierdoHistorial.add(panelIzquierdoInferiorHistorial, BorderLayout.SOUTH);
		historialLiga.add(panelIzquierdoHistorial, BorderLayout.WEST);
		historialLiga.add(panelDerechoHistorial, BorderLayout.EAST);
		
		//---------------------------------------------------------------------
//Rellenamos el panel NOTICIARIO-----------------------------------
		
		/*noticiarioTexto = new JScrollPane();
		
		noticiarioTexto.setPreferredSize(new Dimension(700,700));
		noticiarioTexto.setBorder((new TitledBorder("Noticias")));
		
		panelNoticiario.add(noticiarioTexto);
		//noticiarioTexto.setViewportView(a);
		noticiario.add(panelNoticiario);*/
		
		//--------------------------------------------------------
		
//Rellennamos los paneles TRASPASOS-------------------------------------
		
		JButton aceptar = new JButton("Aceptar");
		aceptar.setPreferredSize(new Dimension(300, 50));
		aceptar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i =	JOptionPane.showConfirmDialog(rootPane, "Esta seguro que quiere descargar este archivo?", "Confirma", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION)
				{			
				
				}
			}
		});
		
		//
		JComboBox comboJugadores = new JComboBox();
		comboJugadores.setPreferredSize(new Dimension(300, 50));
		comboJugadores.addItem("Alvaro");
		comboJugadores.addItem("Pablo");
		comboJugadores.addItem("Goiri");
		
		
		JComboBox comboEquipos = new JComboBox();
		comboEquipos.setPreferredSize(new Dimension(300, 50));
		comboEquipos.addItem("Lakers");
		comboEquipos.addItem("Warriors");
		
		
		JComboBox comboNombres = new JComboBox();
		comboNombres.setPreferredSize(new Dimension(300, 50));
		comboNombres.addItem("Asier");
		comboNombres.addItem("Pablo");
		
		
		JScrollPane scrollTraspasos1 = new JScrollPane();
		scrollTraspasos1.setBorder((new TitledBorder("TRASPASOS1")));
		scrollTraspasos1.setPreferredSize(new Dimension(300, 300));
		
		JScrollPane scrollTraspasos2 = new JScrollPane();
		scrollTraspasos2.setBorder((new TitledBorder("TRASPASOS2")));
		scrollTraspasos2.setPreferredSize(new Dimension(300, 300));
		
		trasArribaIzq.add(comboJugadores);
		trasArribaDcha.add(scrollTraspasos1);
		JPanel apoyoAbajoIzq = new JPanel(new FlowLayout());
		JPanel apoyoAbajoIz = new JPanel(new FlowLayout());
		apoyoAbajoIzq.add(comboEquipos);
		apoyoAbajoIz.add(comboNombres);
		trasAbajoIzq.add(apoyoAbajoIzq, BorderLayout.NORTH);
		trasAbajoIzq.add(apoyoAbajoIz, BorderLayout.CENTER);
		trasCentroDcha.add(aceptar);
		trasAbajoDcha.add(scrollTraspasos2);
		
		trasPanel.add(trasArribaIzq);
		trasPanel.add(trasArribaDcha);
		trasPanel.add(trasCentroIzq);
		trasPanel.add(trasCentroDcha);
		trasPanel.add(trasAbajoIzq);
		trasPanel.add(trasAbajoDcha);
		
		traspasos.add(trasPanel);	
		//------------------------------------
		
		//Rellenamos panel Agencia Libre---------------

		/*JScrollPane scrollAgencia = new JScrollPane();
		scrollAgencia.setBorder((new TitledBorder("Historial")));
		scrollAgencia.setPreferredSize(new Dimension(1000,100));
		panelAgencia.add(scrollAgencia);
		agencialibre.add(panelAgencia);
		
		
		*/
		
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(tabbedPane.getSelectedIndex() == 0) {
					//se ha seleccionado la pestanya Home 
					home.seleccionado();
				}
			}
		});
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("NBA");
		this.pack();
		this.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new VentanaPrincipal();
			}
		});
	}

}