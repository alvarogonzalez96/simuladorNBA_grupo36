package presentacion;

import javax.swing.*;

import negocio.LigaManager;

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
				}
			}
		});
		botonPlayoffs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(LigaManager.fase == 1) {
					while(LigaManager.playoffs.getCampeon() == null) {
						LigaManager.playoffs.avanzar();
					}
					//la fase de LigaManager la incrementa la propia clase playoffs
					JOptionPane.showMessageDialog(null, "Campeon: "+LigaManager.campeon.getNombre(), "Aviso", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}
	
	private class AreaCuadroPlayoffs extends JPanel {
		
		@Override
		public void paint(Graphics gr) {
			Graphics2D g = (Graphics2D) gr;
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
	}
	 
}
