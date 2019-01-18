package presentacion;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.event.*;

import datos.BD;
import negocio.Equipo;
import negocio.LigaManager;
import negocio.Usuario;

public class Inicio {	
		
	private static String nombreUsuarioAnterior;
	private static String nombreUsuarioIntroducido;

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
		
		BD.conectar();
		ventanaInicio();
	}
	
	protected void ventanaInicio() {
		int respuesta = 2;
		String nombreUsuario = "*";
		String contrasenya = null;

		respuesta = JOptionPane.showConfirmDialog(null, "Bienvenido a Simulador NBA, ¿ya tienes una cuenta?");
		/*
		 * respuesta = 0 --> sí
		 * respuesta = 1 --> no
		 * respuesta = 2 --> cancelar
		 */
		if(respuesta == 0) {
			//Introduce su nombre de usuario
			nombreUsuario = popup("Introduce tu nombre de usuario", true);
			if(nombreUsuario == null) {
				//Volver a la ventana inicial
				ventanaInicio();
			} else {
				//Selecciona la contraseña
				contrasenya = popupPassword("Introduce tu contraseña");
				if(contrasenya == null) {
					//Volver a la ventana inicial
					ventanaInicio();
				} else {
					//Hacer un if para comprobar con la BD
					int g = BD.login(nombreUsuario, contrasenya);
					if(g >= 0) {
						//todo correcto
						nombreUsuarioIntroducido = nombreUsuario;
						recordarUsuario();
						Usuario u = new Usuario(nombreUsuario, g);
						new VentanaPrincipal(u, false);
					} else if(g == -1) {
						//incorrecto
						JOptionPane.showMessageDialog(null, "Los datos introducidos no son correctos", "Error", JOptionPane.ERROR_MESSAGE);
						ventanaInicio();
					} else {
						//error interno
						JOptionPane.showMessageDialog(null, "Ha habido un error interno. Consulta el archivo log para más informacion", "Error", JOptionPane.ERROR_MESSAGE);
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
				contrasenya = popupPassword("Elige tu contraseña");
				if(contrasenya == null) {
					ventanaInicio();
				} else {
					String[] equipos = getNombresEquipos();
					//Selecciona el equipo con el que va a jugar
					String res = (String) JOptionPane.showInputDialog(null, "Selecciona tu equipo", "Simulador NBA", JOptionPane.DEFAULT_OPTION, null, equipos, equipos[0]);
					if(res == null) {
						ventanaInicio();
					} else {
						int teamID = calcularIDEquipo(res);
						int r = BD.registrar(nombreUsuario, contrasenya, teamID);
						if(r >= 0) {
							//todo bien
							nombreUsuarioIntroducido = nombreUsuario;
							recordarUsuario();
							VentanaEspera espera = new VentanaEspera();
							Usuario usuario = new Usuario(nombreUsuario, teamID);
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									VentanaPrincipal v = new VentanaPrincipal(usuario, true);
									espera.setAlwaysOnTop(false);
									espera.dispose();
									v.setAlwaysOnTop(true);
									//v.setVisible(true);
									v.toFront();
									v.setAlwaysOnTop(false);
								}
								
							});
							//espera.dispose();
						} else if(r == -1) {
							//alertar de que ese nombre de usuario ya existe, y volver a empezar
							JOptionPane.showMessageDialog(null, "Introduce un nombre de usuario que no esté en uso", "Error", JOptionPane.ERROR_MESSAGE);
							ventanaInicio();
						} else {
							//error en la base de datos, volver a empezar
							JOptionPane.showMessageDialog(null, "Ha habido un error interno. Consulta el archivo log para más información.\n"
									+ "Es posible que otro programa esté accediendo a la BD al mismo tiempo. Si es así, cierra ese programa y vuelve a intentarlo.", 
									"Error", JOptionPane.ERROR_MESSAGE);
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
	
	private String popupPassword(String mensaje) {
		PasswordPanel p = new PasswordPanel();
		JOptionPane op = new JOptionPane(p, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		JDialog dlg = op.createDialog(mensaje);

		dlg.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				p.ganaFocus();
			}
		});
		String password = "";
		do {
			dlg.setVisible(true);
		
			if (op.getValue() != null && op.getValue().equals(JOptionPane.OK_OPTION)) {
				password = new String(p.getPassword());
			} else if(op.getValue() == null) {
				dlg.dispose();
				return null;
			}
		} while(password != null && password.isEmpty());
		dlg.dispose();
		return password;
	}

	private void salir() {
		BD.desconectar();
	}
	
	private String[] getNombresEquipos() {
		String[] n = new String[30];
		for(int i = 0; i < LigaManager.equipos.length; i++) {
			n[i] = LigaManager.equipos[i].getNombre();
		}
		return n;
	}
	
	private int calcularIDEquipo(String equipo) {
		for(Equipo e: LigaManager.equipos) {
			if(e.getNombre().equalsIgnoreCase(equipo)) {
				return e.getTid();
			}
		}
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

@SuppressWarnings("serial")
class PasswordPanel extends JPanel {
	  private final JPasswordField passwordField = new JPasswordField(20);
	  private boolean ganaFocusAntes;

	  void ganaFocus() {
	    if (!ganaFocusAntes) {
	      ganaFocusAntes = true;
	      passwordField.requestFocusInWindow();
	    }
	  }

	  public PasswordPanel() {
	    super(new FlowLayout());
	    add(passwordField);
	  }

	  public char[] getPassword() {
	      return passwordField.getPassword();
	  }
	}
