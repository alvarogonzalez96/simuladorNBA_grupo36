package negocio;

import java.util.Scanner;

public class Partido {

	protected Equipo local;
	protected Equipo visitante;
	
	protected int puntosLocal, puntosVisitante;
	protected boolean atacaLocal;
	
	public Partido(Equipo local, Equipo visitante) {
		
		asignarMinutos(local);
		//System.out.println();
		asignarMinutos(visitante);
		
		puntosLocal = puntosVisitante = 0;
		atacaLocal = true;
		
		int tiempo = 0; 
		
		Jugador[] localJugando = new Jugador[5];
		Jugador[] visitanteJugando = new Jugador[5];
		
		//48 min * 60 = 2880 segundos
		while(tiempo < 2880) {
			localJugando = seleccionarCinco(local.jugadores,tiempo);
			//System.out.println();
			for (int i = 0; i < localJugando.length; i++) {
				//System.out.println(localJugando[i]);
			}
			visitanteJugando = seleccionarCinco(visitante.jugadores, tiempo);
			//System.out.println();
			for (int i = 0; i < visitanteJugando.length; i++) {
				//System.out.println(visitanteJugando[i]);
			}
			//Scanner sc = new Scanner(System.in);
			//sc.nextLine();
			
			if(atacaLocal) {
				simularJugada(localJugando, visitanteJugando);
			} else {
				simularJugada(visitanteJugando, localJugando);
			}
			
			atacaLocal = !atacaLocal;
			
			int rand = (int)(Math.random()*19+5);
			tiempo = tiempo + rand;
			actualizarTiempoJugadores(localJugando, visitanteJugando, rand);
		}
		while(puntosLocal == puntosVisitante) {
			System.out.println("empate");
			if(atacaLocal) {
				simularJugada(localJugando, visitanteJugando);
			} else {				
				simularJugada(visitanteJugando, localJugando);
			}
			atacaLocal = !atacaLocal;
		}
		System.out.println("Marcador final: "+puntosVisitante+"-"+puntosLocal);
	}
	
	public void simularJugada(Jugador[] atacando, Jugador[] defendiendo) {
		//Número que decide la jugada
		double random = Math.random();
		double pTiro = 0.773;
		double pTiroLibre = 0.098;
		 
		if(random <= pTiro) {
			//Tira
			tirar(atacando, defendiendo);
		} else if(random <= pTiro + pTiroLibre) {
			//Tiros libres
			tirosLibres(atacando, defendiendo);
		} else {
			//Pérdida
			//Termina la jugada
			
		}
	}
	
	public void tirar(Jugador[] atacando, Jugador[] defendiendo) {
		double tiroDeDos = 0.6572;
		double random = Math.random();
		double meterDos = 0.55;
		double meterTres = 0.36;
		
		if(random <= tiroDeDos) {
			//Tira de 2
			random = Math.random();
			if(random <= meterDos) {
				//Mete el tiro de 2
				if(atacaLocal) {
					puntosLocal += 2;
				} else {
					puntosVisitante += 2;
				}
				//Acaba la jugada
			} else {
				//Falla el tiro de 2
				
				//LLamar a rebote
				rebote(atacando, defendiendo);
			}
		} else {
			//Tira de 3
			random = Math.random();
			if(random <= meterTres) {
				//Mete el tiro de 3
				if(atacaLocal) {
					puntosLocal += 3;
				} else {
					puntosVisitante += 3;
				}
				//Acaba la jugada
			} else {
				//Falla el tiro de tres
				
				//Lamar a rebote
				rebote(atacando, defendiendo);
			}	
		}
	}
	
	public void tirosLibres(Jugador[] atacando, Jugador[] defendiendo) {
		double meterTiroLibre = 0.772;
		double random = Math.random();
		
		//Primer tiro libre
		if(random <= meterTiroLibre) {
			//Mete el primer tiro libre
			if(atacaLocal) {
				puntosLocal += 1;
			} else {
				puntosVisitante += 1;
			}
		} else {
			//Falla el primer tiro libre
			
		}
		
		//Segundo tiro libre
		random = Math.random();
		if(random <= meterTiroLibre) {
			//Mete el segundo tiro libre
			if(atacaLocal) {
				puntosLocal += 1;
			} else {
				puntosVisitante += 1;
			}
			//Acaba la jugada
			
		} else {
			//Falla el segundo tiro libre
			
			//Llamar a rebote
			rebote(atacando, defendiendo);
		}
	}
	
	public void rebote(Jugador[] atacando, Jugador[] defendiendo) {
		double reboteDef = 0.731;
		double random = Math.random();
		
		if(random <= reboteDef) {
			//Rebote defensivo
			
			//Acaba la jugada
			
		} else {
			//Rebote ofensivo
			
			//Se inicia otra vez el ataque, recursividad.
			simularJugada(atacando, defendiendo);
		}
	}
	
	public void actualizarTiempoJugadores(Jugador[] locales, Jugador[] visitantes, int rand) {
		for (int i = 0; i < visitantes.length; i++) {
			locales[i].setTiempoJugado(locales[i].getTiempoJugado()-rand);
			visitantes[i].setTiempoJugado(visitantes[i].getTiempoJugado()-rand);
		}
	}
	
	public Jugador[] seleccionarCinco(Jugador[] jugadores, int tiempo) {
		Jugador[] j = new Jugador[5];
		
		for (int i = 0; i < j.length; i++) {
			j[i] = elegir(jugadores, tiempo, i);
		}
		return j;
	}
	
	public Jugador elegir(Jugador[] jugadores, int tiempo, int pos) {
		Jugador j = new Jugador();
		Posicion p = elegirPosicion(pos);
		for (int i = 0; i < jugadores.length; i++) {
			if(jugadores[i].posicion == p && jugadores[i].getTiempoJugado() > 0) {
				j = elegirJugador(jugadores[i]);
				break;
			}
		}
		return j;
	}
	
	public Jugador elegirJugador(Jugador jugador) {
		Jugador j = new Jugador();
		if(jugador.rol == Rol.ESTRELLA || jugador.rol == Rol.TITULAR) {
			j = jugador;
		} else {
			j = jugador;
		}
		return j;
	}
	
	public Posicion elegirPosicion(int pos) {
		if(pos == 0) {
			return Posicion.BASE;
		} else if(pos == 1) {
			return Posicion.ESCOLTA;
		} else if(pos == 2) {
			return Posicion.ALERO;
		} else if(pos == 3) {
			return Posicion.ALAPIVOT;
		} else if(pos == 4) {
			return Posicion.PIVOT;
		}
		return null;
	}
	
	public void asignarMinutos(Equipo equipo) {
		int min = 2880;
		int minBase = 0;
		int minEscolta = 0;
		int minAlero = 0;
		int minAlaPivot = 0;
		int minPivot = 0;
		
		for (int i = 0; i < 10; i++) {
			if(equipo.jugadores[i].posicion == Posicion.BASE) {
				repartoEstrellaTitular(equipo.jugadores[i]);
				if(equipo.jugadores[i].rol == Rol.SUPLENTE) {
					equipo.jugadores[i].setMinutos(minBase);
					equipo.jugadores[i].setTiempoJugado(minBase);
				}
				minBase = min - equipo.jugadores[i].getMinutos();
				
			} else if(equipo.jugadores[i].posicion == Posicion.ESCOLTA) {
				repartoEstrellaTitular(equipo.jugadores[i]);
				if(equipo.jugadores[i].rol == Rol.SUPLENTE) {
					equipo.jugadores[i].setMinutos(minEscolta);
					equipo.jugadores[i].setTiempoJugado(minEscolta);
				}
				minEscolta = min - equipo.jugadores[i].getMinutos();
			} else if(equipo.jugadores[i].posicion == Posicion.ALERO) {
				repartoEstrellaTitular(equipo.jugadores[i]);
				if(equipo.jugadores[i].rol == Rol.SUPLENTE) {
					equipo.jugadores[i].setMinutos(minAlero);
					equipo.jugadores[i].setTiempoJugado(minAlero);
				}
				minAlero = min - equipo.jugadores[i].getMinutos();
			} else if(equipo.jugadores[i].posicion == Posicion.ALAPIVOT) {
				repartoEstrellaTitular(equipo.jugadores[i]);
				if(equipo.jugadores[i].rol == Rol.SUPLENTE) {
					equipo.jugadores[i].setMinutos(minAlaPivot);
					equipo.jugadores[i].setTiempoJugado(minAlaPivot);
				}
				minAlaPivot = min - equipo.jugadores[i].getMinutos();
			} else if(equipo.jugadores[i].posicion == Posicion.PIVOT) {
				repartoEstrellaTitular(equipo.jugadores[i]);
				if(equipo.jugadores[i].rol == Rol.SUPLENTE) {
					equipo.jugadores[i].setMinutos(minPivot);
					equipo.jugadores[i].setTiempoJugado(minPivot);
				}
				minPivot = min - equipo.jugadores[i].getMinutos();
			}
			
			//System.out.println(equipo.jugadores[i]);
		}
	}
	public void repartoEstrellaTitular(Jugador jugador) {
		if(jugador.rol == Rol.ESTRELLA) {
			minutosEstrella(jugador);
		} 
		if(jugador.rol == Rol.TITULAR) {
			minutosTitular(jugador);
		}
	}
	
	public void minutosEstrella(Jugador jugador) {
		int rand;
		rand = (int)(Math.random()*300);
		jugador.setMinutos(2040 + rand);
		jugador.setTiempoJugado(jugador.getMinutos());
	}
	
	public void minutosTitular(Jugador jugador) {
		int rand;
		rand = (int)(Math.random()*300);
		jugador.setMinutos(1800 + rand);
		jugador.setTiempoJugado(jugador.getMinutos());
	}
}
