package presentacion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
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
	
	
	public VentanaPrincipal() {
		//Nimbus Look&Feel
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		Container cp = this.getContentPane();
		cp.setLayout(new GridLayout());

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
		
		//---------------------------------------------------
		
		//Paneles HISTORIAL Principales, ordenados--------------------
		
		JPanel panelIzquierdoHistorial = new JPanel( new BorderLayout());
		JPanel panelIzquierdoSuperiorHistorial = new JPanel();
		JPanel panelIzquierdoInferiorHistorial = new JPanel( );
		
		JPanel panelDerechoHistorial = new JPanel ( new BorderLayout());
			
		//--------------------------------------------------------------
		
		//Paneles Noticiario Principales, ordenados--------------------
		
		JPanel panelNoticiario = new JPanel();
		
		//--------------------
		
		//Creamos las pestanias-----------------------------------
		JPanel home = new JPanel();
		tabbedPane.addTab("Home", null, home, null);
		home.setLayout(new GridLayout(2,1));

		JPanel calendario = new JPanel();
		tabbedPane.addTab("Calendario", null, calendario, null);
		calendario.setLayout(new GridLayout(3,1));

		JPanel plantilla = new JPanel();
		tabbedPane.addTab("Plantilla", null, plantilla, null);
		plantilla.setLayout(new GridLayout(3,1));

		JPanel finanzas = new JPanel();
		tabbedPane.addTab("Finanzas", null, finanzas, null);
		finanzas.setLayout(new GridLayout(3,1));

		JPanel traspasos = new JPanel();
		tabbedPane.addTab("Traspasos", null, traspasos, null);
		traspasos.setLayout(new GridLayout(3,1));
		
		JPanel agencialibre = new JPanel();
		tabbedPane.addTab("Agencia libre", null, agencialibre, null);
		agencialibre.setLayout(new GridLayout(3,1));

		JPanel historialLiga = new JPanel();
		tabbedPane.addTab("Historial Liga", null, historialLiga, null);
		historialLiga.setLayout(new  GridLayout(1,2));

		JPanel clasificacion = new JPanel();
		tabbedPane.addTab("Clasificacion", null, clasificacion, null);
		clasificacion.setLayout(new GridLayout(3,1));

		JPanel lideres = new JPanel();
		tabbedPane.addTab("Lideres de la liga", null, lideres, null);
		lideres.setLayout(new GridLayout(3,1));

		JPanel noticiario = new JPanel();
		tabbedPane.addTab("Noiticiario", null, noticiario, null);
		noticiario.setLayout(new GridLayout(3,1));

		JPanel playoffs = new JPanel();
		tabbedPane.addTab("Play Offs", null, playoffs, null);
		playoffs.setLayout(new GridLayout(3,1));

		
		//Tree------------------------------
		
		
		
		
		
		
		
		
		
	
		/*getContentPane().setLayout(new BorderLayout());
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
		
		//create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);
         
        //create the tree by passing in the root node
        tree = new JTree(root);
        add(tree);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
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
		home.add(panelInferior);
		
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
		
		noticiarioTexto = new JScrollPane();
		noticiarioTexto.setPreferredSize(new Dimension(600,800));
		noticiarioTexto.setBorder((new TitledBorder("Noticias")));
		
		panelNoticiario.add(noticiarioTexto);
		noticiario.add(panelNoticiario);
		
		//--------------------------------------------------------
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
