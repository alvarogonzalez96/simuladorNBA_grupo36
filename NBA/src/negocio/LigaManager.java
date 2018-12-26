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

import datos.ParseadorJSON;
import negocio.Equipo.ModeloFinanzasEquipo;
import negocio.Equipo.ModeloTablaEquipo;
import presentacion.PanelNoticiario;
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
	 * Ejemplo: para la temporada 2018-2019, la clave sera 2018
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
	
	public static Equipo campeon;
	public static Jugador mvp, roy, dpoy, sextoHombre;
	
	public static ArrayList<String> noticiasAgenciaLibre, noticiasDraft;
	
	private static TableModel modelo;
	
	public static void inicializar(boolean desdeJSON, Usuario u) {
		usuario = u;
		recienCreada = desdeJSON;
		jugadores = new ArrayList<>();
		agentesLibres = new ArrayList<>();
		temporadasPasadas = new HashMap<>();
		temporadasPasadas.put(2018, new Temporada());
		cargarJugadores();
		cargarAgentesLibres();
		cargarEquipos();
		asignarJugadoresAEquipos();
		inicializarClasificaciones();
		//cargar fase
		if(desdeJSON) {
			diaActual = Calendario.PRIMER_DIA;
			calendario = new Calendario(equipos, diaActual); // el calendario es el mismo para todas las temporadas
			anyo = 2018;
		} else {
			//de la BD
			diaActual = null; //cargar el dia actual en la BD
			anyo = 0;
		}
		
		/*boolean m = simularDia();
		while(!m) {
			m = simularDia();
		}*/
		
		//reset();
		//nuevaTemporada();
	}
	
	/** 
	 *  @return true si ha terminado la temporada (incluidos los playoffs), 
	 *  		false si no ha terminado la temporada
	 * */
	public static boolean simularDia() {
		System.out.println("Simulando "+calendario.getDiaActual());
		System.out.println(calendario.ultimosPartidosJugados.size());
		if(!calendario.getDiaActual().after(Calendario.ULTIMO_DIA_TEMP_REGULAR)) { //fase = 0
			// simular dia de temporada regular
			if(calendario.calendario.keySet().contains(calendario.getDiaActual())) {
				for(Partido p: calendario.calendario.get(calendario.getDiaActual())) {
					p.jugar(false);
					calendario.addPartidoJugado(p);
				}
				ordenarClasificaciones();
				System.out.println(clasificaciones.get("GENERAL").getEquipos().get(3).getAbrev());
			}
			calendario.avanzarDia();
			if(calendario.getDiaActual().after(Calendario.ULTIMO_DIA_TEMP_REGULAR)) {
				//fin de la temporada regular
				fase++;
				playoffs = new Playoffs();
				return true;
			}
			return false;
		} else { // eliminar este rama de la alternativa cuando este terminado el juego
			//Eleccion de premios al final de temporada
			System.out.println();
			System.out.println("-----------------------------------------");
			System.out.println("PREMIOS DE LA TEMPORADA REGULAR");
			System.out.println("MVP: " + elegirMVP().getNombre());
			System.out.println("Sexto Hombre: " + elegirSextoHombre().getNombre());
			System.out.println("ROY: " + elegirROY().getNombre());
			System.out.println("DPOY: " + elegirDPOY().getNombre());
			
			System.out.println();
			System.out.println("-----------------------------------------");
			for (Equipo e : equipos) {
				System.out.println("JUGADORES DE "+e.nombre.toUpperCase()+":");
				for (Jugador j : e.jugadores) {
					if(!j.rol.equals(Rol.NOJUEGA)) {
						System.out.println(j.getNombre() + " " + (double)Math.round((j.getPuntosPartido()/82)*100)/100 + " ppp" + " " + (double)Math.round((j.getAsistenciasPartido()/82)*100)/100 + " app" + " " + (double)Math.round((j.getRebotesPartido()/82)*100)/100 + " rpp ; val: "+j.getValoracion());
					}
				}
				System.out.println();
			}
			
			System.out.println();
			System.out.println("-----------------------------------------");
			for(String tipoClasif: clasificaciones.keySet()) {
				System.out.println();
				System.out.println("CLASIFICACION "+tipoClasif);
				clasificaciones.get(tipoClasif).imprimir();
			}
			
			// playoffs / verano
			//jubilar();
			playOffs(); //fase = 1
			//draft();
			//renovaciones();
			//agenciaLibre();
			
			for (Equipo e : equipos) {
				for (Jugador jugador : e.jugadores) {
					jugador.rol = null;
				}
				e.ordenarJugadores();
				e.asignarRoles();
				System.out.println();
				System.out.println(e.nombre);
				for (Jugador j : e.jugadores) {
					System.out.println(j.nombre + "(" + j.posicion + "), rol: " + j.getRol() + ", overall: " + j.overall);
				}
			}
			
			if(fase == 2) {
				//draft
			} else if(fase == 3) {
				//jubilar
			} else if(fase == 4){
				//renovaciones
			} else {
				//agencia libre
			}
			
			return true;
		}
	}  
	
	@Deprecated
	/**
	 * Metodo para jugar los playOffs
	 * */
	public static void playOffs() {
		Equipo[] oeste = new Equipo[8];
		Equipo[] este = new Equipo[8];
		
		Clasificacion cOeste = clasificaciones.get("OESTE");
		Clasificacion cEste = clasificaciones.get("ESTE");
		
		for (int i = 0; i < oeste.length; i++) {
			oeste[i] = cOeste.get(i);
			este[i] = cEste.get(i);
		}

		//Cuartos de final de conferencia
		Equipo[] semisOeste = new Equipo[4];
		Equipo[] semisEste = new Equipo[4];
		System.out.println();
		System.out.println("*CUARTOS DE FINAL DE CONFERENCIA*");
		int j = 7;
		for (int i = 0; i < semisOeste.length; i++) {
			semisOeste[i] = ganadorSerie(oeste[i], oeste[j]);
			System.out.println("Gana la serie: " + semisOeste[i].getNombre());
			System.out.println();
			semisEste[i] = ganadorSerie(este[i], este[j]);
			System.out.println("Gana la serie: " + semisEste[i].getNombre());	
			System.out.println();
			j--;
		}
		
		//Semifinales de conferencia
		oeste = new Equipo[2];
		este = new Equipo[2];
		j = 3;
		System.out.println();
		System.out.println("*SEMIFINALES DE CONFERENCIA*");
		for (int i = 0; i < oeste.length; i++) {
			oeste[i] = ganadorSerie(semisOeste[i], semisOeste[j]);
			System.out.println("Gana la serie: " + oeste[i].getNombre());
			System.out.println();
			este[i] = ganadorSerie(semisEste[i], semisEste[j]);
			System.out.println("Gana la serie: " + semisEste[i].getNombre());	
			System.out.println();
			j--;
		}
		
		//Final de conferencia
		Equipo[] equiposFinal = new Equipo[2];
		System.out.println();
		System.out.println("*FINALES DE CONFERENCIA*");
		equiposFinal[0] = ganadorSerie(oeste[0], oeste[1]);
		System.out.println("Gana la serie: " + equiposFinal[0].getNombre());
		System.out.println();
		equiposFinal[1] = ganadorSerie(este[0], este[1]);
		System.out.println("Gana la serie: " + equiposFinal[1].getNombre());
		System.out.println();
		 
		//Final
		System.out.println("*FINAL DE LA NBA*");
		campeon = ganadorSerie(equiposFinal[0], equiposFinal[1]);
		System.out.println("Gana el anillo: " + campeon.getNombre());
		
	}
	
	@Deprecated
	/**
	 * @return Equipo ganador de la serie
	 * */
	public static Equipo ganadorSerie(Equipo e1, Equipo e2) {
		int victorias1 = 0;
		int victorias2 = 0;
		
		while(victorias1 < 4 && victorias2 < 4) {
			Partido p = new Partido(e1, e2);
			p.jugar(true);
			if(p.puntosLocal > p.puntosVisitante) {
				victorias1 ++;
			} else {
				victorias2 ++;
			}
		}
		
		if(victorias1 > victorias2) {
			return e1;
		} else {
			return e2;
		}
		
	}
	
	private static void cargarJugadores() {
		if(recienCreada) { // cargar los jugadores por defecto desde el JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/jugadores.json");
			JSONArray jugadoresJSON = all.getJSONArray("players");
			jugadores = ParseadorJSON.aArrayListJugador(jugadoresJSON);
		} else {
			// cargar los jugadores y sus atributos desde la BD
		}
	}
	
	private static void cargarEquipos() {
		if(recienCreada) { //cargar los equipos por defecto desde JSON
			JSONObject all = ParseadorJSON.getObjetoPrimario("data/equipos.json");
			JSONArray equiposJSON = all.getJSONArray("teams");
			equipos = ParseadorJSON.aArrayEquipos(equiposJSON);
		} else {
			// cargar los equipos y sus atributos desde la BD
		}
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
	public static void draft() {
		noticiasDraft = new ArrayList<String>();
		noticiasDraft.add("");
		noticiasDraft.add("DRAFT: ");
		pasaAnyo();
		Equipo[] ordenDraft = new Equipo[30];
		
		Clasificacion general = clasificaciones.get("GENERAL");
		
		for (int i = 0; i < ordenDraft.length; i++) {
			ordenDraft[i] = general.get(i);
			System.out.println("Puesto numero: " + (30-i) + ", " + ordenDraft[i].nombre);
		}	
		
		draft = new ArrayList<>();
		
		//ajustar agencia libre
		ajustarAgenciaLibre(); //en este metodo se crean los jugadores del draft
		jugadores.addAll(draft);
		
		asignarSalariosYContratosDraft();
		draft.sort(new OrdenadorJugadores());
		elegirDraft(ordenDraft);
		mandarAgenciaLibre();
		PanelNoticiario.rellenarNoticiario(noticiasDraft);
		fase++;
	}
	
	/**
	 * Para evitar la descompensacion entre las diferentes
	 * posiciones a lo largo de las temporadas, hay que ajustar la agencia libre 
	 * al final de cada temporada.
	 * 
	 * Ajustar la agencia libre significa hacer que de cada posicion haya 250 jugadores:
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
	 * Eleccion de cada equipo del draft
	 * */
	private static void elegirDraft(Equipo[] orden) {
		int j = 1;
		do {
			noticiasDraft.add("Ronda: " + j);
			for (int i = 29; i >= 0; i--) {
				Jugador jug = elegirMejorDisponible();
				jug.setTid(orden[i].getTid());
				noticiasDraft.add("El equipo: " + orden[i].getNombre() + ", elige a: " + jug.getNombre() + ", o: " + jug.getOverall());
				orden[i].jugadores.add(jug);
			}
			j++;
		} while(j <= 2);
	}
	
	/**
	 * @return Jugador , el mejor disponible en el draft
	 * */
	private static Jugador elegirMejorDisponible() {
		Jugador mejor = new Jugador();
		mejor.overall = -100;
		for (Jugador j : draft) {
			if(mejor.overall < j.overall) {
				mejor = j;
			}			
		}
		draft.remove(mejor);
		return mejor;
	}

	private static void mandarAgenciaLibre() {
		for (Jugador j : draft) {
			if(j.getTid() == -1) {
				agentesLibres.add(j);
			}
		}
		draft.clear();
	}
	
	/**
	 * Selecciona que jugadores se jubilan este anyo
	 * tid = -2 -> jugadores jubilados
	 * */
	public static void jubilar() {
		ArrayList<String> noticiasJubilados = new ArrayList<String>();
		noticiasJubilados.add("JUGADORES RETIRADOS: ");
		double rand;
		for (Equipo e : equipos) {
			for (Jugador j : e.jugadores) {
				rand = Math.random();
				if(j.getEdad() >= 34) {	
					if(j.getEdad() > 40) {
						noticiasJubilados.add(j.getNombre() + ", edad: " + j.getEdad() + ", valoracion: " + j.getValoracion());
						j.setTid(-2);
						j.salario = 0;
					}else if(j.getValoracion() >= 2500 && rand > 0.9 ) {
						noticiasJubilados.add(j.getNombre() + ", edad: " + j.getEdad() + ", valoracion: " + j.getValoracion());
						j.setTid(-2);
						j.salario = 0;
					} else if(j.getValoracion() >= 1000 && rand > 0.6) {
						noticiasJubilados.add(j.getNombre() + ", edad: " + j.getEdad() + ", valoracion: " + j.getValoracion());
						j.setTid(-2);
						j.salario = 0;
					} else if(rand > 0.2 ) {
						noticiasJubilados.add(j.getNombre() + ", edad: " + j.getEdad() + ", valoracion: " + j.getValoracion());
						j.setTid(-2);
						j.salario = 0;
					}
				}
			}
		}
		
		eliminarJugadores(); //borra de los arrays de cada equipo
		actualizarSalarios();
		
		noticiasJubilados.add("RETIRADOS DE LA AGENCIA LIBRE: ");
		for (Jugador j : agentesLibres) {
			rand = Math.random();
			if(j.getEdad() > 36 && rand > 0.5) {
				noticiasJubilados.add(j.getNombre() + ", edad: " + j.getEdad());
				j.setTid(-2);
			} else if(j.getEdad() > 38) {
				noticiasJubilados.add(j.getNombre() + ", edad: " + j.getEdad());
				j.setTid(-2);
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
		
		PanelNoticiario.rellenarNoticiario(noticiasJubilados);
	}
	
	private static void actualizarSalarios() {
		for (Equipo e : equipos) {
			e.salarioTotal = 0;
			e.calcSalarioTotal();
		}
	}
	
	/**
	 * Renueva los contratos de los jugadores
	 * */
	public static void renovaciones() {
		ArrayList<String> noticiasRenovaciones = new ArrayList<String>();
		noticiasRenovaciones.add("");
		double rand;
		
		noticiasRenovaciones.add("RENOVACION DE JUGADORES:");
		for(Equipo e: equipos) {
			noticiasRenovaciones.add("");
			noticiasRenovaciones.add("Renovaciones de " + e.getNombre());
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
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(j.getEdad() > 30 && rand <= 0.85) {
								//Renueva por 3 anyos 35-40
								j.anyosContratoRestantes = 3;
								j.salario = (35000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(rand <= 0.9) {
								//Renueva 5 anyos 35-40
								j.anyosContratoRestantes = 5;
								j.salario = (35000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							}
						} else if(j.getValoracion() >= 2500) {
							if(j.getEdad() > 35 && rand <= 0.7) {
								//Renueva por 1 anyo y 15-20
								j.anyosContratoRestantes = 1;
								j.salario = (15000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(j.getEdad() > 30 && rand <= 0.75) {
								//Renueva por 3 anyos y 20-25
								j.anyosContratoRestantes = 3;
								j.salario = (20000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(rand <= 0.8) {
								//Renueva por 5 anyos y 25-30
								j.anyosContratoRestantes = 5;
								j.salario = (25000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							}
						} else if(j.getValoracion() >= 1000) {
							if(j.getEdad() > 35 && rand <= 0.6) {
								//Renueva por 1 anyo y 5-10
								j.anyosContratoRestantes = 1;
								j.salario = (5000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(j.getEdad() > 30 && rand <= 0.65) {
								//Renueva por 3 anyos y 6-11
								j.anyosContratoRestantes = 3;
								j.salario = (6000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(rand <= 0.7) {
								//Renueva por 5 anyos y 11-16
								j.anyosContratoRestantes = 5;
								j.salario = (11000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							}
						} else if(j.getValoracion() > 0){
							if(j.getEdad() > 35 && rand <= 0.4) {
								//Renueva por 1 anyo y 1-5
								j.anyosContratoRestantes = 1;
								j.salario = (1000 + ((int)(Math.random()*4001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(j.getEdad() > 30 && rand <= 0.45) {
								//Renueva por 2 anyos y 2-8
								j.anyosContratoRestantes = 2;
								j.salario = (2000 + ((int)(Math.random()*6001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(rand <= 0.5) {
								//Renueva por 3 anyos y 5-10
								j.anyosContratoRestantes = 3;
								j.salario = (5000 + ((int)(Math.random()*5001)));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							}	
						} else {
							if(j.getEdad() > 35 && rand <= 0.25) {
								//Renueva por 1 anyo y 1
								j.anyosContratoRestantes = 1;
								j.salario = 1000;
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(j.getEdad() > 30 && rand <= 0.3) {
								//Renueva por 2 anyos y 1
								j.anyosContratoRestantes = 2;
								j.salario = 1000;
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							} else if(rand <= 0.45) {
								//Renueva por 3 anyos y 1-2 
								j.anyosContratoRestantes = 3;
								j.salario = 1000 + ((int)(Math.random()*1001));
								noticiasRenovaciones.add(j.getNombre() + "(valoracion: " + j.getValoracion() + ")" + ", " + j.anyosContratoRestantes + " anyos por " + j.salario + " $");
							}	
						}
					}
				}
				if(j.anyosContratoRestantes==0) {
					j.setTid(-1);
					noticiasRenovaciones.add(j.nombre + "(valoracion: " + j.getValoracion() + ")," + " 0");
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
		
		PanelNoticiario.rellenarNoticiario(noticiasRenovaciones);
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
	public static void agenciaLibre() {
		noticiasAgenciaLibre = new ArrayList<String>();
		agentesLibres.clear();
		cargarAgentesLibres();

		noticiasAgenciaLibre.add("");
		noticiasAgenciaLibre.add("AGENCIA LIBRE:");
		
		for (Equipo e : clasificaciones.get("GENERAL").equipos) {
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
		noticiasAgenciaLibre.add("");
		noticiasAgenciaLibre.add(equipo.getNombre() + ", espacio salarial: " + (Equipo.limiteSalarial - equipo.salarioTotal) + " $");
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
					noticiasAgenciaLibre.add("FICHAJE ESTRELLA: " + jugador.getNombre() + ", por: " + jugador.salario + ", durante: " + jugador.anyosContratoRestantes + ", o: " + jugador.getOverall() + ", valoracion: " + jugador.getValoracion() + ", tid: " +jugador.getTid());
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
			noticiasAgenciaLibre.add("Ajustes de plantillas: ");
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
					noticiasAgenciaLibre.add("Fichado: " + j.nombre + ", por: " + j.salario + ", durante: " + j.anyosContratoRestantes + ", o: " + j.getOverall() + ", valoracion: " + j.getValoracion() + ", tid: " +j.getTid());
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
				noticiasAgenciaLibre.add("Fichado: " + j.nombre + ", por: " + j.salario + ", durante: " + j.anyosContratoRestantes + ", o: " + j.getOverall() + ", valoracion: " + j.getValoracion() + ", tid: " +j.getTid());
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
						if (p == Posicion.PIVOT)  System.out.println(equipo.getNombre()+" ficha relleno de pivot");
						jugador.setTid(equipo.getTid());
						jugador.salario = 1000;
						jugador.anyosContratoRestantes = 1;
						noticiasAgenciaLibre.add("Fichado: " + jugador.nombre + ", por: " + jugador.salario + ", durante: " + jugador.anyosContratoRestantes + ", o: " + jugador.getOverall() + ", valoracion: " + jugador.getValoracion() + ", tid: " +jugador.getTid());
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
			noticiasAgenciaLibre.add("Fichado: " + jugador.nombre + ", por: " + jugador.salario + ", durante: " + jugador.anyosContratoRestantes + ", o: " + jugador.getOverall() + ", valoracion: " + jugador.getValoracion() + ", tid: " +jugador.getTid());
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
	public static void despedirJugadores() {
		Integer[] contPos = new Integer[5];
		actualizarRoles();
		for (Equipo e : equipos) {
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
		PanelNoticiario.rellenarNoticiario(noticiasAgenciaLibre);
	}
	
	/**
	 * Metodo para mandar los jugadores sobrantes a la 
	 * agencia libre
	 * */
	private static void cortarJugador(Equipo e, int maxJugadores) {
		if(noticiasAgenciaLibre == null) {
			noticiasAgenciaLibre = new ArrayList<>();
		}
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
					noticiasAgenciaLibre.add("DESPIDO: " + j.nombre + ", rol: " + j.rol + ", equipo: " + e.nombre);
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
	/*
	private static void mostrarAgenciaLibre() {
		for (Jugador jugador : agentesLibres) {
			if(jugador.getValoracion() > 0) {
				System.out.println(jugador.getNombre() + ", edad: " + jugador.getEdad() + ", valoracion: " + jugador.getValoracion());
			}
		}
	}
*/
	private static void pasaAnyo() {
		for(Equipo e: equipos) {
			for(Jugador j: e.jugadores) {
				j.anyosContratoRestantes--;
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
	
	/**
	 * Metodo que resetea todos los datos correspondientes a la temporada actual, 
	 * que se ejecutara cada vez que termine una temporada, para empezar una nueva.
	 * */
	public static void reset() {
		//guardar datos fin de temporada
		Equipo u = usuario.getEquipo();
		temporadasPasadas.get(anyo).guardaBalanceUsuario(u.getVictorias(), u.getDerrotas());
		temporadasPasadas.get(anyo).guardaCampeon(campeon);
		temporadasPasadas.get(anyo).guardaClasificacion(equipos);
		
		
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
	
	public static void main(String[] args) {
		Usuario u = new Usuario("prueba", 0, 9);
		inicializar(true, u);
		/*for(int i = 0; i < 10; i++) {
			reset();
			nuevaTemporada();
		}*/
		System.out.println();
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
			System.out.println("hey");
		}
		
	}

}