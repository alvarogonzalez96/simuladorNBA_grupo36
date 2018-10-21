package presentacion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;



import javax.swing.UIManager.*;//Importar para poder usar nimbus look&Feel

public class ventanaPrincipal extends JFrame {

	

	private JTextField texto;
	private JButton simPartido, simSemana, simMes;
	protected JMenuBar barra;
	protected JTabbedPane tabbedPane;
	protected JScrollPane scroll;
	
	
	public ventanaPrincipal() {
		
		
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
		
		
		
		
		//Paneles Principales, ordenados--------------------
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
		
		
		
		//Paneles Secundarios HOME---------------------------------
		
		
		JPanel panelBalance = new JPanel();
		JPanel panelTitulares = new JPanel();
		JPanel panelUltimosResultados = new JPanel();
		JPanel panelCalendario = new JPanel();
		
				
		
		
		//--------------------------------------------------------------
		
		
		
		
		
		
		//Creamos las pesta�as-----------------------------------
		
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
		
		JPanel agencialibre = new JPanel();
		tabbedPane.addTab("Agencia libre", null, agencialibre, null);
		agencialibre.setLayout(new GridLayout(3,1));
		
		JPanel historialLiga = new JPanel();
		tabbedPane.addTab("Historial Liga", null, historialLiga, null);
		historialLiga.setLayout(new GridLayout(3,1));
		
		
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
		
		
		
		
// Aqui creamos los botones para las simulaciones-----------------------------------------------------------------
		simPartido = new JButton ("Simular Partido");
		simSemana = new JButton ("Simular Semana");
		simMes = new JButton ("Simular Mes");
		
//----------------------------------------------------------------------------
		
		
		
//Rellenamos los paneles para ver si funciona
	
		
		
		
		
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
	
		
		
		
		
		
		

	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	this.setTitle("NBA");
	this.pack();
	this.setVisible(true);
}

public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
			new ventanaPrincipal();
		}
	});
}

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

