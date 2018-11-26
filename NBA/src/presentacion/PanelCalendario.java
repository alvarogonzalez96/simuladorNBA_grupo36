package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.awt.event.*;

import negocio.*;

@SuppressWarnings("serial")
public class PanelCalendario extends JPanel {

	private Liga liga;
	private Usuario usuario;
	
	private AreaCalendario panelPrincipal;
	private JPanel panelSeleccion;
	private JComboBox<String> combo;
	
	public PanelCalendario(Liga liga) {
		super();
		this.liga = liga;
		this.usuario = liga.usuario;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10,10,10,10));
		crearPaneles();
	}
	
	private void crearPaneles() {
		panelSeleccion = new JPanel();
		combo = new JComboBox<>();
		addElementosCombo();
		panelSeleccion.add(combo);
		add(panelSeleccion, BorderLayout.NORTH);
		
		panelPrincipal = new AreaCalendario();
		add(panelPrincipal, BorderLayout.CENTER);
		
		setListeners();
	}
	
	private void setListeners() {
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelPrincipal.repaint();
			}
		});
	}
	
	private void addElementosCombo() {
		combo.addItem("Octubre");
		combo.addItem("Noviembre");
		combo.addItem("Diciembre");
		combo.addItem("Enero");
		combo.addItem("Febrero");
		combo.addItem("Marzo");
		combo.addItem("Abril");
		combo.addItem("Mayo");
		combo.addItem("Junio");
		combo.addItem("Julio");
		combo.addItem("Agosto");
		combo.addItem("Septiembre");
	}
	
	private class AreaCalendario extends JPanel {
		@Override
		public void paint(Graphics gr) {
			Graphics2D g = (Graphics2D) gr;
			
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			g.setColor(Color.BLACK);
			int size = (int) (getHeight()*.8/6);
			int marginX = (getWidth()-7*size)/2;
			int marginY = (getHeight()-6*size)/2;
			
			int mes = getDiaSeleccionado();
			Date dia = new Date();
			int anyo = 2016;
			if(mes >= 9) anyo = 2015;
			try {
				dia = sdf.parse("01/"+(mes+1)+"/"+anyo);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			int diaSemanaInicio = getDiaSemana(dia);
			
			//dibujar cabecera (dias de la semana)
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			for(int i = 0; i < 7; i++) {
				String d;
				switch(i) {
				case 0: d = "L"; break;
				case 1: d = "M"; break;
				case 2: d = "X"; break;
				case 3: d = "J"; break;
				case 4: d = "V"; break;
				case 5: d = "S"; break;
				default: d = "D";
				}
				g.drawString(d, marginX + i*size + size/2, marginY-20);
			}
			System.out.println(diaSemanaInicio);
			//dibujar el calendario
			for(int w = 0; w < 6; w++) {
				for(int d = 0; d < 7; d++) {
					g.drawRect(marginX+d*size, marginY+w*size, size, size);
					if(w != 0 || d >= diaSemanaInicio) {
						//pintar el dia
						g.drawString(getDia(dia)+"", marginX + d*size + 5, marginY + w*size + 20);
						dia = incrementarDia(dia);
						pintarPartido(g, dia, w, d, marginX, marginY, size);
					}
				}
			}
		}
		
		private void pintarPartido(Graphics2D g, Date dia, int w, int d, int marginX, int marginY, int size) {
			Calendario c = liga.getCalendario();
			ArrayList<Partido> partidos = c.calendario.get(dia);
			if(partidos != null) {
				for(Partido p: partidos) {
					if(p.esLocal(usuario.getEquipo())) {
						//pintar como local
						g.drawString("vs "+p.getVisitante().getAbrev(), marginX+d*size + 15, marginY+w*size+(size/2));
						break;
					} else if(p.esVisitante(usuario.getEquipo())) {
						//pintar como visitante
						g.drawString("@ "+p.getLocal().getAbrev(), marginX+d*size + 15, marginY+w*size+(size/2));
						break;
					}
				}
			}
		}
		
		private int getDiaSeleccionado() {
			switch(combo.getSelectedIndex()) {
			case 0: return 9;
			case 1: return 10;
			case 2: return 11;
			default: return combo.getSelectedIndex()-3;
			}
		}
		
		private int getDia(Date d) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c.get(Calendar.DAY_OF_MONTH);
			
		}
		
		private int getDiaSemana(Date d) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int m = c.get(Calendar.DAY_OF_WEEK);
			switch(m) {
			case Calendar.MONDAY: return 0;
			case Calendar.TUESDAY: return 1;
			case Calendar.WEDNESDAY: return 2;
			case Calendar.THURSDAY: return 3;
			case Calendar.FRIDAY: return 4;
			case Calendar.SATURDAY: return 5;
			default: return 6;
			}
		}
		
		private Date incrementarDia(Date d) {
			Calendar calendario = Calendar.getInstance();
			calendario.setTime(d);
			calendario.add(Calendar.DATE, 1);
			return calendario.getTime();
		}
	}
	
}
