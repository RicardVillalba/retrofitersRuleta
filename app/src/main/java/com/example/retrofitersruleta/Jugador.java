package com.example.retrofitersruleta;
public class Jugador {

    private String id;
    private String nombre;
    private int monedero;
    private int turnos;

    // Constructor
    public Jugador(String id, String nombre, int monedero, int turnos) {
        this.id = id;
        this.nombre = nombre;
        this.monedero = monedero;
        this.turnos = turnos;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getMonedero() {
        return monedero;
    }

    public void setMonedero(int monedero) {
        this.monedero = monedero;
    }

    public int getTurnos() {
        return turnos;
    }

    public void setTurnos(int turnos) {
        this.turnos = turnos;
    }

    // Otros métodos si es necesario

    @Override
    public String toString() {
        return "Jugador{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", monedero=" + monedero +
                ", turnos=" + turnos +
                '}';
    }
}

