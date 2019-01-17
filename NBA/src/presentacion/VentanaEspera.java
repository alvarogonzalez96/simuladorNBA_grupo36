package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;

import javax.swing.*;

import negocio.LigaManager;

public class VentanaEspera extends JWindow {

	JLabel texto;
	JLabel img;
	
	public VentanaEspera() {
		super();
		texto = new JLabel("\nCargando...", SwingConstants.CENTER);
		texto.setFont(new Font("Impact", Font.PLAIN, 30));
		texto.setForeground(new Color(52, 107, 170));
		JPanel p = new JPanel(new BorderLayout());
		getContentPane().add(p);
		p.add(texto, BorderLayout.SOUTH);
		ImageIcon i = new ImageIcon(new ImageIcon("res/logo.png").getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH));
		img = new JLabel();//"res\\loader.gif"));
		img.setIcon(i);
		p.add(img, BorderLayout.CENTER);
		setSize(300,300);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		p.setBackground(Color.WHITE);
		setVisible(true);
	}	
	
	public static void main(String[] args) {
		new VentanaEspera();
	}
	
}
