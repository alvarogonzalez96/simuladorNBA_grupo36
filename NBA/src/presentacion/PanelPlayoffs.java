package presentacion;
import negocio.*;
import javax.swing.*;

import negocio.LigaManager;
import negocio.Playoffs.Series;
import negocio.Playoffs.SeriePlayoffs;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class PanelPlayoffs extends JPanel {

	private JPanel panelBotones;
	private AreaCuadroPlayoffs cuadro;
	
	private JButton botonPartido, botonPlayoffs;
	
	public PanelPlayoffs() {
		panelBotones = new JPanel();
		botonPlayoffs = new JButton("Simular playoffs");
		botonPartido = new JButton("Simular partido");
		panelBotones.add(botonPartido);
		panelBotones.add(botonPlayoffs);
		
		cuadro = new AreaCuadroPlayoffs();
		
		setLayout(new BorderLayout());
		add(panelBotones, BorderLayout.NORTH);
		add(cuadro, BorderLayout.CENTER);
		
		setListeners();
	}
	
	private void setListeners() {
		botonPartido.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(LigaManager.fase == 1) {
					LigaManager.playoffs.avanzar();
					
					//repintar cuadro de playoffs y comprobar si ha terminado la temporada
					repaint();
					if(LigaManager.playoffs.getCampeon() != null) {
						JOptionPane.showMessageDialog(null, "Campeon: "+LigaManager.campeon.getNombre(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		botonPlayoffs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(LigaManager.fase == 1) {
					while(LigaManager.playoffs.getCampeon() == null) {
						LigaManager.playoffs.avanzar();
						repaint();
					}
					//la fase de LigaManager la incrementa la propia clase playoffs
					JOptionPane.showMessageDialog(null, "Campeon: "+LigaManager.campeon.getNombre(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
					//Aqui para probar
					LigaManager.jubilar();
					LigaManager.renovaciones();
					LigaManager.agenciaLibre();
				}
			}
		});
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
			if(LigaManager.fase > 0) {
				Equipo e1, e2;
				SeriePlayoffs sp = LigaManager.playoffs.series.get(s);
				if(sp != null) { //comprobar que se existe esa serie
					e1 = sp.getA();
					e2 = sp.getB();
					
					//pintarlos en el cuadro
					g.drawString(sp.getVictoriasA()+" "+e1.getAbrev(), a.x+5, a.y+20);
					g.drawString(sp.getVictoriasB()+" "+e2.getAbrev(), b.x+5, b.y+20);
				}
			}
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
			case CUARTOS_2_ESTE: return new Point(getWidth()-marginX-ancho, marginY+separacion+3*alto);
			case CUARTOS_3_ESTE: return new Point(getWidth()-marginX-ancho, marginY+2*separacion+6*alto);
			case CUARTOS_4_ESTE: return new Point(getWidth()-marginX-ancho, marginY+3*separacion+9*alto);
			case CUARTOS_1_OESTE: return new Point(marginX, marginY);
			case CUARTOS_2_OESTE: return new Point(marginX, marginY+separacion+3*alto);
			case CUARTOS_3_OESTE: return new Point(marginX, marginY+2*separacion+6*alto);
			case CUARTOS_4_OESTE: return new Point(marginX, marginY+3*separacion+9*alto);
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
	 
}
