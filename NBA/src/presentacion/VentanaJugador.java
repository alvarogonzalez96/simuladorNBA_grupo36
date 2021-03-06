package presentacion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import negocio.Equipo;
import negocio.Jugador;
import negocio.LigaManager;

@SuppressWarnings("serial")
public class VentanaJugador extends JFrame {
	
	Jugador jugador;
	
	JLabel nombre;
	JTable tablaEstadisticas;
	JScrollPane scrollTabla;
	
	JButton botonFichar, botonRenovar, botonCortar;
	
	JPanel panelTitulo, panelCentral, panelInferior; //arriba, centro, abajo
	JPanel panelBotones; //dentro de panelInferior, a la izquierda
	
	JTextField inputCantidad;
	JSlider sliderAnyos;
	
	PanelTab panelTab;
	
	private static ArrayList<VentanaJugador> ventanasAbiertas;
	static {
		ventanasAbiertas = new ArrayList<>();
	}
	
	public VentanaJugador(PanelTab panelTab, Jugador j) {
		this.panelTab = panelTab;
		Container cp = getContentPane();
		this.jugador = j;
		cp.setLayout(new BorderLayout());
		
		initComponentes();
		
		ventanasAbiertas.add(this);
		
		panelTitulo = new JPanel();
		panelInferior = new JPanel(new BorderLayout());
		panelBotones = new JPanel(new FlowLayout());
		
		panelTitulo.add(nombre);
		
		setListeners();
		addBotones();
		
		add(panelTitulo, BorderLayout.NORTH);
		add(scrollTabla, BorderLayout.CENTER);
		add(panelInferior, BorderLayout.SOUTH);
		panelInferior.add(panelBotones, BorderLayout.WEST);
		
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
		this.setMinimumSize(this.getSize());
		this.setTitle(jugador.getNombre());
	}
	
	/**
	 * Metodo que cierra todas las ventanas de jugador abiertas.
	 * Esto es necesario para evitar que el usuario realice
	 * acciones 'ilegales' con respecto a las renovaciones/despidos/etc.
	 * 
	 * Ejemplo: dejar una ventana de jugador abierta indefinidamente. En un
	 * momento dado, ese jugador puede que se vaya a otro equipo. Como
	 * la ventana de jugador se abrió cuando dicho jugador seguía en el equipo
	 * del usuario, el botón de renovar seguirá mostrándose, por lo que el usuario
	 * podría hacer que el juego no funcionara de forma correcta.
	 * */
	public static void cerrarVentanasAbiertas() {
		for(VentanaJugador v: ventanasAbiertas) {
			v.dispose();
		}
		ventanasAbiertas.clear();
	}
	
	private void addBotones() {
		if(jugador.getTid() == -1) {
			//agente libre
			panelBotones.add(botonFichar);
			panelBotones.add(new JLabel("Cantidad: "));
			panelBotones.add(inputCantidad);
			panelBotones.add(new JLabel("Años: "));
			panelBotones.add(sliderAnyos);
		} else {
			if(jugador.getTid() == LigaManager.usuario.getEquipo().getTid()) {
				//jugador del equipo del usuario
				panelBotones.add(botonCortar);
				if(jugador.getAnyosContratoRestantes() == 0) {
					panelBotones.add(botonRenovar);
					panelBotones.add(new JLabel("Cantidad: "));
					panelBotones.add(inputCantidad);
					panelBotones.add(new JLabel("Años: "));
					panelBotones.add(sliderAnyos);
				}
			}
		}
	}
	
	private void alertaDraft() {
		JOptionPane.showMessageDialog(null, "No puedes realizar ninguna gestión hasta que haya concluido el draft", "Aviso", JOptionPane.WARNING_MESSAGE);
	}
	
	private void setListeners() {
		botonFichar.addActionListener(
				(ActionEvent e) -> {
					//los playoffs están en curso, no se puede hacer nada
					if(LigaManager.draftEnCurso) {
						alertaDraft();
						return;
					}
					
					//si no es posible el fichaje -> mensaje de error
					//si es posible -> confirmación -> si confirma, recomendar que reasigne roles
					int cantidad, anyos;
					try {
						cantidad = Integer.parseInt(inputCantidad.getText());
						anyos = sliderAnyos.getValue();
						int of = aceptaOferta(jugador, cantidad, anyos);
						if(of == 1) {
							int opcion = JOptionPane.showConfirmDialog(null, jugador.getNombre()+" ha aceptado tu oferta. ¿Deseas ficharlo?");
							if(opcion == JOptionPane.YES_OPTION) {
								fichar(jugador, cantidad, anyos);
								JOptionPane.showMessageDialog(null, "Has fichado a "+jugador.getNombre(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
								dispose();
							}
						} else if (of == 0) {
							JOptionPane.showMessageDialog(null, jugador.getNombre()+" ha rechazado tu oferta", "Oferta rechazada", JOptionPane.INFORMATION_MESSAGE);
						} else if(of == -1){
							JOptionPane.showMessageDialog(null, "No puedes realizar este fichaje porque ya tienes 15 jugadores, el máximo establecido por la liga", "Aviso", JOptionPane.WARNING_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(null, "Como tu equipo se pasa del límite salarial, solo puedes fichar a jugadores por el mínimo (1000)", "Aviso", JOptionPane.WARNING_MESSAGE);
						}
					} catch(NumberFormatException ex) {
						JOptionPane.showMessageDialog(null, "Error, la cantidad introducida debe ser un número entero", "Error", JOptionPane.ERROR_MESSAGE);
					}
				});
		
		botonRenovar.addActionListener(
				(ActionEvent e) -> {
					//los playoffs están en curso no se puede hacer nada
					if(LigaManager.draftEnCurso) {
						alertaDraft();
						return;
					}
					
					//abrir ventana para renovar -> elección de cantidad y años de contrato
					int cantidad, anyos;
					try {
						cantidad = Integer.parseInt(inputCantidad.getText());
						anyos = sliderAnyos.getValue();
						int of = proponerRenovacion(jugador, cantidad, anyos);
						if(of == 1) {
							int opcion = JOptionPane.showConfirmDialog(null, jugador.getNombre()+" ha aceptado tu oferta. ¿Deseas renovarlo?");
							if(opcion == JOptionPane.YES_OPTION) {
								renovar(jugador, cantidad, anyos);
								JOptionPane.showMessageDialog(null, "Has renovado a "+jugador.getNombre()+" por "+anyos+" temporadas ("+cantidad+"/temporada)", "Aviso", JOptionPane.INFORMATION_MESSAGE);
								dispose();
							}
						} else {
							JOptionPane.showMessageDialog(null, jugador.getNombre()+" ha rechazado tu oferta", "Oferta rechazada", JOptionPane.INFORMATION_MESSAGE);
						} 
					} catch(NumberFormatException ex) {
						JOptionPane.showMessageDialog(null, "Error, la cantidad introducida debe ser un número entero", "Error", JOptionPane.ERROR_MESSAGE);
					}
				});
		
		botonCortar.addActionListener(
				(ActionEvent e) -> {
					//los playoffs están en curso, no se puede hacer nada
					if(LigaManager.draftEnCurso) {
						alertaDraft();
						return;
					}
					
					//lanzar mensaje de confirmación, y si confirma, cortar el contrato
					if(esPosibleCortar(jugador)) {
						int opcion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres cortar a "+jugador.getNombre()+"?");
						if(opcion == JOptionPane.YES_OPTION) {
							//cortar y cerrar la ventana
							cortar(jugador);
							dispose();
						}
					} else {
						if(LigaManager.fase <= 1) { // no han pasado los playoffs
							JOptionPane.showConfirmDialog(null, "No puedes cortar a "+jugador.getNombre()+", te faltarían jugadores en la posición de "+jugador.getPosicion(), "Aviso", JOptionPane.WARNING_MESSAGE);
							return;
						}
						// han pasado los playoffs
						int res = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres cortar a "+jugador.getNombre()+"?"
								+ " Te faltarían jugadores en la posición de "+jugador.getPosicion(), "Aviso", JOptionPane.WARNING_MESSAGE);
						if(res == JOptionPane.YES_OPTION) {
							cortar(jugador);
							dispose();
						}
					}
				});
	}
	
	/**
	 * 1 -> todo en orden y oferta aceptada
	 * 0 -> oferta rechazada
	 * -1-> ya hay 15 jugadores en el equipo
	 * -2-> se pasa del límite y no ofrece el mínimo
	 * */
	private int aceptaOferta(Jugador j, int cantidad, int anyos) {
		Equipo e = LigaManager.usuario.getEquipo();
		if(e.getJugadores().size() >= 15) {
			return -1;
		} else if(e.calcSalarioTotal() + cantidad > Equipo.limiteSalarial && cantidad != 1000){
			return -2;
		} else {
			double rand = Math.random();
			double randAnyos = Math.random()*5;
			if(j.getOverall() >= 85 && cantidad >= 30000 + rand*10001  && anyos >= randAnyos) {
				return 1;
			} 
			if(j.getOverall() >= 80 && j.getOverall() < 85 && cantidad >= 20000 + rand*10001 && anyos >= randAnyos) {
				return 1;
			} 
			if(j.getOverall() >= 70 && j.getOverall() < 80 && cantidad >= 10000+rand*10001 && anyos >= randAnyos-1) {
				return 1;
			} 
			if(j.getOverall() >= 65 && j.getOverall() < 70 && cantidad >= 1000 + rand*12001) {
				return 1;
			}
			if(j.getOverall() >= 60 && j.getOverall() <  65 && cantidad >= 1000 + rand*4001) {
				return 1;
			}
			if(j.getOverall() < 60 && cantidad >= 1000) {
				return 1;
			}
			return 0;
		}
	}
	
	/**
	 * 1 -> todo en orden y oferta aceptada
	 * 0 -> oferta rechazada
	 * */
	private int proponerRenovacion(Jugador j, int cantidad, int anyos) {
		double rand = Math.random();
		double randAnyos = Math.random()*5;
		if(j.getOverall() >= 85 && cantidad >= 30000 + rand*10001  && anyos >= randAnyos) {
			return 1;
		} 
		if(j.getOverall() >= 80 && j.getOverall() < 85 && cantidad >= 20000 + rand*10001 && anyos >= randAnyos) {
			return 1;
		} 
		if(j.getOverall() >= 70 && j.getOverall() < 80 && cantidad >= 10000+rand*10001 && anyos >= randAnyos-1) {
			return 1;
		} 
		if(j.getOverall() >= 65 && j.getOverall() < 70 && cantidad >= 1000 + rand*12001) {
			return 1;
		}
		if(j.getOverall() >= 60 && j.getOverall() <  65 && cantidad >= 1000 + rand*4001) {
			return 1;
		}
		if(j.getOverall() < 60 && cantidad >= 1000) {
			return 1;
		} 		
		return 0;
	}
	
	private void fichar(Jugador j, int cantidad, int anyos) {
		Equipo equipo = LigaManager.usuario.getEquipo();
		j.setTid(equipo.getTid());
		j.salario = cantidad;
		j.anyosContratoRestantes = anyos;
		equipo.getJugadores().add(j);
		LigaManager.agentesLibres.remove(j);
		LigaManager.actualizarSalarios();
		equipo.ordenarJugadores();
		equipo.asignarRoles();
	}
	
	private void renovar(Jugador j, int cantidad, int anyos) {
		Equipo equipo = LigaManager.usuario.getEquipo();
		j.anyosContratoRestantes = anyos;
		j.salario = cantidad;
		equipo.calcSalarioTotal();
	}
	
	private void cortar(Jugador j) {
		j.setTid(-1);
		j.anyosContratoRestantes = 0;
		j.salario = 0;
		j.rol = null;
		Equipo e = LigaManager.usuario.getEquipo();
		for(int i = e.getJugadores().size()-1; i >= 0; i--) {
			if(e.getJugadores().get(i).getNombre().equals(jugador.getNombre())) {
				e.getJugadores().remove(i);
				break;
			}
		}
		e.asignarRoles();
		LigaManager.agentesLibres.add(jugador);
	}
	
	private boolean esPosibleCortar(Jugador jug) {
		int pos = 0;
		Equipo e = LigaManager.usuario.getEquipo();
		for(Jugador j: e.getJugadores()) {
			if(j.getPosicion() == jug.getPosicion() && !j.getNombre().equals(jug.getNombre())) {
				pos++;
			}
		}
		return pos >= 2;
	}
	
	private void initComponentes() {
		nombre = new JLabel(jugador.getNombre());
		nombre.setSize(new Dimension(100, 100));
		nombre.setFont(new Font("Arial", Font.PLAIN, 20));
		
		botonFichar = new JButton("Fichar");
		botonRenovar = new JButton("Renovar");
		botonCortar = new JButton("Cortar");
		
		inputCantidad = new JTextField(10);
		sliderAnyos = new JSlider(1,6,3);
		sliderAnyos.setSnapToTicks(true);
		sliderAnyos.setPaintTicks(true);
		sliderAnyos.setMajorTickSpacing(1);
		sliderAnyos.setPaintLabels(true);
		
		tablaEstadisticas = new JTable();
		tablaEstadisticas.setDefaultRenderer(String.class, new RendererTablaEstadisticas());
		scrollTabla = new JScrollPane(tablaEstadisticas);
		tablaEstadisticas.setModel(jugador.getModeloTablaTemporadas());
		scrollTabla.getViewport().setBackground(Color.WHITE);
	}
	
	@Override
	public void dispose() {
		panelTab.seleccionado();
		super.dispose();
	}
}

class RendererTablaEstadisticas implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable tabla, Object value, boolean isSelected, boolean hasFocus, int row,
			int col) {
		return new JLabel(value+"", SwingConstants.CENTER);
	}
	
}