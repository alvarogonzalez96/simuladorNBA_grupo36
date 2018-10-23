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
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		
		int respuesta = 2;
		String nombreUsuario = "*";
		String contrasenya = null;
		
		do {
			respuesta = JOptionPane.showConfirmDialog(null, "Bienvenido a Simulador NBA, ¿ya tienes una cuenta?");
			/*
			 * respuesta = 2 -> cancelar
			 * respuesta = 1 -> no
			 * respuesta = 0 -> sí
			 */
			if(respuesta == 0) {
				//Introduce su nombre de usuario
				do {
					nombreUsuario = (String)JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario", null);
					System.out.println(nombreUsuario);
				} while(nombreUsuario == null || nombreUsuario == "");
				
				//Selecciona la contrasenya
				do {
					contrasenya = (String)JOptionPane.showInputDialog(null, "Introduce tu contraseña", null);
				} while(contrasenya == null || contrasenya == "");
				
				//Hacer un if para comprobar con la BD
				new VentanaPrincipal();
				
			} else if(respuesta == 1) {
				//Selecciona su nombre de usuario
				do {
					nombreUsuario = (String)JOptionPane.showInputDialog(null, "Elige tu nombre de usuario", null);
					System.out.println(nombreUsuario);
				} while(nombreUsuario == null);
			
				//Selecciona la contrasenya
				do {
					contrasenya = (String)JOptionPane.showInputDialog(null, "Elige tu contraseña", null);
				} while(contrasenya == null);
				//String de ejemplo
				String equipos[] = {"Los Angeles Lakers", "Golden State Warrios", "Boston Celtics", "New York Knicks"};
				//Selecciona el equipo con el que va a jugar
				String res = (String)JOptionPane.showInputDialog(null, "Selecciona tu equipo", "Simulador NBA", JOptionPane.DEFAULT_OPTION, null, equipos, equipos[0]);
				new VentanaPrincipal();
			}
		} while(respuesta == 2);
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
