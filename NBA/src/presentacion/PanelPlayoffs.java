package presentacion;
import negocio.*;
import javax.swing.*;

import negocio.LigaManager;
import negocio.Playoffs.Series;
import negocio.Playoffs.SeriePlayoffs;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class PanelPlayoffs extends PanelTab {

	private JPanel panelBotones;
	private AreaCuadroPlayoffs cuadro;
	
	private JButton botonPartido, botonPlayoffs;
	
	private VentanaDraft ventanaDraft;
	
	public PanelPlayoffs() {
		super();
	}
	
	@Override
	protected void setListeners() {
		botonPartido.addActionListener(
				(ActionEvent e) -> {
					if(LigaManager.fase == 1) {
						LigaManager.playoffs.avanzar();
						
						//repintar cuadro de playoffs y comprobar si ha terminado la temporada
						repaint();
						if(LigaManager.playoffs.getCampeon() != null) {
							finPlayoffs();
						}
					}
				});
		
		botonPlayoffs.addActionListener(
				(ActionEvent e) -> {
					if(LigaManager.fase == 1) {
						while(LigaManager.playoffs.getCampeon() == null) {
							LigaManager.playoffs.avanzar();
							repaint();
						}
						finPlayoffs();
					}
				});
	}

	private void finPlayoffs() {
		LigaManager.finTemporada = true;
		botonPartido.setEnabled(false);
		botonPlayoffs.setEnabled(false);
		//la fase de LigaManager la incrementa la propia clase playoffs
		JOptionPane.showMessageDialog(null, "Campeon: "+LigaManager.campeon.getNombre(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
		LigaManager.guardarDatosFinTemporada();
		ArrayList<Jugador> retiradosUsuario = LigaManager.jubilar();
		avisarJubilados(retiradosUsuario);
		LigaManager.actualizarAnyosContrato();
		for(Equipo e: LigaManager.equipos) {
			for(int i = e.getJugadores().size()-1; i >= 0; i--) {
				Jugador j = e.getJugadores().get(i);
				if(j.getAnyosContratoRestantes() < 0) {
					LigaManager.agentesLibres.add(j);
					j.setTid(-1);
					j.salario = 0;
					j.anyosContratoRestantes = 0;
					e.calcSalarioTotal();
					e.getJugadores().remove(i);
				}
			}
		}
		
		ArrayList<Jugador> draft = LigaManager.prepararDraft();
		LigaManager.draftEnCurso = true;
		ventanaDraft = new VentanaDraft(this, draft); //se realiza la seleccion de los 60 jugadores (2 rondas)
	}
	
	public void forzarCerrarVentanaDraft() {
		ventanaDraft.cerrarForzoso();
	}
	
	/**
	 * Metodo que sera llamado unicamente desde la ventana de draft,
	 * al ser cerrada. Este metodo dara comienzo a las
	 * renovaciones, fichajes de agencia libre y los despidos
	 * de los equipos, as� como la seleccion de los roles.
	 * */
	public void comenzarFaseGestiones() {
		LigaManager.terminarDraft();
		
		//el orden en el que se realizaran las gestiones sera el de la clasificacion
		ArrayList<Equipo> orden = LigaManager.clasificaciones.get("GENERAL").getEquipos();
		
		LigaManager.renovaciones(orden, true);
		LigaManager.agenciaLibre(orden, true);
		LigaManager.despedirJugadores(orden, true);
		//roles
	}
	
	@Override
	protected void initComponentes() {
		botonPlayoffs = new JButton("Simular playoffs");
		botonPartido = new JButton("Simular partido");
		panelBotones.add(botonPartido);
		panelBotones.add(botonPlayoffs);
		
		add(panelBotones, BorderLayout.NORTH);
		add(cuadro, BorderLayout.CENTER);
	}

	@Override
	protected void crearPaneles() {
		panelBotones = new JPanel();

		cuadro = new AreaCuadroPlayoffs();
	}

	@Override
	protected void seleccionado() {
		botonPartido.setEnabled(LigaManager.fase == 1);
		botonPlayoffs.setEnabled(LigaManager.fase == 1);
	}
	
	private class AreaCuadroPlayoffs extends JPanel {

		private int alto, ancho, marginX, marginY, separacion;
		private int espacioX;
		
		@Override
		public void paint(Graphics gr) {
			Graphics2D g = (Graphics2D) gr;
		
			g.setFont(new Font("Arial", Font.PLAIN, 15));
			
			marginX = getWidth()/15;
			marginY = getWidth()/20;
			ancho = (getWidth()-2*marginX)/10;
			alto = (getHeight()-marginY)/15;
			separacion = 20;
			espacioX = ancho/3;
			
			for(Series s: Series.values()) {
				dibujarSerie(s, g);
			}
		}
		
		private void dibujarSerie(Series s, Graphics2D g) {
			Point a,b;
			if(s != Series.FINAL) {
				a = calcPunto(s);
				b = new Point(a.x, a.y+calcularOffset(s));
				//dibujar el de arriba
				g.drawRect(a.x, a.y, ancho, alto);
				//dibujar el de abajo
				g.drawRect(b.x, b.y, ancho, alto);
			} else {
				Point c,d;
				c = calcPunto(Series.SEMIS_1_OESTE);
				d = calcPunto(Series.SEMIS_1_ESTE);
				a = new Point((c.x+d.x)/2, marginY+5*alto+2*separacion-(alto+separacion)/2);
				b = new Point(a.x, a.y+alto+separacion);
				//finalista arriba
				g.drawRect(a.x, a.y, ancho, alto);
				//finalista abajo
				g.drawRect(b.x, b.y, ancho, alto);
			}
			rellenarSerie(s,a,b,g);
		}
		
		private void rellenarSerie(Series s, Point a, Point b, Graphics2D g) {
			//if(LigaManager.fase > 0) {
			if(LigaManager.playoffs == null) return;
			if(LigaManager.playoffs.series == null) return;
				
				Equipo e1, e2;
				SeriePlayoffs sp = LigaManager.playoffs.series.get(s);
				if(sp != null) { //comprobar que se existe esa serie
					e1 = sp.getA();
					e2 = sp.getB();
					
					//pintarlos en el cuadro
					if(s != Series.SEMIS_2_OESTE && s != Series.SEMIS_2_ESTE) {
						g.drawString(sp.getVictoriasA()+" "+e1.getAbrev(), a.x+5, a.y+20);
						g.drawString(sp.getVictoriasB()+" "+e2.getAbrev(), b.x+5, b.y+20);
					} else {
						g.drawString(sp.getVictoriasA()+" "+e1.getAbrev(), b.x+5, b.y+20);
						g.drawString(sp.getVictoriasB()+" "+e2.getAbrev(), a.x+5, a.y+20);
					}
				}
			//}
		}
		
		private int calcularOffset(Series s) {
			if(s.ordinal() <= 7 ) {
				//cuartos
				return alto+separacion;
			} else if(s.ordinal() <= 11) {
				//semis
				return 3*alto+separacion;
			} else if(s.ordinal() <= 13) {
				//finales conferencia
				return 6*alto+2*separacion;
			} else {
				//finales
			}
			return 1;
		}
		
		private Point calcPunto(Series s) {
			switch(s) {
			case CUARTOS_1_ESTE: return new Point(getWidth()-marginX-ancho, marginY);
			case CUARTOS_4_ESTE: return new Point(getWidth()-marginX-ancho, marginY+separacion+3*alto);
			case CUARTOS_3_ESTE: return new Point(getWidth()-marginX-ancho, marginY+2*separacion+6*alto);
			case CUARTOS_2_ESTE: return new Point(getWidth()-marginX-ancho, marginY+3*separacion+9*alto);
			case CUARTOS_1_OESTE: return new Point(marginX, marginY);
			case CUARTOS_4_OESTE: return new Point(marginX, marginY+separacion+3*alto);
			case CUARTOS_3_OESTE: return new Point(marginX, marginY+2*separacion+6*alto);
			case CUARTOS_2_OESTE: return new Point(marginX, marginY+3*separacion+9*alto);
			case SEMIS_1_OESTE: return new Point(marginX+ancho+espacioX, marginY+(alto+separacion)/2);
			case SEMIS_2_OESTE: return new Point(marginX+ancho+espacioX, marginY+6*alto+2*separacion+(alto+separacion)/2);
			case SEMIS_1_ESTE: return new Point(getWidth()-marginX-ancho*2-espacioX, marginY+(alto+separacion)/2);
			case SEMIS_2_ESTE: return new Point(getWidth()-marginX-ancho*2-espacioX, marginY+6*alto+2*separacion+(alto+separacion)/2);
			case FINAL_OESTE: return new Point(marginX+ancho*2+2*espacioX, marginY+2*alto+separacion);
			case FINAL_ESTE: return new Point(getWidth()-marginX-ancho*3-2*espacioX, marginY+2*alto+separacion);
			case FINAL: return new Point();
			default: return new Point();
			}
		}
	}
	
	private void avisarJubilados(ArrayList<Jugador> ret) {
		if(ret.isEmpty()) return;
		String msg = "";
		boolean plural = ret.size() > 1;
		for(int i = 0; i < ret.size(); i++) {
			if(i == ret.size()-1) {
				msg = msg + ret.get(i).getNombre()+" ("+ret.get(i).getEdad()+")";
			} else if(i == ret.size()-2) {
				msg = msg + ret.get(i).getNombre()+" ("+ret.get(i).getEdad()+")"+" y ";
			} else {
				msg = msg + ret.get(i).getNombre()+" ("+ret.get(i).getEdad()+")"+", ";
			}
		}
		if(plural) {
			JOptionPane.showMessageDialog(null, msg+" se han jubilado, por lo que ya no podras contar con ellos de cara a la siguiente temporada", "Jugadores retirados", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, msg+" se ha jubilado, por lo que ya no podras contar con el de cara a la siguiente temporada", "Jugador retirado", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	 
}
