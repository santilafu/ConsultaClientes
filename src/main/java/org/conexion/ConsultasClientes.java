package org.conexion;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class ConsultasClientes {

    static String uri = "xmldb:exist://localhost:8080/exist/xmlrpc/db/clientes";
    static String usuario = "admin";
    static String password = "1234";

    public static void main(String[] args) {
        try {
            // Registrar el driver
            String driver = "org.exist.xmldb.DatabaseImpl";
            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.getDeclaredConstructor().newInstance();
            DatabaseManager.registerDatabase(database);

            // Conectar a la colección
            Collection col = DatabaseManager.getCollection(uri, usuario, password);

            if (col != null) {
                System.out.println("¡Conexión exitosa!\n");

                // Consulta 1: Nombre y dirección de clientes que viven en Madrid
                System.out.println("CONSULTA 1: Clientes de Madrid");
                ejecutarConsulta(col,
                        "for $c in doc('/db/clientes/datos.xml')//cliente[ciudad='Madrid'] " +
                                "return <resultado><nombre>{$c/nombre/text()}</nombre><direccion>{$c/direccion/text()}</direccion></resultado>"
                );

                // Consulta 2: Nombre, ciudad y código postal de clientes de España
                System.out.println("\n CONSULTA 2: Clientes de España");
                ejecutarConsulta(col,
                        "for $c in doc('/db/clientes/datos.xml')//cliente[pais='España'] " +
                                "return <resultado><nombre>{$c/nombre/text()}</nombre><ciudad>{$c/ciudad/text()}</ciudad><codigoPostal>{$c/codigoPostal/text()}</codigoPostal></resultado>"
                );

                // Consulta 3: Teléfono y país de clientes con código postal que empieza por "5"
                System.out.println("\nCONSULTA 3: Clientes con CP que empieza por 5");
                ejecutarConsulta(col,
                        "for $c in doc('/db/clientes/datos.xml')//cliente[starts-with(codigoPostal, '5')] " +
                                "return <resultado><telefono>{$c/telefono/text()}</telefono><pais>{$c/pais/text()}</pais></resultado>"
                );

                col.close();
            } else {
                System.out.println("No se pudo conectar");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para ejecutar consultas XQuery
    public static void ejecutarConsulta(Collection col, String xquery) {
        try {
            XQueryService servicio = (XQueryService) col.getService("XQueryService", "1.0");
            ResourceSet resultado = servicio.query(xquery);
            ResourceIterator iter = resultado.getIterator();

            if (!iter.hasMoreResources()) {
                System.out.println("No se encontraron resultados");
            }

            while (iter.hasMoreResources()) {
                Resource res = iter.nextResource();
                System.out.println(res.getContent().toString());
            }
        } catch (Exception e) {
            System.out.println("Error en consulta: " + e.getMessage());
        }
    }
}