package negocio;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPartido {

	Partido p;
	Equipo e1, e2;
	ArrayList<Jugador> jugadores;
	@Before
	public void setUp() throws Exception {
		jugadores = new ArrayList<Jugador>();
		for (int i = 0; i < 12; i++) {
			jugadores.add(new Jugador());
		}
		
		e1 = new Equipo(40, jugadores);
		e2 = new Equipo(41, jugadores);
		
		p = new Partido(e1, e2);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(p.esLocal(e1));
		assertFalse(p.esLocal(e2));
	}

}
