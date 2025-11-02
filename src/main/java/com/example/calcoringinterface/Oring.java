package com.example.calcoringinterface;

public class Oring {
    public String codigo;
    public double diametro;
    public double espessura;

    public Oring(String codigo, double diametro, double espessura) {
        this.codigo = codigo;
        this.diametro = diametro;
        this.espessura = espessura;
    }

    @Override
    public String toString() {
        return String.format("%s | Diâmetro: %.2f mm", codigo, diametro);
    }
}
