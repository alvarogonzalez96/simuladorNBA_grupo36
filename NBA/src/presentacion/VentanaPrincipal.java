package presentacion;

import negocio.*;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import datos.BD;

@SuppressWarnings("serial")
public class VentanaPrincipal extends JFrame {
	
	protected JMenuBar barra;
	protected JTabbedPane tabbedPane;
	protected JScrollPane scroll;
	
	JMenu menuUsuario;
	
	protected JMenuItem itemAyuda;
	protected JMenuItem itemLogout;
	protected JMenuItem itemAcercaDe;
	
	PanelHome home;
	PanelHistorial historialLiga;
	PanelLideres lideres;
	PanelFinanzas finanzas;
	PanelCalendario calendario;
	PanelClasificacion clasificacion;
	PanelPlayoffs playoffs;
	PanelPlantilla plantilla;
	
	private VentanaHTML ventanaAyuda, ventanaAcercaDe;
	
	public VentanaPrincipal(Usuario u, boolean desdeJSON) {
		LigaManager.inicializar(desdeJSON, u);
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
		
		ventanaAyuda = new VentanaHTML(VentanaHTML.AYUDA);
		ventanaAcercaDe = new VentanaHTML(VentanaHTML.ACERCA_DE);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JMenuBar barra = new JMenuBar();
		menuUsuario = new JMenu("Usuario");
		barra.add(menuUsuario);
		this.setJMenuBar(barra);
				
		home = new PanelHome();
		tabbedPane.addTab("Home", null, home, null);

		calendario = new PanelCalendario();
		tabbedPane.addTab("Calendario", null, calendario, null);
		
		plantilla = new PanelPlantilla();
		tabbedPane.addTab("Plantilla", null, plantilla, null);
		
		finanzas = new PanelFinanzas();
		tabbedPane.addTab("Finanzas", null, finanzas, null);
		
		JPanel traspasos = new PanelTraspasos();
		tabbedPane.addTab("Traspasos", null, traspasos, null);
		
		JPanel agencialibre = new PanelAgenciaLibre();
		tabbedPane.addTab("Agencia libre", null, agencialibre, null);
		
		historialLiga = new PanelHistorial();
		tabbedPane.addTab("Historial Liga", null, historialLiga, null);
		
		clasificacion = new PanelClasificacion();
		tabbedPane.addTab("Clasificación", null, clasificacion, null);

		lideres = new PanelLideres();
		tabbedPane.addTab("Líderes de la liga", null, lideres, null);
	
		playoffs = new PanelPlayoffs();
		tabbedPane.addTab("Play Offs", null, playoffs, null);	
		
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(tabbedPane.getSelectedComponent() instanceof PanelTab)
				((PanelTab) tabbedPane.getSelectedComponent()).seleccionado();
			}
		});
		
		initBarra();
		
		finanzas.seleccionarEquipo(LigaManager.usuario.getEquipo().getTid());
		plantilla.seleccionarEquipo(LigaManager.usuario.getEquipo().getTid());
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("NBA");
		this.pack();
		this.setVisible(true);
	}
	
	private void initBarra() {
		itemAyuda = new JMenuItem("Ayuda");
		itemLogout = new JMenuItem("Salir");
		itemAcercaDe = new JMenuItem("Acerca de...");
		
		menuUsuario.add(itemAyuda);
		menuUsuario.add(itemAcercaDe);
		menuUsuario.addSeparator();
		menuUsuario.add(itemLogout);
		
		itemAyuda.addActionListener(
				(ActionEvent e) -> {
					if(ventanaAyuda.isShowing()) {
						ventanaAyuda.setState(JFrame.NORMAL);
						ventanaAyuda.toFront();
					} else {
						ventanaAyuda.setVisible(true);
					}
				});
		
		itemAcercaDe.addActionListener(
				(ActionEvent e) -> {
					if(ventanaAcercaDe.isShowing()) {
						ventanaAcercaDe.setState(JFrame.NORMAL);
						ventanaAcercaDe.toFront();
					} else {
						ventanaAcercaDe.setVisible(true);
					}
				});
		
		itemLogout.addActionListener(
				(ActionEvent e) -> {
					//cerrar sesión, cerrar la ventana principal
					dispose();
				});
	}
	
	@Override
	public void dispose() {
		int c = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres salir? Perderás todo el progreso de la temporada actual.", "Confirmar", JOptionPane.WARNING_MESSAGE);
		if(c == JOptionPane.OK_OPTION) {
			BD.rollback();
			BD.desconectar();
			ventanaAyuda.dispose();
			ventanaAcercaDe.dispose();
			if(LigaManager.draftEnCurso) {
				playoffs.forzarCerrarVentanaDraft();
			}
			VentanaJugador.cerrarVentanasAbiertas();
			LigaManager.logger.log(Level.INFO, "Cierre de programa desde ventana principal");
			super.dispose();
		}
	}
}