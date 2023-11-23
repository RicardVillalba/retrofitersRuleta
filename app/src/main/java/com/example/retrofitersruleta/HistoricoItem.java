package com.example.retrofitersruleta;



public class HistoricoItem {
    private int id;
    private String nombre;
    private int monedero;
    private int turnos;

    public HistoricoItem(int id, String nombre, int monedero, int turnos) {
        this.id = id;
        this.nombre = nombre;
        this.monedero = monedero;
        this.turnos = turnos;
    }

    // Agrega getters según sea necesario
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getMonedero() {
        return monedero;
    }

    public int getTurnos() {
        return turnos;
    }
}
