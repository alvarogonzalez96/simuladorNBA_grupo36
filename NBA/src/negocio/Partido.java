package negocio;

public class Partido {

	protected Equipo local;
	protected Equipo visitante;
	
	protected Quinteto quintetoLocal, quintetoVisitante;
	
	protected int puntosLocal, puntosVisitante;
	protected boolean atacaLocal;
		
	public Partido(Equipo local, Equipo visitante) {
		
		this.local = local;
		this.visitante = visitante;
		
		asignarMinutos(local);
		asignarMinutos(visitante);
		
		/*for(Jugador j: local.jugadores) {
			System.out.println(j.nombre +" "+j.minutos+" "+j.posicion);
		}*/
		
		puntosLocal = puntosVisitante = 0;
		atacaLocal = true;
		
		//jugar();
	}
	
	public void jugar() {
		int tiempo = 0; 

		quintetoLocal = new Quinteto(local);
		quintetoVisitante = new Quinteto(visitante);

		//48 min * 60 = 2880 segundos
		while(tiempo < 2880) {

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

			quintetoLocal.actualizar(tiempo);
			quintetoVisitante.actualizar(tiempo);
		}
		while(puntosLocal == puntosVisitante) {
			System.out.println("empate");
			if(atacaLocal) {
				simularJugada(quintetoLocal, quintetoVisitante);
			} else {				
				simularJugada(quintetoVisitante, quintetoLocal);
			}
			atacaLocal = !atacaLocal;
		}
		System.out.println("Marcador final: "+puntosVisitante+"-"+puntosLocal);
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
	
	public void tirosLibres(Quinteto atacando, Quinteto defendiendo) {
		double meterTiroLibre = 0.772;
		double random = Math.random();
		
		//Primer tiro libre
		
		/*
		 * Podríamos hacer algo así if(random <= (meterTiroLibre + habilidadTiroLibre))
		 * La habilidad de tiro libre puede ser positiva o negativa en funicón de qué jugador esté tirando; es decir,
		 * si Curry en la vida real mete un 90% de tiro libre, al 0.722 sumarle un rango de valores que hagan que se aproxime
		 * cada jugador a su porcentaje real, lo mismo podríamos hacer con los tiros de dos y de tres.
		 * Para tomar la decisión de qué jugador tira, podríamos ordenarlos de mayor a menor éxito según sean tiros de 3 o de 2 y si son
		 * jugadores estrella o no
		 */
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
