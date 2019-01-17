package presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.*;

public class VentanaAyuda extends JFrame {

	private JEditorPane pane;
	private JScrollPane scroll;
	
	public VentanaAyuda() {
		super();
		
		pane = new JEditorPane();
		pane.setContentType("text/html");
		
		scroll = new JScrollPane(pane);
		scroll.setPreferredSize(new Dimension(500,500));
		getContentPane().add(scroll, BorderLayout.CENTER);
		
		try {
			String html = archivoAString("data/ayuda.html");
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
	
	public static void main(String[] args) {
		new VentanaAyuda();
	}

}
