package presentacion;

import java.awt.*;
import javax.swing.*;

import negocio.Jugador;

public class VentanaEstadisticas extends JFrame {

	JLabel nombreJugador;
	JTable tablaEstadisticas;
	
	public VentanaEstadisticas() {
		
	}
	
	public VentanaEstadisticas(Jugador j) {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		nombreJugador = new JLabel(j.getNombre());
		nombreJugador.setSize(new Dimension(100, 100));
		
		
		cp.add(nombreJugador, BorderLayout.NORTH);
		
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setTitle("Estadisticas");
	}
	
	public static void main(String[] args) {
		 Jugador a = new Jugador();
		 a.setNombre("Pablo");
		 SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new VentanaEstadisticas(a);
			}
		});
	}
}
