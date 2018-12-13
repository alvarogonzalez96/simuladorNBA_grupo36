package presentacion;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public abstract class PanelTab extends JPanel {

	public PanelTab() {
		super();
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10,10,10,10));
		crearPaneles();
		initComponentes();
		setListeners();
	}
	
	protected abstract void initComponentes();
	
	protected abstract void crearPaneles();
	
	protected abstract void setListeners();
	
	protected abstract void seleccionado();
}
