package negocio;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestQuinteto {

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		assertEquals(Posicion.BASE, Quinteto.elegirPosicion(0));
		assertEquals(Posicion.ESCOLTA, Quinteto.elegirPosicion(1));
		assertEquals(Posicion.ALERO, Quinteto.elegirPosicion(2));
		assertEquals(Posicion.ALAPIVOT, Quinteto.elegirPosicion(3));
		assertEquals(Posicion.PIVOT, Quinteto.elegirPosicion(4));
		
		assertNull(Quinteto.elegirPosicion(8));
	}

}
