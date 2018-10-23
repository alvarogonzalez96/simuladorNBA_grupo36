package presentacion;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class Inicio {	
		
	public Inicio() {
		
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {}
		
		ventanaInicio();
	}
	
	protected void ventanaInicio() {
		int respuesta = 2;
		String nombreUsuario = "*";
		String contrasenya = null;

		respuesta = JOptionPane.showConfirmDialog(null, "Bienvenido a Simulador NBA, ¿ya tienes una cuenta?");
		/*
		 * respuesta = 0 --> si
		 * respuesta = 1 --> no
		 * respuesta = 2 --> cancelar
		 */
		if(respuesta == 0) {
			//Introduce su nombre de usuario
			nombreUsuario = popup("Introduce tu nombre de usuario");
			if(nombreUsuario == null) {
				// volver a la ventana inicial
				ventanaInicio();
			} else {
				//Selecciona la contrasenya
				contrasenya = popup("Introduce tu contraseña");
				if(contrasenya == null) {
					//volver a la ventana inicial
					ventanaInicio();
				} else {
					//Hacer un if para comprobar con la BD
					new VentanaPrincipal();					
				}
			}

		} else if(respuesta == 1) {
			//Selecciona su nombre de usuario
			nombreUsuario = popup("Elige tu nombre de usuario");
			if(nombreUsuario == null) {
				ventanaInicio();
			} else {
				//Selecciona la contrasenya
				contrasenya = popup("Elige tu contraseña");
				if(contrasenya == null) {
					ventanaInicio();
				} else {
					//String de ejemplo
					String equipos[] = {"Los Angeles Lakers", "Golden State Warrios", "Boston Celtics", "New York Knicks"};
					//Selecciona el equipo con el que va a jugar
					String res = (String) JOptionPane.showInputDialog(null, "Selecciona tu equipo", "Simulador NBA", JOptionPane.DEFAULT_OPTION, null, equipos, equipos[0]);
					if(res == null) {
						ventanaInicio();
					} else {
						new VentanaPrincipal();						
					}
				}
			}
		} else {
			salir();
		}
	}
	
	private String popup(String mensaje) {
		String campo = "";
		do {
			campo = (String) JOptionPane.showInputDialog(null, mensaje, null);
		} while(campo != null && campo.isEmpty());
		return campo;
	}
	
	private void salir() {
		System.exit(0);
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Inicio();	
			}
		});

	}

}
