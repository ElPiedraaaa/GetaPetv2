package com.example.getapet.pojo;

import java.io.File;
import java.util.ArrayList;

public class Animal
{
    private int id;
     private String nombre;
     private int edad;
     private int especie;
     private int estado;
     private int pais;
     private int ciudad;
     private String descripcion;
    private ArrayList<String> nombreImagenes;
     private ArrayList<File> imagenes;

    public Animal(String nombre, int edad, int especie, int estado, int pais, int ciudad, String descripcion) {
        this.nombre = nombre;
        this.edad = edad;
        this.especie = especie;
        this.estado = estado;
        this.pais = pais;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
    }

    public Animal(String nombre, int edad, int especie, int estado, int pais, int ciudad, String descripcion, ArrayList<File> imagenes) {
        this.nombre = nombre;
        this.edad = edad;
        this.especie = especie;
        this.estado = estado;
        this.pais = pais;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.imagenes = imagenes;
    }

    public Animal(int id, String nombre, int edad, int especie, int estado, int pais, int ciudad, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.especie = especie;
        this.estado = estado;
        this.pais = pais;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
    }

    public Animal(int id, String nombre, int edad, int especie, int estado, int pais, int ciudad, String descripcion, ArrayList<String> nombreImagenes, ArrayList<File> imagenes) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.especie = especie;
        this.estado = estado;
        this.pais = pais;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
        this.nombreImagenes = nombreImagenes;
        this.imagenes = imagenes;
    }

    public Animal() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getEspecie() {
        return especie;
    }

    public void setEspecie(int especie) {
        this.especie = especie;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getPais() {
        return pais;
    }

    public void setPais(int pais) {
        this.pais = pais;
    }

    public int getCiudad() {
        return ciudad;
    }

    public void setCiudad(int ciudad) {
        this.ciudad = ciudad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<File> getImagenes() {
        return imagenes;
    }

    public void setImagenes(ArrayList<File> imagenes) {
        this.imagenes = imagenes;
    }

    public ArrayList<String> getNombreImagenes() {
        return nombreImagenes;
    }

    public void setNombreImagenes(ArrayList<String> nombreImagenes) {
        this.nombreImagenes = nombreImagenes;
    }
}
