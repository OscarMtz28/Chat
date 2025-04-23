package mx.uam.chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    private static final int PUERTO_SERVIDOR = 6789;
    private static final int TIMEOUT = 5000; // 5 segundos de timeout
    private static final int MAX_INTENTOS = 3;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java ClientePrimosTCP <direccion-servidor>");
            return;
        }

        try (Socket socket = new Socket(args[0], 6789);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor. Ingrese números (o 'salir' para terminar):");

            while (true) {
                System.out.print("> ");
                String entrada = scanner.nextLine();

                out.println(entrada);
                if (entrada.equalsIgnoreCase("salir")) break;

                String respuesta = in.readLine();
                System.out.println("Servidor dice: " + respuesta);
            }
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
}
