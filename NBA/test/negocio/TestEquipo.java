package negocio;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestEquipo {

	Equipo e1;
	ArrayList<Jugador> jugadores;
	
	@Before
	public void setUp() throws Exception {
		jugadores = new ArrayList<Jugador>();
		
		for (int i = 0; i < 11; i++) {
			jugadores.add(new Jugador());
		}
		e1 = new Equipo(30, jugadores);
		e1.jugadores = jugadores;
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testEquipoSalario() {
		e1.calcSalarioTotal();
		assertEquals(0, e1.salarioTotal, 0.1);
		
		int i = 1;
		for (Jugador j : jugadores) {
			j.salario = i;
			i++;
		}
		e1.calcSalarioTotal();
		assertEquals(66, e1.salarioTotal, 0.1);
	}
	
	

}
