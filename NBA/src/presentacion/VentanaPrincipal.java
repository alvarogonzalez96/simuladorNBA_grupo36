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
	
	JMenu menuUsuario;
	
	protected JMenuItem itemInfo;
	protected JMenuItem itemLogout;
	protected JMenuItem itemAcercaDe;
	
	PanelHome home;
	PanelHistorial historialLiga;
	PanelLideres lideres;
	
	public VentanaPrincipal() {
		LigaManager.inicializar(true, new Usuario("Pedro", 18, 0));
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
		menuUsuario = new JMenu("Usuario");
		barra.add(menuUsuario);
		this.setJMenuBar(barra);
		
		
//Creamos las pestanias-----------------------------------		
		home = new PanelHome();
		tabbedPane.addTab("Home", null, home, null);

		JPanel calendario = new PanelCalendario();
		tabbedPane.addTab("Calendario", null, calendario, null);
		
		JPanel plantilla = new PanelPlantilla();
		tabbedPane.addTab("Plantilla", null, plantilla, null);
		
		JPanel finanzas = new PanelFinanzas();
		tabbedPane.addTab("Finanzas", null, finanzas, null);
		
		JPanel traspasos = new PanelTraspasos();
		tabbedPane.addTab("Traspasos", null, traspasos, null);
		
		JPanel agencialibre = new PanelAgenciaLibre();
		tabbedPane.addTab("Agencia libre", null, agencialibre, null);
		
		historialLiga = new PanelHistorial();
		tabbedPane.addTab("Historial Liga", null, historialLiga, null);
		
		JPanel clasificacion = new PanelClasificacion();
		tabbedPane.addTab("Clasificacion", null, clasificacion, null);

		lideres = new PanelLideres();
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
	
		
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {				
				if(tabbedPane.getSelectedIndex() == 0) {
					//se ha seleccionado la pestanya Home 
					home.seleccionado();
				} else if(tabbedPane.getSelectedIndex() == 6) {
					historialLiga.seleccionado();
				} else if(tabbedPane.getSelectedIndex() == 8) {
					lideres.seleccionado();
				}
			}
		});
		
		initBarra();
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("NBA");
		this.pack();
		this.setVisible(true);
	}
	
	private void initBarra() {
		itemInfo = new JMenuItem("Info");
		itemLogout = new JMenuItem("Cerrar sesion");
		itemAcercaDe = new JMenuItem("Acerca de...");
		
		menuUsuario.add(itemInfo);
		menuUsuario.add(itemAcercaDe);
		menuUsuario.addSeparator();
		menuUsuario.add(itemLogout);
		
		itemInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//mostrar ventana con informacion del jugador y estadisticas
			}
		});
		
		itemAcercaDe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//mostrar ventana con informacion sobre el desarrollo del juego
			}
		});
		
		itemLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//cerrar sesion, cerrar la ventana principal y mostrar la ventana de inicio
			}
		});
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