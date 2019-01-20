package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.*;

@SuppressWarnings("serial")
public class VentanaHTML extends JFrame {

	public static final int AYUDA = 0, ACERCA_DE = 1;
	
	private JEditorPane pane;
	private JScrollPane scroll;
	
	public VentanaHTML(int tipo) {
		super();
		
		String dir;
		if(tipo == AYUDA) {
			dir = "data/ayuda.html";
			setTitle("Ayuda");
		} else {
			dir = "data/acercaDe.html";
			setTitle("Acerca de");
		}
		
		pane = new JEditorPane();
		pane.setContentType("text/html");
		
		scroll = new JScrollPane(pane);
		scroll.setPreferredSize(new Dimension(700,700));
		getContentPane().add(scroll, BorderLayout.CENTER);
		
		try {
			String html = archivoAString(dir);
			pane.setText(html);
			pane.setEditable(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		//setVisible(true);
	}
	
	@Override 
	public void setVisible(boolean visible) {
	    super.setVisible(visible);

	    if (visible == true) {
	      scroll.getVerticalScrollBar().setValue(0);    // scroll bar to top
	      scroll.repaint();
	    }
	  }
	
	public static String archivoAString(String dir) throws IOException {
		return new String(Files.readAllBytes(Paths.get(dir)));
	}

}
