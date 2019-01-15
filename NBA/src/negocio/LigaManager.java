package negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import datos.BD;
import datos.ParseadorJSON;
import negocio.Equipo.ModeloFinanzasEquipo;
import negocio.Equipo.ModeloTablaEquipo;
import negocio.Renovacion;
import presentacion.PanelTraspasos;

public class LigaManager {

	public static int fase; 
	
	/*
	 * 	fase 0: temporada regular
		fase 1: playoffs
		fase 2: draft
		fase 3: jubilar
		fase 4: renovaciones
		fase 5: agencia libre
		volver a empezar
	 */
	
	static final int limiteJugadoresPorPosicionAgenciaLibre = 150;
	
	public static Usuario usuario;
	
	/* Lista en la que se guardara un objeto temporada por
	 * cada anyo.
	 * Ejemplo: para la temporada
	 *  2018-2019, la clave sera 2018
	 */	
	public static HashMap<Integer, Temporada> temporadasPasadas;
	
	public static Calendario calendario;
	public static Equipo[] equipos;
	public static ArrayList<Jugador> jugadores;
	public static ArrayList<Jugador> agentesLibres;
	public static HashMap<String, Clasificacion> clasificaciones;
	public static ArrayList<Jugador> draft;
	public static Playoffs playoffs;
	
	public static boolean recienCreada;
	public static int anyo;
	public static Date diaActual;
	public static boolean finTemporada;
	
	public static Equipo campeon;
	public static Jugador mvp, roy, dpoy, sextoHombre;
	
	private static TableModel modelo;
	
	public static boolean draftEnCurso = false;
	
	static {
		cargarEquipos();
	}
	
	public static void inicializar(boolean desdeJSON, Usuario u) {
		usuario = u;
		recienCreada = desdeJSON;
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		temporadasPasadas = new HashMap<>();
		cargarJugadores();
		cargarAgentesLibres();
		//cargarEquipos();
		asignarJugadoresAEquipos();
		inicializarClasificaciones();
		//cargar fase
		if(desdeJSON) {
			diaActual = Calendario.PRIMER_DIA;
			calendario = new Calendario(equipos, diaActual); // el calendario es el mismo para todas las temporadas
			anyo = 2018;
			finTemporada = false;
			temporadasPasadas.put(2018, new Temporada());
		} else {
			//de la BD
			//cargar temporadas -> conseguir anyo actual + datos de historial liga (temporadas pasadas)
			//*cargar jugadores
			//*cargar juega -> modificar tid etc y anyadir jugadores a equipos
			
			temporadasPasadas = BD.cargarTemporadasPasadas();
			
			int ultimoAnyo = 0;
			for(int a: temporadasPasadas.keySet()) {
				if(a > ultimoAnyo) {
					ultimoAnyo = a;
				}
			}
			
			diaActual = Calendario.PRIMER_DIA;
			calendario = new Calendario(equipos, diaActual);
			anyo = ultimoAnyo+1;
			finTemporada = false;
			temporadasPasadas.put(anyo, new Temporada());
		}
	}
	
	/** 
	 *  @return true si ha terminado la temporada (incluidos los playoffs), 
	 *  		false si no ha terminado la temporada
	 * */
	public static boolean simularDia() {
		if(!calendario.getDiaActual().after(Calendario.ULTIMO_DIA_TEMP_REGULAR)) { //fase = 0
			// simular dia de temporada regular
			if(calendario.calendario.keySet().contains(calendario.getDiaActual())) {
				for(Partido p: calendario.calendario.get(calendario.getDiaActual())) {
					p.jugar(false);
					calendario.addPartidoJugado(p);
				}
				ordenarClasificaciones();
			}
			calendario.avanzarDia();
			if(calendario.getDiaActual().after(Calendario.ULTIMO_DIA_TEMP_REGULAR)) {
				//fin de la temporada regular
				fase++;
				playoffs = new Playoffs();
				return true;
			}
			return false;
		} else {
			return true;
		}
	}  
	
	private static void cargarJugadores() {
		if(recienCreada) { // cargar los jugadores por defecto desde el JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/jugadores.json");
			JSONArray jugadoresJSON = all.getJSONArray("players");
			jugadores = ParseadorJSON.aArrayListJugador(jugadoresJSON);
		} else {
			// cargar los jugadores y sus atributos desde la BD
			jugadores = BD.cargaJugadoresBD();
		}
	}
	
	private static void cargarEquipos() {
		//if(recienCreada) { //cargar los equipos por defecto desde JSON
		JSONObject all = ParseadorJSON.getObjetoPrimario("data/equipos.json");
		JSONArray equiposJSON = all.getJSONArray("teams");
		equipos = ParseadorJSON.aArrayEquipos(equiposJSON);
	}
	
	private static void asignarJugadoresAEquipos() {
		for(Equipo e: equipos) {
			for(Jugador j: jugadores) {
				if(j.getTid() == e.getTid()) {
					e.jugadores.add(j);
				}
			}
			e.ordenarJugadores();
			e.asignarRoles();
		}
	}
	
	public static Equipo[] crearEquipos(ArrayList<Jugador> jugadores) {
		equipos = new Equipo[30];
		for(int i = 0; i < 30; i++) {
			equipos[i] = new Equipo(i, jugadores);
		}
		return equipos;
	}
	

	private static void cargarAgentesLibres() {
		//con la lista jugadores seleccionar los de tid = -1
		for(Jugador j: jugadores) {
			if(j.getTid() == -1) {
				agentesLibres.add(j);
			}
		}
	}
	
	private static void inicializarClasificaciones() {
		clasificaciones = new HashMap<>();
		ArrayList<Equipo> este, oeste, atl, cent, sure, pac, suro, noro;
		este = new ArrayList<>();
		oeste = new ArrayList<>();
		atl = new ArrayList<>();
		cent = new ArrayList<>();
		sure = new ArrayList<>();
		pac = new ArrayList<>();
		suro = new ArrayList<>();
		noro = new ArrayList<>();
		
		for(Equipo e: equipos) {
			e.victorias = 0;
			e.derrotas = 0;
			if(e.conferencia == Conferencia.ESTE) {
				este.add(e);
			} else {
				oeste.add(e);
			}
			switch(e.division) {
			case ATLANTICO: atl.add(e); break;
			case CENTRAL: cent.add(e); break;
			case SURESTE: sure.add(e); break;
			case PACIFICO: pac.add(e); break;
			case SUROESTE: suro.add(e); break;
			case NOROESTE: noro.add(e); break;
			}
		}
		
		clasificaciones.put("GENERAL", new Clasificacion(equipos));
		clasificaciones.put("ESTE", new Clasificacion(este));
		clasificaciones.put("OESTE", new Clasificacion(oeste));
		clasificaciones.put("ATLANTICO", new Clasificacion(atl));
		clasificaciones.put("CENTRAL", new Clasificacion(cent));
		clasificaciones.put("SURESTE", new Clasificacion(sure));
		clasificaciones.put("PACIFICO", new Clasificacion(pac));
		clasificaciones.put("NOROESTE", new Clasificacion(noro));
		clasificaciones.put("SUROESTE", new Clasificacion(suro));
	}
	
	public static void ordenarClasificaciones() {
		for(Clasificacion c: clasificaciones.values()) {
			c.ordenar();
		}
	}
	
	/**
	 * Metodo para seleccionar el orden del draft de cada equipo y su respectiva eleccion 
	 * */
	public static ArrayList<Jugador> prepararDraft() {
		resetearRookies();
		Equipo[] ordenDraft = new Equipo[30];
		
		Clasificacion general = clasificaciones.get("GENERAL");
		
		for (int i = 0; i < ordenDraft.length; i++) {
			ordenDraft[i] = general.get(i);
			System.out.println("Puesto numero: " + (30-i) + ", " + ordenDraft[i].nombre);
		}	
		
		draft = new ArrayList<>();
		
		ajustarAgenciaLibre(); //en este metodo se crean los jugadores del draft
		jugadores.addAll(draft);
		
		asignarSalariosYContratosDraft();
		draft.sort(new OrdenadorJugadores());
		return elegirDraft(ordenDraft);
		//terminarDraft
	}
	
	/**
	 * Una vez terminada la seleccion de los 60 mejores jugadores
	 * del draft, los demas se mandan a la agencia libre, y se cambia de fase
	 * */
	public static void terminarDraft() {
		mandarAgenciaLibre();
		fase++;
	}
	
	/**
	 * Para evitar la descompensacion entre las diferentes
	 * posiciones a lo largo de las temporadas, hay que ajustar la agencia libre 
	 * al final de cada temporada.
	 * 
	 * Ajustar la agencia libre significa hacer que de cada posicion haya un numero fijo de jugadores:
	 *   - Si hay de mas: eliminando los peores
	 *   - Si hay de menos: creando mas en el draft 
	 * */
	private static void ajustarAgenciaLibre() {
		int[] contPos = contarPosicionesAgenciaLibre();
		
		//quitar los sobrantes
		for(int i = 0; i < 5; i++) {
			while(contPos[i] > limiteJugadoresPorPosicionAgenciaLibre) {
				borrarPeorAgenciaLibre(Quinteto.elegirPosicion(i));
				contPos[i]--;
			}
		}
		
		//crear jugadores nuevos de cada posicion para llegar al limite (draft)
		int nuevos = 0;
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < limiteJugadoresPorPosicionAgenciaLibre - contPos[i]; j++) {
				draft.add(crearJugador(Quinteto.elegirPosicion(i)));
				nuevos++;
			}
		}
		
		while(nuevos < 60) {
			draft.add(crearJugadorDraft());
			nuevos++;
		}
	}
	
	private static void borrarPeorAgenciaLibre(Posicion p) {
		Jugador peor = new Jugador();
		peor.overall = 10000;
		for(Jugador j: agentesLibres) {
			if(j.posicion == p) {
				if(j.overall < peor.overall) {
					peor = j;
					BD.borrarJugador(j);
				}
			}
		}
		agentesLibres.remove(peor);
	}
	
	private static int[] contarPosicionesAgenciaLibre() {
		int[] c = new int[5];
		for(Jugador j: agentesLibres) {
			c[j.posicion.ordinal()]++;
		}
		return c;
	}
	
	private static Jugador crearJugador(Posicion p) {
		int rand;
		Jugador a = new Jugador();
		Jugador b = new Jugador();
		
		do {
			rand = (int) (Math.random()*jugadores.size());
			if(!jugadores.get(rand).equals(null)) {
				a = jugadores.get(rand);
			}
		} while(a.posicion != p);
		
		do {
			rand = (int) (Math.random()*jugadores.size());
			if(!jugadores.get(rand).equals(null)) {
				b = jugadores.get(rand);
			}
		} while(!a.getPosicion().equals(b.getPosicion()));
		
		Jugador c = new Jugador(a, b);
		return c;
	}
	
	/**
	 * @return Jugador del draft, creado a partir de dos jugadores aleatorios de la liga
	 * */
	private static Jugador crearJugadorDraft() {
		int rand;
		Jugador a = null;
		Jugador b = null;
		
		do {
			rand = (int) (Math.random()*jugadores.size());
			if(!jugadores.get(rand).equals(null)) {
				a = jugadores.get(rand);
			}
			rand = (int) (Math.random()*jugadores.size());
			if(!jugadores.get(rand).equals(null)) {
				b = jugadores.get(rand);
			}
		} while(!a.getPosicion().equals(b.getPosicion()));
		
		Jugador c = new Jugador(a, b);
		return c;
	}
	
	/**
	 * Asigna un salario y un contrato en funcion de la calidad del jugador del draft
	 * */
	private static void asignarSalariosYContratosDraft() {
		int salario = 8000;
		int i = 0;
		for (Jugador j : draft) {
			if(salario > 4000) {
				j.salario = salario;
				salario = salario -1000;
			} else if(salario > 1000) {
				j.salario = salario;
				salario = salario - 500;
			} else {
				j.salario = 1000;
			}
			
			if(i < 30) {
				j.anyosContratoRestantes = 3;
			} else {
				j.anyosContratoRestantes = 2;
			}
			i++;
		}
	}
	
	/**
	 * Devuelve un ArrayList con los 60 mejores jugadores del draft en orden
	 * (2 rondas completas)
	 * */
	private static ArrayList<Jugador> elegirDraft(Equipo[] orden) {
		ArrayList<Jugador> jugs = new ArrayList<Jugador>();
		int j = 1;
		do {
			for (int i = 29; i >= 0; i--) {
				Jugador jug = elegirMejorDisponible(draft);
				jugs.add(jug);
				//jug.setTid(orden[i].getTid());
				//orden[i].jugadores.add(jug);
			}
			j++;
		} while(j <= 2);
		return jugs;
	}
	
	/**
	 * @return Jugador , el mejor disponible en el draft
	 * */
	public static Jugador elegirMejorDisponible(ArrayList<Jugador> jugs) {
		Jugador mejor = new Jugador();
		mejor.overall = -100;
		for (Jugador j : jugs) {
			if(mejor.overall < j.overall) {
				mejor = j;
			}			
		}
		jugs.remove(mejor);
		return mejor;
	}

	private static void mandarAgenciaLibre() {
		for (Jugador j : draft) {
			if(j.getTid() == -1) {
				if(!agentesLibres.contains(j)) {
					agentesLibres.add(j);
				}
			}
		}
		draft.clear();
	}
	
	/**
	 * Selecciona que jugadores se jubilan este anyo
	 * tid = -2 -> jugadores jubilados
	 * 
	 * Devuelve un array de los jugadores retirados
	 * del equipo del usuario
	 * */
	public static ArrayList<Jugador> jubilar() {
		ArrayList<Jugador> retirados = new ArrayList<>();
		boolean retirado;
		double rand;
		for (Equipo e : equipos) {
			for (Jugador j : e.jugadores) {
				rand = Math.random();
				retirado = false;
				if(j.getEdad() >= 34) {	
					if(j.getEdad() > 40) {
						j.setTid(-2);
						j.salario = 0;
						retirado = true;
					} else if(j.getValoracion() >= 2500 && rand > 0.9 ) {
						j.setTid(-2);
						j.salario = 0;
						retirado = true;
					} else if(j.getValoracion() >= 1000 && rand > 0.6) {
						j.setTid(-2);
						j.salario = 0;
						retirado = true;
					} else if(rand > 0.2 ) {
						j.setTid(-2);
						j.salario = 0;
						retirado = true;
					}
					if(retirado && e.getTid() == usuario.getEquipo().getTid()) {
						retirados.add(j);
					}
					if(retirado) {
						BD.borrarJugador(j);
					}
				}
			}
		}
		
		eliminarJugadores(); //borra de los arrays de cada equipo
		actualizarSalarios();
		
		for (Jugador j : agentesLibres) {
			rand = Math.random();
			if(j.getEdad() > 36 && rand > 0.5) {
				j.setTid(-2);
				BD.borrarJugador(j);
			} else if(j.getEdad() > 38) {
				j.setTid(-2);
				BD.borrarJugador(j);
			}
		}
		
		//eliminar de los arraylists jugadores y agentesLibres los jugadores retirados (tid == -2)
		for(int i = jugadores.size()-1; i >= 0; i--) {
			if(jugadores.get(i).getTid()==-2) {
				jugadores.remove(i);
			}
		}
		for(int i = agentesLibres.size()-1; i >= 0; i--) {
			if(agentesLibres.get(i).getTid()==-2) {
				agentesLibres.remove(i);
			}
		}

		return retirados;
	}
	
	public static void actualizarSalarios() {
		for (Equipo e : equipos) {
			e.salarioTotal = 0;
			e.calcSalarioTotal();
		}
	}
	
	/**
	 * Renueva los contratos de los jugadores
	 * */
	public static void renovaciones(ArrayList<Equipo> orden, boolean antesQueUsuario) {
		double rand;
		
		int inicio, fin;
		int posUsuario = orden.indexOf(usuario.getEquipo());
		if(antesQueUsuario) {
			//que procese todos los previos al usuario
			inicio = 0;
			fin = posUsuario;
		} else {
			//que procese todos los que estan detras del usuario
			inicio = posUsuario+1;
			fin = orden.size();
		}
		for(int i = inicio; i < fin; i++) {
			Equipo e = orden.get(i);
			for(Jugador j: e.jugadores) {
				actualizarSalarios();
				if((Equipo.limiteSalarial - e.salarioTotal) > -45000) {
					rand = Math.random();
					if(j.anyosContratoRestantes == 0) {
						if(j.getValoracion() >= 3500 && rand <= 0.8) {
							if(j.getEdad() > 35) {
								//Renueva por un anyo y 30-35
								j.anyosContratoRestantes = 1;
								j.salario = (30000 + ((int)(Math.random()*5001)));
							} else if(j.getEdad() > 30 && rand <= 0.85) {
								//Renueva por 3 anyos 35-40
								j.anyosContratoRestantes = 3;
								j.salario = (35000 + ((int)(Math.random()*5001)));
							} else if(rand <= 0.9) {
								//Renueva 5 anyos 35-40
								j.anyosContratoRestantes = 5;
								j.salario = (35000 + ((int)(Math.random()*5001)));
							}
						} else if(j.getValoracion() >= 2500) {
							if(j.getEdad() > 35 && rand <= 0.7) {
								//Renueva por 1 anyo y 15-20
								j.anyosContratoRestantes = 1;
								j.salario = (15000 + ((int)(Math.random()*5001)));
							} else if(j.getEdad() > 30 && rand <= 0.75) {
								//Renueva por 3 anyos y 20-25
								j.anyosContratoRestantes = 3;
								j.salario = (20000 + ((int)(Math.random()*5001)));
							} else if(rand <= 0.8) {
								//Renueva por 5 anyos y 25-30
								j.anyosContratoRestantes = 5;
								j.salario = (25000 + ((int)(Math.random()*5001)));
							}
						} else if(j.getValoracion() >= 1000) {
							if(j.getEdad() > 35 && rand <= 0.6) {
								//Renueva por 1 anyo y 5-10
								j.anyosContratoRestantes = 1;
								j.salario = (5000 + ((int)(Math.random()*5001)));
							} else if(j.getEdad() > 30 && rand <= 0.65) {
								//Renueva por 3 anyos y 6-11
								j.anyosContratoRestantes = 3;
								j.salario = (6000 + ((int)(Math.random()*5001)));
							} else if(rand <= 0.7) {
								//Renueva por 5 anyos y 11-16
								j.anyosContratoRestantes = 5;
								j.salario = (11000 + ((int)(Math.random()*5001)));
							}
						} else if(j.getValoracion() > 0){
							if(j.getEdad() > 35 && rand <= 0.4) {
								//Renueva por 1 anyo y 1-5
								j.anyosContratoRestantes = 1;
								j.salario = (1000 + ((int)(Math.random()*4001)));
							} else if(j.getEdad() > 30 && rand <= 0.45) {
								//Renueva por 2 anyos y 2-8
								j.anyosContratoRestantes = 2;
								j.salario = (2000 + ((int)(Math.random()*6001)));
							} else if(rand <= 0.5) {
								//Renueva por 3 anyos y 5-10
								j.anyosContratoRestantes = 3;
								j.salario = (5000 + ((int)(Math.random()*5001)));
							}	
						} else {
							if(j.getEdad() > 35 && rand <= 0.25) {
								//Renueva por 1 anyo y 1
								j.anyosContratoRestantes = 1;
								j.salario = 1000;
							} else if(j.getEdad() > 30 && rand <= 0.3) {
								//Renueva por 2 anyos y 1
								j.anyosContratoRestantes = 2;
								j.salario = 1000;
							} else if(rand <= 0.45) {
								//Renueva por 3 anyos y 1-2 
								j.anyosContratoRestantes = 3;
								j.salario = 1000 + ((int)(Math.random()*1001));
							}	
						}
					}
				}
				if(j.anyosContratoRestantes==0) {
					j.setTid(-1);
					j.salario = 0;
				}
			}
		}
		
		for(Equipo e: equipos) {
			for(Jugador j: e.jugadores) {
				if(j.getTid() == -1) {
					agentesLibres.add(j);
				}
			}
		}
		eliminarJugadores();
		
		fase++;
	}
	
	/**
	 * Actualiza los equipos, eliminando los jugadores que pasan a ser agentes libres
	 * */
	private static void eliminarJugadores() {
		boolean actualizar = false;
		do {
			actualizar = false;
			for (Equipo e : equipos) {
				for (Jugador j : e.jugadores) {
					if(j.salario == 0) {
						actualizar = true;
						e.jugadores.remove(j);
						break;
					}
				}
			}	
		} while(actualizar);
	}
	
	/**
	 * Metodo que simula la agencia libre
	 * */
	public static void agenciaLibre(ArrayList<Equipo> orden, boolean antesQueUsuario) {
		agentesLibres.clear();
		cargarAgentesLibres();
		
		int inicio, fin;
		int posUsuario = orden.indexOf(usuario.getEquipo());
		if(antesQueUsuario) {
			inicio = 0;
			fin = posUsuario;
		} else {
			inicio = posUsuario+1;
			fin = orden.size();
		}
		for(int i = inicio; i < fin; i++) {
			Equipo e = orden.get(i);
			actualizarSalarios();
			eleccionAgenciaLibre(e);
		}

		fase++;
	}
	
	/**
	 * Busca las posiciones que necesita reforzar el equipo
	 * y a continuacion ficha a jugadores de esa posicion
	 * */
	private static void eleccionAgenciaLibre(Equipo equipo) {
		int contBase, contEscolta, contAlero, contAP, contPivot;
		contBase = contEscolta = contAlero = contAP = contPivot = 0;
		
		for (Jugador j : equipo.jugadores) {
			if (j.posicion.equals(Posicion.BASE)) {
				contBase++;
			} else if(j.posicion.equals(Posicion.ESCOLTA)) {
				contEscolta++;
			} else if(j.posicion.equals(Posicion.ALERO)) {
				contAlero++;
			} else if(j.posicion.equals(Posicion.ALAPIVOT)) {
				contAP++;
			} else if(j.posicion.equals(Posicion.PIVOT)){
				contPivot++;
			}
		}
		ArrayList<Jugador> fichados = new ArrayList<>();
		
		if(equipo.salarioTotal < Equipo.limiteSalarial) {
			for (Jugador jugador : agentesLibres) {
				int sal = salarioAL(jugador);
				if(equipo.salarioTotal + sal < Equipo.limiteSalarial && jugador.getOverall() > 78) {
					equipo.jugadores.add(jugador);
					jugador.setTid(equipo.getTid());
					jugador.salario = sal;
					jugador.anyosContratoRestantes =  (int) (Math.random()*5)+1;
					fichados.add(jugador);
					actualizarSalarios();
				}
			}
		}
		
		for (Jugador jugador : fichados) {
			agentesLibres.remove(jugador);
		}
		
		if((equipo.salarioTotal+1000) < Equipo.limiteSalarial) {
			while(contBase < 2 || contEscolta < 2 || contAlero < 2 || contAP < 2 || contPivot < 2) {
				if((equipo.salarioTotal+1000) >= Equipo.limiteSalarial) {
					ajustarPlantilla(contBase, contEscolta, contAlero, contAP, contPivot, equipo);
					break;
				}
				if(contBase < 2) {
					ficharAgenteLibre(equipo, Posicion.BASE);
					contBase++;
				} else if(contEscolta < 2) {
					ficharAgenteLibre(equipo, Posicion.ESCOLTA);
					contEscolta++;
				} else if(contAlero < 2) {
					ficharAgenteLibre(equipo, Posicion.ALERO);
					contAlero++;
				} else if(contAP < 2) {
					ficharAgenteLibre(equipo, Posicion.ALAPIVOT);
					contAP++;
				} else if(contPivot < 2) {
					ficharAgenteLibre(equipo, Posicion.PIVOT);
					contPivot++;
				}
			}
		} else {
			ajustarPlantilla(contBase, contEscolta, contAlero, contAP, contPivot, equipo);
		}
	}
	
	/**
	 * Si un equipo no tiene dinero, pero necesita fichar jugadores para su plantilla,
	 * fichara a jugadores de "baja" calidad (overall < 60) por el minimo salarial 
	 * establecido en 1M $ por un unico anyo de contrato
	 * */
	private static void ajustarPlantilla(int contBase, int contEscolta, int contAlero, int contAP, int contPivot, Equipo equipo) {
		while(contBase < 2 || contEscolta < 2 || contAlero < 2 || contAP < 2 || contPivot < 2) {
			if(contBase < 2) {
				ficharParaRellenar(equipo, Posicion.BASE);
				contBase++;
			} else if(contEscolta < 2) {
				ficharParaRellenar(equipo, Posicion.ESCOLTA);
				contEscolta++;
			} else if(contAlero < 2) {
				ficharParaRellenar(equipo, Posicion.ALERO);
				contAlero++;
			} else if(contAP < 2) {
				ficharParaRellenar(equipo, Posicion.ALAPIVOT);
				contAP++;
			} else if(contPivot < 2) {
				ficharParaRellenar(equipo, Posicion.PIVOT);
				contPivot++;
			}
		}
	}
	
	/**
	 * Metodo para fichar jugadores
	 * */
	private static void ficharAgenteLibre (Equipo equipo, Posicion p) {
		agentesLibres.sort(new OrdenadorJugadores());
		int sal;
		int anyosDeContrato;
		for (Jugador j : agentesLibres) {
			if(j.getPosicion().equals(p)) {
				sal = salarioAL(j);
				if((equipo.salarioTotal + sal) < Equipo.limiteSalarial) {
					anyosDeContrato = (int) (Math.random()*5)+1;
					j.setTid(equipo.getTid());
					j.salario = sal;
					j.anyosContratoRestantes = anyosDeContrato;
					equipo.jugadores.add(j);
					actualizarSalarios();
					agentesLibres.clear();
					cargarAgentesLibres();
					return;
				}
			}
		}
		//si se llega aqui, fichar si o si
		for(Jugador j: agentesLibres) {
			if(j.getPosicion().equals(p) && j.overall <= 70) {
				anyosDeContrato = (int) (Math.random()*5)+1;
				j.setTid(equipo.getTid());
				j.salario = 1000;
				j.anyosContratoRestantes = 1;
				equipo.jugadores.add(j);
				actualizarSalarios();
				agentesLibres.clear();
				cargarAgentesLibres();
				break;
			}
		}
	}
	
	/**
	 * @return int con las exigencias salariales del jugador
	 * para fichar por el equipo
	 * */
	private static int salarioAL(Jugador j) {
		int salario = 0;
		if(j.getOverall() >= 85) {
			//estrella
			salario = 30000 + (int)(Math.random()*10001);
		} else if(j.getOverall() >= 80) {
			//normal-estrella
			salario = 20000 + (int)(Math.random()*10001);
		} else if(j.getOverall() >= 70) {
			//normal
			salario = 10000 + (int)(Math.random()*10001);
		} else if (j.getOverall() >= 60){
			//normal-malo
			salario = 1000 + (int)(Math.random()*12001);
		} else {
			//malo
			salario = 1000 + (int)(Math.random()*4001);
		}
		return salario;
	}

	
	private static void ficharParaRellenar(Equipo equipo, Posicion p) {
		int overall = 59;
		boolean fichado = false;
		Jugador peor = new Jugador();
		peor.overall = 1000;
		
		for(int i = 0; i < 100-overall; i++) {
			for (Jugador jugador : agentesLibres) {
				if(jugador.getPosicion() == p) {
					if(jugador.getOverall() <= overall+i) {
						jugador.setTid(equipo.getTid());
						jugador.salario = 1000;
						jugador.anyosContratoRestantes = 1;
						equipo.jugadores.add(jugador);
						actualizarSalarios();
						agentesLibres.clear();
						cargarAgentesLibres();
						fichado = true;
						break;
					}
					if(peor.getOverall() > jugador.getOverall()) {
						peor = jugador;
					}
				}
			}
			if(fichado) {
				break;
			}
		}
		
		//si se llega aqui y no se ha fichado, fichar si o si
		if(!fichado) {
			Jugador jugador = peor;
			jugador.setTid(equipo.getTid());
			jugador.salario = 1000;
			jugador.anyosContratoRestantes = 1;
			equipo.jugadores.add(jugador);
			actualizarSalarios();
			agentesLibres.clear();
			cargarAgentesLibres();
		}
	}
	
	
	/**
	 * Metodo para que ningun equipo de la liga
	 * supere el maximo de 15 jugadores
	 * */
	public static void despedirJugadores(ArrayList<Equipo> orden, boolean antesQueUsuario) {
		Integer[] contPos = new Integer[5];
		actualizarRoles();
	
		int inicio, fin;
		int posUsuario = orden.indexOf(usuario.getEquipo());
		if(antesQueUsuario) {
			inicio = 0;
			fin = posUsuario;
		} else {
			inicio = posUsuario+1;
			fin = orden.size();
		}
		for(int j = inicio; j < fin; j++) {
			Equipo e = orden.get(j);
			for (int i = 0; i < contPos.length; i++) {
				contPos[i] = 0;
			}
			contPos = contarPosiciones(e, contPos);
			
			int maxJugadores = 0;
			for (int i = 0; i < contPos.length; i++) {
				maxJugadores = maxJugadores+contPos[i];
			}
			
			cortarJugador(e, maxJugadores);
		}
	}
	
	/**
	 * Metodo para mandar los jugadores sobrantes a la 
	 * agencia libre
	 * */
	private static void cortarJugador(Equipo e, int maxJugadores) {
		ArrayList<Jugador> jugadoresBorrar = new ArrayList<Jugador>();
		while(maxJugadores > 15) {
			actualizarRoles();
			for (Jugador j : e.jugadores) {
				if(j.getRol().equals(Rol.NOJUEGA)) {
					j.setTid(-1);
					j.anyosContratoRestantes = 0;
					j.salario = 0;
					j.rol = null;
					maxJugadores--;
					jugadoresBorrar.add(j);
					break;
				}
			}

			for (Jugador jugador : jugadoresBorrar) {
				e.jugadores.remove(jugador);
				if(!agentesLibres.contains(jugador)) {
					agentesLibres.add(jugador);
				}
			}
		}
	}
	
	/**
	 * Metodo para contar el numero de jugadores por
	 * posicion de un equipo
	 * contPos[0] -> bases
	 * contPos[1] -> escoltas
	 * contPos[2] -> aleros
	 * contPos[3] -> alapivots
	 * contPos[4] -> pivots
	 * */
	public static Integer[] contarPosiciones(Equipo e, Integer[] contPos) {
		for (Jugador j : e.jugadores) {
			if(j.getPosicion().equals(Posicion.BASE)) {
				contPos[0] = contPos[0] + 1;
			} else if(j.getPosicion().equals(Posicion.ESCOLTA)) {
				contPos[1] = contPos[1] + 1;
			} else if(j.getPosicion().equals(Posicion.ALERO)) {
				contPos[2] = contPos[2] + 1;
			} else if(j.getPosicion().equals(Posicion.ALAPIVOT)) {
				contPos[3] = contPos[3] + 1;
			} else if(j.getPosicion().equals(Posicion.PIVOT)){
				contPos[4] = contPos[4] + 1;
			}
		}
		return contPos;
	}
	
	public static void actualizarAnyosContrato() {
		for(Equipo e: equipos) {
			for(Jugador j: e.jugadores) {
				j.anyosContratoRestantes--;
			}
		}
	}
	
	private static void resetearRookies() {
		for(Equipo e: equipos) {
			for(Jugador j: e.jugadores) {
				j.rookie = false;
			}
		}
	}
	
	/**
	 * Metodo que elige el MVP segun el rendimiento del jugador
	 * */
	private static Jugador elegirMVP() {
		Jugador mvp = new Jugador();
		for (Equipo equipo : equipos) {
			for (Jugador j : equipo.jugadores) {
				//j.getValoracion() = (j.getPuntosPartido() + j.getAsistenciasPartido() + j.getRebotesPartido());
				if(j.getRol().equals(Rol.ESTRELLA) || j.getRol().equals(Rol.TITULAR)) {
					if(j.getValoracion() > mvp.getValoracion() && equipo.getVictorias() > 41) {
						mvp = j;
					}
				}
			}
		}
		LigaManager.mvp = mvp;
		for(Temporada t: temporadasPasadas.values()) {
			System.out.println(t.mvp);
		}
		System.out.println("real:"+mvp.getNombre());
		temporadasPasadas.get(anyo).setMVP(mvp.getNombre());
		return mvp;
	}
	
	/**
	 * Metodo que elige al mejor sexto hombre de la temporada
	 */
	private static Jugador elegirSextoHombre() {
		Jugador sextoHombre = new Jugador();
		for (Equipo equipo : equipos) {
			for (Jugador j : equipo.jugadores) {
				//j.getValoracion() = (j.getPuntosPartido() + j.getAsistenciasPartido() + j.getRebotesPartido());
				if(j.getRol().equals(Rol.SUPLENTE)) {
					if(j.getValoracion() > sextoHombre.getValoracion() && equipo.getVictorias() > 41) {
						sextoHombre = j;
					}
				}
			}
		}
		LigaManager.sextoHombre = sextoHombre;
		temporadasPasadas.get(anyo).setSextoHombre(sextoHombre.getNombre());
		return sextoHombre;
	}
	
	/**
	 * Metodo que elige al novato del anyo
	 * */
	private static Jugador elegirROY() {
		Jugador ROY = new Jugador();
		for (Equipo equipo : equipos) {
			for (Jugador j : equipo.jugadores) {
				//j.valoracion = (j.getPuntosPartido() + j.getAsistenciasPartido() + j.getRebotesPartido());
				if(j.rookie) {
					if(j.getValoracion() > ROY.getValoracion() && equipo.getVictorias() > 41) {
						ROY = j;
					}
				}
			}
		}
		LigaManager.roy = ROY;
		temporadasPasadas.get(anyo).setROY(ROY.getNombre());
		return ROY;
	}
	
	/**
	 * Metodo que elige al defensor del anyo
	 * */
	private static Jugador elegirDPOY() {
		Jugador DPOY = new Jugador();
		for (Equipo equipo : equipos) {
			for (Jugador j : equipo.jugadores) {
				if(j.getDefensa() > DPOY.getDefensa() && equipo.getVictorias() > 41) {
					DPOY = j;
				}
			}
		}
		LigaManager.dpoy = DPOY;
		temporadasPasadas.get(anyo).setDPOY(DPOY.getNombre());
		return DPOY;
	}
	
	public static void elegirPremiosIndividuales() {
		elegirMVP();
		elegirROY();
		elegirSextoHombre();
		elegirDPOY();
	}
	
	public static String getNombrePorID(int id) {
		for(Jugador j: jugadores) {
			if(j.getID() == id) {
				return j.getNombre();
			}
		}
		return "Sin nombre";
	}
	
	public static String getNombreEquipoPorTid(int tid) {
		if(tid < 0 || tid > 29) return "";
		return equipos[tid].getNombre();
	}
	
	/**
	 * Metodo que comprueba que el equipo del usuario esta en condiciones
	 * de comenzar una nueva temporada:
	 * Devuelve: 
	 * 	-> 0 si todo esta bien
	 * 	-> -1 si se pasa del limite salarial
	 *  -> -2 si tiene mas de 15 jugadores
	 *  -> -3 si no tiene como minimo 2 jugadores por posicion
	 *  -> -4 si tiene jugadores con 0 anyos de contrato -> tiene que cambiar eso
	 * */
	public static int comprobarEquipoUsuario() {
		Equipo e = usuario.getEquipo();
		int contadorJugadoresConContrato = 0;
		for(Jugador j: e.jugadores) {
			if(j.getAnyosContratoRestantes() <= 0) {
				return -4;
			}
		}
		for(Jugador j: e.jugadores) {
			if(j.anyosContratoRestantes >= 0) { // 0 o 1???
				contadorJugadoresConContrato++;
			}
		}
		boolean noTiene2PorPosicion = false;
		int[] pos = new int[5];
		for(Jugador j: e.jugadores) {
			pos[j.posicion.ordinal()]++;
		}
		for(int i: pos) {
			if(i < 2) {
				noTiene2PorPosicion = true;
				break;
			}
		}
		if(e.calcSalarioTotal() > Equipo.limiteSalarial) {
			return -1;
		} else if(contadorJugadoresConContrato > 15) {
			return -2;
		} else if(noTiene2PorPosicion){
			return -3;
		}
		return 0;
	}
	
	public static void guardarDatosFinTemporada() {
		Equipo u = usuario.getEquipo();
		temporadasPasadas.get(anyo).guardaBalanceUsuario(u.getVictorias(), u.getDerrotas());
		temporadasPasadas.get(anyo).guardaCampeon(campeon);
		temporadasPasadas.get(anyo).guardaClasificacion(equipos);
	}
	
	/**
	 * Metodo que resetea todos los datos correspondientes a la temporada actual, 
	 * que se ejecutara cada vez que termine una temporada, para empezar una nueva.
	 * */
	public static void reset() {		
		for(Jugador j: jugadores) {
			j.guardaStatsTemporada();
		}
		for(int i = usuario.getEquipo().getJugadores().size()-1; i>=0; i--) {
			if(usuario.getEquipo().getJugadores().get(i).getAnyosContratoRestantes() <= 0) {
				usuario.getEquipo().getJugadores().get(i).setTid(-1);
				usuario.getEquipo().getJugadores().get(i).salario = 0;
				usuario.getEquipo().getJugadores().get(i).anyosContratoRestantes = 0;
				agentesLibres.add(usuario.getEquipo().getJugadores().get(i));
				usuario.getEquipo().getJugadores().remove(i);
			}
		}
		eliminarJugadores();
		//los equipos se mantienen igual, las plantillas ya estan actualizadas
		calendario.reset();
		inicializarClasificaciones();
		resetearJugadores();
		//evolucionar atributos de jugadores + agentes libres (?)
		campeon = null;
		mvp = null;
		roy = null;
		dpoy = null;
		sextoHombre = null;
		//playoffs = null;
		
		fase = 0;
		anyo++;
		
		Equipo e = usuario.getEquipo();
		for(Renovacion reno: e.renovacionesPendientes) {
			for(Jugador j: e.getJugadores()) {
				if(j.getNombre().equals(reno.getNombre())) {
					j.salario = reno.getCantidad();
					j.anyosContratoRestantes = reno.getAnyos();
				}
			}
		}
		
		actualizarRoles();
		
		recienCreada = false;
		diaActual = Calendario.PRIMER_DIA;
		calendario.diaActual = Calendario.PRIMER_DIA;
		
		PanelTraspasos.actualizarCombo();
		
		temporadasPasadas.put(anyo, new Temporada());
	}
	
	private static void resetearJugadores() {
		for(Jugador j: jugadores) {
			j.puntosPartido = 0;
			j.asistenciasPartido = 0;
			j.rebotesPartido = 0;
			j.puntosTemporada = 0;
			j.asistenciasTemporada = 0;
			j.rebotesTemporada = 0;
			j.partidosJugadosTemporada = 0;
		}
	}

	private static void actualizarRoles() {
		for(Equipo e: equipos) {
			e.asignarRoles();
		}
	}
	
	public static Jugador getJugadorConNombre(String n) {
		for(Jugador j: jugadores) {
			if(j.nombre.equals(n)) {
				return j;
			}
		}
		return null;
	}
	
	/**
	 * c:
	 * 	0 -> puntos
	 * 	1 -> rebotes
	 * 	2 -> asistencias
	 * */
	public static Jugador[] getMejoresEn(int c) {
		ArrayList<Jugador> jugs = new ArrayList<>();
		
		for(int i = 0; i < 5; i++) {
			Jugador max = new Jugador();
			for(Jugador j: jugadores) {
				if(j.tid >= 0 && !jugs.contains(j)) { //esta jugando
					if(c == 0) {
						//puntos
						if(j.puntosTemporada > max.puntosTemporada) {
							max = j;
						}
					} else if(c == 1) {
						//rebotes
						if(j.rebotesTemporada > max.rebotesTemporada) {
							max = j;
						}
					} else {
						//asistencias
						if(j.asistenciasTemporada > max.asistenciasTemporada) {
							max = j;
						}
					}
				}
			}
			jugs.add(max);
		}
		
		Jugador[] j = new Jugador[5];
		for(int i = 0; i < 5; i++) {
			j[i] = jugs.get(i);
		}
		return j;
	}
	
	public static void guardarBD() {
		BD.guardarTemporada();
		for(Jugador j: jugadores) {
			BD.guardarJuega(j);
		}
	}
	
	public static TableModel getModeloTablaAgenciaLibre() {
		if(modelo == null) {
			modelo = new ModeloTablaAgenciaLibre();
		}
		return modelo;
	}
	
	public static class ModeloTablaAgenciaLibre implements TableModel {

		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0: return "Nombre";
			case 1: return "Edad";
			case 2: return "Posicion";
			case 3: return "Overall";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return agentesLibres.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			LigaManager.agentesLibres.sort(new OrdenadorAgenciaLibre());
			Jugador j = agentesLibres.get(rowIndex);
			switch(columnIndex) {
			case 0: return j.nombre;
			case 1: return j.getEdad();
			case 2: return j.posicion;
			case 3: return j.overall;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}
		
	}

}