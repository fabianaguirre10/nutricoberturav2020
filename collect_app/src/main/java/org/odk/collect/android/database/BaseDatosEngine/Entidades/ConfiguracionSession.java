package org.odk.collect.android.database.BaseDatosEngine.Entidades;

/**
 * Created by Administrador1 on 8/2/2018.
 */

public class ConfiguracionSession {
    public static String Cnf_idAccount ;
    public static String Cnf_AccountNombre;
    public static String Cnf_idcampania ;
    public static String Cnf_CampaniaNombre ;
    public static String Cnf_factorbusqueda;
    public static String cnf_fechacargaruta;
    public static String Cnf_imei;
    public static String Cnf_batteryStatus ;
    public static String Cnf_formularios;

    public static String getCnf_imei() {
        return Cnf_imei;
    }

    public static String getCnf_formularios() {
        return Cnf_formularios;
    }

    public static void setCnf_formularios(String cnf_formularios) {
        Cnf_formularios = cnf_formularios;
    }

    public static void setCnf_imei(String cnf_imei) {
        Cnf_imei = cnf_imei;
    }

    public static String getCnf_batteryStatus() {
        return Cnf_batteryStatus;
    }

    public static void setCnf_batteryStatus(String cnf_batteryStatus) {
        Cnf_batteryStatus = cnf_batteryStatus;
    }

    public static String getCnf_fechacargaruta() {
        return cnf_fechacargaruta;
    }

    public static void setCnf_fechacargaruta(String cnf_fechacargaruta) {
        ConfiguracionSession.cnf_fechacargaruta = cnf_fechacargaruta;
    }

    public static String getCnf_idAccount() {
        return Cnf_idAccount;
    }

    public static void setCnf_idAccount(String cnf_idAccount) {
        Cnf_idAccount = cnf_idAccount;
    }

    public static String getCnf_AccountNombre() {
        return Cnf_AccountNombre;
    }

    public static void setCnf_AccountNombre(String cnf_AccountNombre) {
        Cnf_AccountNombre = cnf_AccountNombre;
    }

    public static String getCnf_idcampania() {
        return Cnf_idcampania;
    }

    public static void setCnf_idcampania(String cnf_idcampania) {
        Cnf_idcampania = cnf_idcampania;
    }

    public static String getCnf_CampaniaNombre() {
        return Cnf_CampaniaNombre;
    }

    public static void setCnf_CampaniaNombre(String cnf_CampaniaNombre) {
        Cnf_CampaniaNombre = cnf_CampaniaNombre;
    }

    public static String getCnf_factorbusqueda() {
        return Cnf_factorbusqueda;
    }

    public static void setCnf_factorbusqueda(String cnf_factorbusqueda) {
        Cnf_factorbusqueda = cnf_factorbusqueda;
    }
}
