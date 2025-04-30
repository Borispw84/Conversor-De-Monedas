package com.conversordemonedas;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class ConversorApp {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    private static Map<String, Double> rates;

    public static void main(String[] args) {
        // Obtener las tasas de cambio
        obtenerDatosCambio();

        // Crear el escáner para leer entradas de usuario
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Paso 1: Introducir un importe
            System.out.print("Introduce el importe que deseas convertir: ");
            double cantidad = scanner.nextDouble();
            scanner.nextLine();  // Consumir el salto de línea

            // Paso 2: Seleccionar la moneda de origen
            System.out.print("Introduce la moneda de origen (ejemplo: USD, EUR, GBP): ");
            String monedaOrigen = scanner.nextLine().toUpperCase();

            // Paso 3: Seleccionar la moneda de destino
            System.out.print("Introduce la moneda de destino (ejemplo: USD, EUR, GBP): ");
            String monedaDestino = scanner.nextLine().toUpperCase();

            // Realizar la conversión
            double resultado = convertirMoneda(cantidad, monedaOrigen, monedaDestino);

            // Mostrar el resultado
            if (resultado != -1) {
                System.out.println("El importe de " + cantidad + " " + monedaOrigen + " equivale a " + resultado + " " + monedaDestino);
            } else {
                System.out.println("No se pudo realizar la conversión. Verifique las monedas.");
            }

            // Preguntar si desea continuar
            System.out.print("¿Deseas realizar otra conversión? (sí/no): ");
            String respuesta = scanner.nextLine().toLowerCase();
            if (!respuesta.equals("sí")) {
                System.out.println("Gracias por usar el conversor de monedas.");
                break;
            }
        }

        scanner.close();
    }

    // Método para obtener las tasas de cambio de la API
    public static void obtenerDatosCambio() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conexion.getInputStream());
            Gson gson = new Gson();
            TipoDeCambio response = gson.fromJson(reader, TipoDeCambio.class);

            rates = response.getRates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para convertir la moneda de origen a la moneda de destino
    public static double convertirMoneda(double cantidad, String monedaOrigen, String monedaDestino) {
        try {
            // Si la moneda origen es USD, ya está en la base, simplemente multiplicamos por la tasa de destino
            double tasaOrigen = rates.get(monedaOrigen);
            double tasaDestino = rates.get(monedaDestino);

            // Convertir la cantidad a USD y luego a la moneda de destino
            double cantidadEnUSD = cantidad / tasaOrigen;
            double resultado = cantidadEnUSD * tasaDestino;

            return resultado;
        } catch (Exception e) {
            System.out.println("Error: una de las monedas ingresadas no es válida.");
            return -1;
        }
    }

    // Clase interna para manejar la respuesta de la API
    private static class TipoDeCambio {
        private String base;
        private Map<String, Double> rates;

        public Map<String, Double> getRates() {
            return rates;
        }
    }
}
