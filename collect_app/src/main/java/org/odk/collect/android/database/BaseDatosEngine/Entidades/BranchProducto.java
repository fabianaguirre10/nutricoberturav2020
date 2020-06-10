package org.odk.collect.android.database.BaseDatosEngine.Entidades;

public class BranchProducto {
    public  String e_code = "code";
    public  String e_productname = "e_productname";
    public  String e_cantSku = "e_cantSku";
    public  String e_valor="e_valor";
    public   String e_codproducto="e_codproducto";
    public   double e_stock=0;
    public   int _id;

    public String getE_code() {
        return e_code;
    }

    public void setE_code(String e_code) {
        this.e_code = e_code;
    }

    public String getE_productname() {
        return e_productname;
    }

    public void setE_productname(String e_productname) {
        this.e_productname = e_productname;
    }

    public String getE_cantSku() {
        return e_cantSku;
    }

    public void setE_cantSku(String e_cantSku) {
        this.e_cantSku = e_cantSku;
    }

    public String getE_valor() {
        return e_valor;
    }

    public void setE_valor(String e_valor) {
        this.e_valor = e_valor;
    }

    public String getE_codproducto() {
        return e_codproducto;
    }

    public void setE_codproducto(String e_codproducto) {
        this.e_codproducto = e_codproducto;
    }

    public double getE_stock() {
        return e_stock;
    }

    public void setE_stock(double e_stock) {
        this.e_stock = e_stock;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
