package presentacion;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.*;

public class PanelNoticiario extends PanelTab {

	public static JScrollPane scroll;
	public static JList<String> noticias;
	public static DefaultListModel<String> model = new DefaultListModel<>();
	
	public PanelNoticiario() {		
		setLayout(new FlowLayout());
	}
	
	public static JList<String> rellenarNoticiario(ArrayList<String> not) {
		int i = 0;
		for (String s : not) {
			model.addElement(s);
			//System.out.println(s);
			//System.out.println(model.getElementAt(i));
			i++;
		}
		
		noticias = new JList<String>(model);
		return noticias;
		
		
	}

	@Override
	protected void initComponentes() {
		noticias = rellenarNoticiario(new ArrayList<String>());
		scroll = new JScrollPane(noticias);
		scroll.setPreferredSize(new Dimension(1200, 700));
		
		add(scroll);
	}

	@Override
	protected void crearPaneles() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void seleccionado() {
		// TODO Auto-generated method stub
		
	}
	
}
