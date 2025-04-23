package mx.uam.chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PUERTO = 6789;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor TCP iniciado. Esperando conexiones en el puerto " + PUERTO);

            while (true) {
                try (Socket socketCliente = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                     PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true)) {

                    System.out.println("Cliente conectado: " + socketCliente.getInetAddress());
                    String numeroStr = in.readLine();

                    if (numeroStr == null || numeroStr.equalsIgnoreCase("salir")) {
                        System.out.println("Cliente cerró la conexión");
                        continue;
                    }

                    try {
                        long numero = Long.parseLong(numeroStr);
                        boolean esPrimo = esPrimo(numero);
                        String respuesta = "El número " + numero + (esPrimo ? " ES primo" : " NO ES primo");
                        out.println(respuesta);
                        System.out.println("Respuesta enviada: " + respuesta);
                    } catch (NumberFormatException e) {
                        out.println("Error: Ingrese un número válido");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static boolean esPrimo(long numero) {
        if (numero <= 1) return false;
        if (numero <= 3) return true;
        if (numero % 2 == 0 || numero % 3 == 0) return false;
        
        for (long i = 5; i * i <= numero; i += 6) {
            if (numero % i == 0 || numero % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}