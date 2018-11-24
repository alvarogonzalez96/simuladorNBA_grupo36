package presentacion;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import datos.BD;

import negocio.Usuario;

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
		BD.conectar();
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
					int g = BD.login(nombreUsuario, contrasenya);
					if(g >= 0) {
						//todo correcto
						new VentanaPrincipal();
					} else if(g == -1) {
						//incorrecto
						JOptionPane.showMessageDialog(null, "Los datos introducidos no son correctos", "Error", JOptionPane.ERROR_MESSAGE);
						ventanaInicio();
					} else {
						//error interno
						JOptionPane.showMessageDialog(null, "Ha habido un error interno. Consulta el archivo log para mas informacion", "Error", JOptionPane.ERROR_MESSAGE);
					}
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
						//new VentanaPrincipal();	
						int teamID = calcularIDEquipo(res);
						int r = BD.registrar(nombreUsuario, contrasenya, teamID);
						if(r >= 0) {
							//todo bien
							Usuario usuario = new Usuario(nombreUsuario, r, teamID);
							new VentanaPrincipal();
						} else if(r == -1) {
							//alertar de que ese nombre de usuario ya existe, y volver a empezar
							JOptionPane.showMessageDialog(null, "Introduce un nombre de usuario que no este en uso", "Error", JOptionPane.ERROR_MESSAGE);
							ventanaInicio();
						} else {
							//error en la base de datos, volver a empezar
							JOptionPane.showMessageDialog(null, "Ha habido un error interno. Consulta el archivo log para mas informacion", "Error", JOptionPane.ERROR_MESSAGE);
							ventanaInicio();
						}
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
	
	private int calcularIDEquipo(String equipo) {
		return 0;
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
