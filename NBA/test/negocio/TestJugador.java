package negocio;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJugador {

	Jugador j1;
	
	@Before
	public void setUp() throws Exception {
		j1 = new Jugador();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValoracion() {
		assertEquals(0, j1.getValoracion(), 0.1);
		
		j1.puntosPartido = 10;
		j1.asistenciasPartido = 10;
		j1.rebotesPartido = 10;
		assertEquals(30, j1.getValoracion(), 0.1);
	}

}
