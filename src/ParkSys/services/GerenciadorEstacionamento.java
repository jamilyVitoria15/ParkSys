package ParkSys.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import ParkSys.entities.Registro;
import ParkSys.entities.Vaga;

public class GerenciadorEstacionamento {

    private HashMap<String, Vaga> vagas;

    private ArrayList<Registro> registros;

    private LinkedList<String> filaEspera;

    public GerenciadorEstacionamento() {

        vagas = new HashMap<>();

        registros = new ArrayList<>();

        filaEspera = new LinkedList<>();
    }

    public HashMap<String, Vaga> getVagas() {
        return vagas;
    }

    public ArrayList<Registro> getRegistros() {
        return registros;
    }

    public LinkedList<String> getFilaEspera() {
        return filaEspera;
    }
}
