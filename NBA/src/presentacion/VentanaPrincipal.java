package presentacion;

import negocio.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
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