package org.odk.collect.android.database.BaseDatosEngine.Entidades;

public class BranchDevoluciones {
    public  String code ;
    public  String productname ;
    public  int cant_dev ;
    public   String codproducto;
    public   String entrega;
    public   String codlocal;
    public   int cant_entrega;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public int getCant_dev() {
        return cant_dev;
    }

    public void setCant_dev(int cant_dev) {
        this.cant_dev = cant_dev;
    }

    public String getCodproducto() {
        return codproducto;
    }

    public void setCodproducto(String codproducto) {
        this.codproducto = codproducto;
    }

    public String getEntrega() {
        return entrega;
    }

    public void setEntrega(String entrega) {
        this.entrega = entrega;
    }

    public String getCodlocal() {
        return codlocal;
    }

    public void setCodlocal(String codlocal) {
        this.codlocal = codlocal;
    }

    public int getCant_entrega() {
        return cant_entrega;
    }

    public void setCant_entrega(int cant_entrega) {
        this.cant_entrega = cant_entrega;
    }
}
