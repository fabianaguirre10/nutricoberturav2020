package org.odk.collect.android.logic;

import androidx.annotation.NonNull;

public class Cardex {

    private String idOperacion;
    private String codProducto;
    private String catProducto;
    private  String producto;
    private String tipoOperacion;
    private int  saldo;
    private int  cantVenta;
    private int  cantDevo;
    private int  cantDevoVenta;
    private double  usd;
    private String codLocal;
    private String stock;

    public Cardex() {
        super();
    }

    public Cardex(String idOperacion, String codProducto, String catProducto, String producto
            , String tipoOperacion, int  saldo, int  cantVenta, int cantDevo, int cantDevoVenta
            , String codLocal, String stock,int usd) {
        super();
        this.idOperacion = idOperacion;
        this.codProducto = codProducto;
        this.catProducto = catProducto;
        this.producto = producto;
        this.tipoOperacion = tipoOperacion;
        this.saldo = saldo;
        this.cantVenta = cantVenta;
        this.cantDevo = cantDevo;
        this.cantDevoVenta = cantDevoVenta;
        this.codLocal = codLocal;
        this.stock = stock;
        this.usd = usd;
    }

    public String getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(String idOperacion) {
        this.idOperacion = idOperacion;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getCatProducto() {
        return catProducto;
    }

    public void setCatProducto(String catProducto) {
        this.catProducto = catProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getCantVenta() {
        return cantVenta;
    }

    public void setCantVenta(int cantVenta) {
        this.cantVenta = cantVenta;
    }

    public int getCantDevo() {
        return cantDevo;
    }

    public void setCantDevo(int cantDevo) {
        this.cantDevo = cantDevo;
    }

    public int getCantDevoVenta() {
        return cantDevoVenta;
    }

    public void setCantDevoVenta(int cantDevoVenta) {
        this.cantDevoVenta = cantDevoVenta;
    }

    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public double getUsd() {
        return usd;
    }

    public void setUsd(double usd) {
        this.usd = usd;
    }

    @NonNull
    @Override
    public String toString() {
        return producto;
    }
}
