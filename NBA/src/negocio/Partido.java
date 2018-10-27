package negocio;

import java.util.Comparator;

public class Partido {

	protected Equipo local;
	protected Equipo visitante;
	
	protected Quinteto quintetoLocal, quintetoVisitante;
	
	protected int puntosLocal, puntosVisitante;
	protected boolean atacaLocal;

		
	public Partido(Equipo local, Equipo visitante) {		
		this.local = local;
		this.visitante = visitante;
		
		puntosLocal = puntosVisitante = 0;
		atacaLocal = true;
		
	}
	
	public void jugar() {
		int tiempo = 0;
		
		asignarMinutos(local);
		asignarMinutos(visitante);
		
		quintetoLocal = new Quinteto(local);
		quintetoVisitante = new Quinteto(visitante);
		
		//48 min * 60 = 2880 segundos
		while(tiempo < 2880) {
			quintetoLocal.actualizar(tiempo);
			quintetoVisitante.actualizar(tiempo);
			
			if(atacaLocal) {
				simularJugada(quintetoLocal, quintetoVisitante);
			} else {
				simularJugada(quintetoVisitante, quintetoLocal);
			}

			atacaLocal = !atacaLocal;

			int rand = (int)(Math.random()*19+5);
			tiempo = tiempo + rand;
			quintetoLocal.actualizarTiempo(rand);
			quintetoVisitante.actualizarTiempo(rand);
		}
		while(puntosLocal == puntosVisitante) {
			if(atacaLocal) {
				simularJugada(quintetoLocal, quintetoVisitante);
			} else {				
				simularJugada(quintetoVisitante, quintetoLocal);
			}
			atacaLocal = !atacaLocal;
		}
		if(puntosLocal > puntosVisitante) {
			local.nuevaVictoria();
			visitante.nuevaDerrota();
		} else {
			local.nuevaDerrota();
			visitante.nuevaVictoria();
		}
		System.out.println("Marcador final: "+puntosVisitante+"-"+puntosLocal);
		System.out.println("----------------------------------------");
		
		/*for (Jugador j : local.jugadores) {
			if(!j.rol.equals(Rol.NOJUEGA)) {
				System.out.println(j.nombre + " " + j.puntosPartido + " pts");
			}
			j.setPuntosPartido(0);
		}
		System.out.println();
		for (Jugador j : visitante.jugadores) {
			j.setPuntosPartido(0);
		}
		System.out.println("*******************");
		System.out.println();
		*/
	}
	
	public void simularJugada(Quinteto atacando, Quinteto defendiendo) {
		//Número que decide la jugada
		double random = Math.random();
		//Probabilidad de que la jugada acabe en un tiro de 2
		double pTiro = 0.773;
		//Probabilidad de que la jugada acabe con un tiro libre
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
	
	public void tirar(Quinteto atacando, Quinteto defendiendo) {
		double tiroDeDos = 0.6572;
		double random = Math.random();
		double meterDos = 0.55;
		double meterTres = 0.36;
		double variacionJugador = 0;
		
		Jugador j = new Jugador();
		
		if(random <= tiroDeDos) {
			//Tira de 2
			//Elijo el tirador
			
			if(atacaLocal) {
				j = elegirTiradorDos(quintetoLocal);
				//System.out.println("Tira de 2 local: " + j.nombre);
			} else {
				j = elegirTiradorDos(quintetoVisitante);
				//System.out.println("Tira de 2 visitante: " + j.nombre);
			}
			
			variacionJugador = j.getTiroCerca();
			variacionJugador = variacionJugador / 100;
			random = Math.random()+0.5;
			if(random <= variacionJugador) {
				//Mete el tiro de 2
				if(atacaLocal) {
					//System.out.println("--Mete canasata el equipo local");
					j.puntosPartido += 2;
					//System.out.println("----Ha metido -> " + j.nombre + ", lleva: " + j.puntosPartido);
					puntosLocal += 2;
				} else {

					//System.out.println("--Mete canasata el equipo visitante");
					j.puntosPartido += 2;
					//System.out.println("----Ha metido -> " + j.nombre + ", lleva: " + j.puntosPartido);
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
			
			if(atacaLocal) {
				j = elegirTiradorTres(quintetoLocal);
				//System.out.println("Tira de 3 local: " + j.nombre);
			} else {
				j = elegirTiradorTres(quintetoVisitante);
				//System.out.println("Tira de 3 visitante: " + j.nombre);
			}
			variacionJugador = j.getTiroLejos();
			variacionJugador = variacionJugador / 100;
			random = Math.random()+0.05;

			if(random <= variacionJugador ) {
				//Mete el tiro de 3
				if(atacaLocal) {
					//System.out.println("--Mete triple el equipo local");
					j.puntosPartido += 3;
					//System.out.println("----Ha metido -> " + j.nombre + ", lleva: " + j.puntosPartido);
					puntosLocal += 3;
				} else {
					//System.out.println("--Mete triple el equipo visitante");
					j.puntosPartido += 3;
					//System.out.println("----Ha metido -> " + j.nombre + ", lleva: " + j.puntosPartido);
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
	
	public void tirosLibres(Quinteto atacando, Quinteto defendiendo) {
		double random = Math.random();
		
		//Primer tiro libre
		Jugador j = new Jugador();
		if(atacaLocal) {
			j = elegirTiradorLibre(quintetoLocal);	
		} else {
			j = elegirTiradorLibre(quintetoVisitante);
		}
		
		if(random <= j.getTiroLibre()) {
			//Mete el primer tiro libre
			if(atacaLocal) {
				puntosLocal += 1;
			} else {
				puntosVisitante += 1;
			}
			j.puntosPartido += 1;
		} else {
			//Falla el primer tiro libre
			
		}
		
		//Segundo tiro libre
		random = Math.random();
		if(random <= j.getTiroLibre()) {
			//Mete el segundo tiro libre
			if(atacaLocal) {
				puntosLocal += 1;
			} else {
				puntosVisitante += 1;
			}
			j.puntosPartido += 1;
			//Acaba la jugada
			
		} else {
			//Falla el segundo tiro libre
			
			//Llamar a rebote
			rebote(atacando, defendiendo);
		}
	}
	
	public void rebote(Quinteto atacando, Quinteto defendiendo) {
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
	
	public Jugador elegirTiradorLibre(Quinteto q) {
		Jugador jugador = q.jugadores[0];
		for (Jugador j : q.jugadores) {
			if(j.getTiroLibre() + (Math.random()*70) > jugador.getTiroLibre()+ (Math.random()*70)) {
				jugador = j;
			}
		}
		return jugador;
	}
	
	public Jugador elegirTiradorTres(Quinteto q) {
		Jugador jugador = q.jugadores[0];
		for (Jugador j : q.jugadores) {
			if(j.getTiroLejos() + (Math.random()*70) > jugador.getTiroLejos()+ (Math.random()*70)) {
				jugador = j;
			}
		}
		return jugador;
	}
	
	public Jugador elegirTiradorDos(Quinteto q) {
		Jugador jugador = q.jugadores[0];
		for (Jugador j : q.jugadores) {
			if(!j.posicion.equals(Posicion.PIVOT)) {
				if(j.getTiroCerca() + (Math.random()*100) > jugador.getTiroCerca()+ (Math.random()*100)) {
					jugador = j;
				} 
			} else {
				if(j.getTiroCerca() - Math.random()*10 > jugador.getTiroCerca() + Math.random()*100) {
					jugador = j;
				}
			}
		}
		return jugador;
	}
	
	public void asignarMinutos(Equipo equipo) {
		int min = 2880;
		int minBase = 0;
		int minEscolta = 0;
		int minAlero = 0;
		int minAlaPivot = 0;
		int minPivot = 0;
		
		for(Jugador j: equipo.jugadores) {
			if(j.posicion == Posicion.BASE) {
				repartoEstrellaTitular(j);
				if(j.rol == Rol.SUPLENTE) {
					j.setMinutos(minBase);
					j.setTiempoJugado(minBase);
				} 
				minBase = min - j.getMinutos();
			} else if(j.posicion == Posicion.ESCOLTA) {
				repartoEstrellaTitular(j);
				if(j.rol == Rol.SUPLENTE) {
					j.setMinutos(minEscolta);
					j.setTiempoJugado(minEscolta);
				}
				minEscolta = min - j.getMinutos();
			} else if(j.posicion == Posicion.ALERO) {
				repartoEstrellaTitular(j);
				if(j.rol == Rol.SUPLENTE) {
					j.setMinutos(minAlero);
					j.setTiempoJugado(minAlero);
				}
				minAlero = min - j.getMinutos();
			} else if(j.posicion == Posicion.ALAPIVOT) {
				repartoEstrellaTitular(j);
				if(j.rol == Rol.SUPLENTE) {
					j.setMinutos(minAlaPivot);
					j.setTiempoJugado(minAlaPivot);
				}
				minAlaPivot = min - j.getMinutos();
			} else {
				repartoEstrellaTitular(j);
				if(j.rol == Rol.SUPLENTE) {
					j.setMinutos(minPivot);
					j.setTiempoJugado(minPivot);
				}
				minPivot = min - j.getMinutos();
			}
			//System.out.println(j.nombre+" juega "+j.getMinutos());
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

	@Override
	public String toString() {
		return visitante.nombre+" - "+local.nombre;
	}
	
}
