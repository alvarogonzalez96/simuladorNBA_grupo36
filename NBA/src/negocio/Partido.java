package negocio;

import javafx.geometry.Pos;

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
	
	public Partido(Partido p) {
		this.local = p.local;
		this.visitante = p.visitante;
		
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
		System.out.println(visitante.nombre+" "+puntosVisitante+" - "+puntosLocal+" "+local.nombre);
		//System.out.println("----------------------------------------");
		for (Jugador j : local.jugadores) {
			j.setNPuntos(0);
			j.setNAsistencias(0);
			j.setNRebotes(0);
		}
		
		for (Jugador j : visitante.jugadores) {
			j.setNPuntos(0);
			j.setNPuntos(0);
			j.setNRebotes(0);
		}
		
	}
	
	public void simularJugada(Quinteto atacando, Quinteto defendiendo) {
	
		double random = Math.random();//Número que decide la jugada
		double pTiro = 0.773;//Probabilidad de que la jugada acabe en un tiro de 2
		double pTiroLibre = 0.098;//Probabilidad de que la jugada acabe con un tiro libre

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
			} else {
				j = elegirTiradorDos(quintetoVisitante);
			}
			
			variacionJugador = j.getTiroCerca();
			variacionJugador = variacionJugador / 100;
			random = Math.random()+0.5;
			if(random <= variacionJugador) {
				//Mete el tiro de 2
				if(atacaLocal) {
					elegirAsistente(quintetoLocal, j);
					puntosLocal += 2;
				} else {
					elegirAsistente(quintetoVisitante, j);
					puntosVisitante += 2;
				}
				j.puntosPartido += 2;
				j.nPuntos += 2;
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
			} else {
				j = elegirTiradorTres(quintetoVisitante);
			}
			variacionJugador = j.getTiroLejos();
			variacionJugador = variacionJugador / 100;
			random = Math.random()+0.05;

			if(random <= variacionJugador ) {
				//Mete el tiro de 3
				if(atacaLocal) {
					elegirAsistente(quintetoLocal, j);
					puntosLocal += 3;
				} else {
					elegirAsistente(quintetoVisitante, j);
					puntosVisitante += 3;
				}
				j.puntosPartido += 3;
				j.nPuntos += 3;
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
			elegirReboteador(defendiendo);
			//Acaba la jugada
			
		} else {
			//Rebote ofensivo
			elegirReboteador(atacando);
			//Se inicia otra vez el ataque, recursividad.
			simularJugada(atacando, defendiendo);
		}
	}
	
	public void elegirReboteador(Quinteto q) {
		Jugador jugador = new Jugador();
		for (Jugador j : q.jugadores) {
			if(j.getPosicion().equals(Posicion.BASE) || j.getPosicion().equals(Posicion.ESCOLTA)) {
				if(j.getRebote() >= 60) {
					if(j.getRebote() - 50 > jugador.getRebote() + Math.random()*100) {
						jugador = j;
					}
				} else if(j.getRebote() + Math.random()*30 > jugador.getRebote() + Math.random()*100) {
					jugador = j;
				}
			} else if(j.getPosicion().equals(Posicion.ALERO)) {
				if(j.getRebote() >= 65) {
					if(j.getRebote() + Math.random()*20 > jugador.getRebote() + Math.random()*100) {
						jugador = j;
					}
				} else if(j.getRebote() + Math.random()*30 > jugador.getRebote() + Math.random()*100) {
					jugador = j;
				}
			} else if(j.getPosicion().equals(Posicion.ALAPIVOT)) {
				if(j.getRebote() + Math.random()*5 > jugador.getRebote() + Math.random()*100) {
					jugador = j;
				}
			} else {
				if(j.getRebote() - Math.random()*20 > jugador.getRebote() + Math.random()*100) {
					jugador = j;
				}
			}
		}
		jugador.rebotesPartido += 1;
		jugador.nRebotes += 1;
	}
	
	public void elegirAsistente(Quinteto q, Jugador jug) {
		Jugador jugador = q.jugadores[0];
		for (Jugador j : q.jugadores) {
			if(j.getAsistencia() + (Math.random()*70) > jugador.getAsistencia() + (Math.random()*70) && !jugador.getNombre().equals(jug.getNombre())) {
				jugador = j;
			}
		}
		double random = Math.random();
		if(random <= 0.582) {
			jugador.asistenciasPartido += 1;
			jugador.nAsistencias += 1;
		}
	}
	
	public Jugador elegirTiradorLibre(Quinteto q) {
		Jugador jugador = q.jugadores[0];
		for (Jugador j : q.jugadores) {
			if(j.getTiroLibre() + (Math.random()*70) > jugador.getTiroLibre() + (Math.random()*70)) {
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
			if(!j.getPosicion().equals(Posicion.PIVOT)) {
				if(j.getTiroCerca() + (Math.random()*100) > jugador.getTiroCerca() + (Math.random()*100)) {
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
			if(j.getPosicion() == Posicion.BASE) {
				repartoEstrellaTitular(j);
				if(j.getRol() == Rol.SUPLENTE) {
					j.setMinutos(minBase);
					j.setTiempoJugado(minBase);
				} 
				minBase = min - j.getMinutos();
			} else if(j.getPosicion() == Posicion.ESCOLTA) {
				repartoEstrellaTitular(j);
				if(j.getRol() == Rol.SUPLENTE) {
					j.setMinutos(minEscolta);
					j.setTiempoJugado(minEscolta);
				}
				minEscolta = min - j.getMinutos();
			} else if(j.getPosicion() == Posicion.ALERO) {
				repartoEstrellaTitular(j);
				if(j.getRol() == Rol.SUPLENTE) {
					j.setMinutos(minAlero);
					j.setTiempoJugado(minAlero);
				}
				minAlero = min - j.getMinutos();
			} else if(j.getPosicion() == Posicion.ALAPIVOT) {
				repartoEstrellaTitular(j);
				if(j.getRol() == Rol.SUPLENTE) {
					j.setMinutos(minAlaPivot);
					j.setTiempoJugado(minAlaPivot);
				}
				minAlaPivot = min - j.getMinutos();
			} else {
				repartoEstrellaTitular(j);
				if(j.getRol() == Rol.SUPLENTE) {
					j.setMinutos(minPivot);
					j.setTiempoJugado(minPivot);
				}
				minPivot = min - j.getMinutos();
			}
		}
	}
	
	public Equipo getLocal() {
		return local;
	}
	
	public Equipo getVisitante() {
		return visitante;
	}
	
	public boolean esLocal(Equipo e) {
		return local.equals(e);
	}
	
	public boolean esVisitante(Equipo e) {
		return visitante.equals(e);
	}
	
	public void repartoEstrellaTitular(Jugador jugador) {
		if(jugador.getRol() == Rol.ESTRELLA) {
			minutosEstrella(jugador);
		} 
		if(jugador.getRol() == Rol.TITULAR) {
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
