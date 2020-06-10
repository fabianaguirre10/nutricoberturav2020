package org.odk.collect.android.database.BaseDatosEngine.Entidades;

import androidx.annotation.NonNull;

public class Producto {
    int _id;
    String codproducto;
    String name;
    double pvp;
    String categoria;
    double stock;
    String descripcion;
    String codigosecundario;
    String estado;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCodproducto() {
        return codproducto;
    }

    public void setCodproducto(String codproducto) {
        this.codproducto = codproducto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPvp() {
        return pvp;
    }

    public void setPvp(double pvp) {
        this.pvp = pvp;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigosecundario() {
        return codigosecundario;
    }

    public void setCodigosecundario(String codigosecundario) {
        this.codigosecundario = codigosecundario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @NonNull
    @Override
    public String toString() {
        return  name.toUpperCase();
    }
}
