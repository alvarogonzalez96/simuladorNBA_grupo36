package presentacion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import datos.BD;

import negocio.Usuario;

public class Inicio {	
		
	private static String nombreUsuarioAnterior;
	private String nombreUsuarioIntroducido;

	static Properties prop;
	
	static void cargaUltimoNombreUsuario() {
		prop = new Properties();
		try {
			File f = new File("prop.properties");
			if(!f.exists()) f.createNewFile();
			InputStream is = new FileInputStream("prop.properties");
			prop.load(is);
			nombreUsuarioAnterior = prop.getProperty("usuario.nombre");
			if(nombreUsuarioAnterior == null) nombreUsuarioAnterior = "";
		} catch (IOException e) {
			// No hacer nada, no se carga el nombre y listo
			e.printStackTrace();
		}
	}
	
	public Inicio() {
		cargaUltimoNombreUsuario();
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
			nombreUsuario = popup("Introduce tu nombre de usuario", true);
			if(nombreUsuario == null) {
				// volver a la ventana inicial
				ventanaInicio();
			} else {
				//Selecciona la contrasenya
				contrasenya = popup("Introduce tu contraseña", false);
				if(contrasenya == null) {
					//volver a la ventana inicial
					ventanaInicio();
				} else {
					//Hacer un if para comprobar con la BD
					int g = BD.login(nombreUsuario, contrasenya);
					if(g >= 0) {
						//todo correcto
						nombreUsuarioIntroducido = nombreUsuario;
						recordarUsuario();
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
			nombreUsuario = popup("Elige tu nombre de usuario", false);
			if(nombreUsuario == null) {
				ventanaInicio();
			} else {
				//Selecciona la contrasenya
				contrasenya = popup("Elige tu contraseña", false);
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
	
	private String popup(String mensaje, boolean loginNombre) {
		String campo = "";
		do {
			if(loginNombre) {				
				campo = (String) JOptionPane.showInputDialog(null, mensaje, nombreUsuarioAnterior);
			} else {
				campo = (String) JOptionPane.showInputDialog(null, mensaje, "");
			}
		} while(campo != null && campo.isEmpty());
		return campo;
	}
	
	private void salir() {
		System.exit(0);
	}
	
	private int calcularIDEquipo(String equipo) {
		return 0;
	}
	
	// Guarda en el archivo properties el nombre de usuario
	private void recordarUsuario() {
		prop.put("usuario.nombre", nombreUsuarioIntroducido);
		try {
			prop.store(new FileWriter("prop.properties"), ""+new Date());
		} catch (IOException e) {
			e.printStackTrace();
		}
		nombreUsuarioAnterior = nombreUsuarioIntroducido;
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
