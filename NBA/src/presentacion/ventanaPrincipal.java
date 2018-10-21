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
		cp.setLayout(new BorderLayout());
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		

		JMenuBar barra = new JMenuBar();
		JMenu menuDatos = new JMenu("Jugador");
		barra.add(menuDatos);
		this.setJMenuBar(barra);
		
		
		
		
		//Paneles Principales, ordenados--------------------
		JPanel panelSuperior = new JPanel();
		JPanel panelMedio = new JPanel();
		JPanel panelInferior = new JPanel ();
		//---------------------------------------------------
		
		
		
		//Paneles Secundarios HOME---------------------------------
		
		
		JPanel panelBalance = new JPanel();
		JPanel panelTitulares = new JPanel();
		JPanel panel2 = new JPanel();
		
		//--------------------------------------------------------------
		
		
		
		
		
		
		//Creamos las pesta�as-----------------------------------
		
		JPanel home = new JPanel();
		tabbedPane.addTab("Home", null, home, null);
		home.setLayout(new GridLayout(3,1));
		
		
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

