package com.example.retrofitersruleta;

public class PartidaRecord {
    private String nombre;
    private int turnos;
    private int monedero;

    public PartidaRecord(String nombre, int turnos, int monedero) {
        this.nombre = nombre;
        this.turnos = turnos;
        this.monedero = monedero;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTurnos() {
        return turnos;
    }

    public int getMonedero() {
        return monedero;
    }
}
