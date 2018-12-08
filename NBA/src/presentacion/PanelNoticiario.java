package presentacion;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.*;

public class PanelNoticiario extends JPanel {

	public static JScrollPane scroll;
	public static JList<String> noticias;
	public static DefaultListModel<String> model = new DefaultListModel<>();
	
	public PanelNoticiario() {
	/*	noticias = rellenarNoticiario(new ArrayList<String>());
		noticias.setPreferredSize(new Dimension(700, 700));
		
		add(noticias);*/
		
		noticias = rellenarNoticiario(new ArrayList<String>());
		scroll = new JScrollPane(noticias);
		scroll.setPreferredSize(new Dimension(1200, 700));
		
		add(scroll);
		
		setLayout(new FlowLayout());
		setVisible(true);
		
	}
	
	public static JList<String> rellenarNoticiario(ArrayList<String> not) {
		int i = 0;
		for (String s : not) {
			model.addElement(s);
			System.out.println(s);
			System.out.println(model.getElementAt(i));
			i++;
		}
		
		noticias = new JList<String>(model);
		return noticias;
		
		
	}
	
}
