package negocio;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJugador {

	Jugador j1, j2;
	
	@Before
	public void setUp() throws Exception {
		j1 = new Jugador();
		j2 = new Jugador();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPuntosPorPartido() {
		j1.setPuntosTemporada(100);
		j1.partidosJugadosTemporada = 10;
		
		assertEquals(10, j1.getPuntosPorPartido(), 0);
	}
	
	@Test
	public void testValoracion() {
		assertEquals(0, j2.getValoracion(), 0.1);
		
		j2.puntosTemporada = 10;
		j2.asistenciasTemporada = 10;
		j2.rebotesTemporada = 10;
		assertEquals(30, j2.getValoracion(), 0.1);
	}

}
