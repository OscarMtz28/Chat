package mx.uam.chat;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    private static final int PUERTO_SERVIDOR = 6789;
    private static final int TIMEOUT = 5000; // 5 segundos de timeout
    private static final int MAX_INTENTOS = 3;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java ClientePrimosUDP <direccion_servidor>");
            return;
        }

        String direccionServidor = args[0];
        Scanner scanner = new Scanner(System.in);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT);
            InetAddress direccionIP = InetAddress.getByName(direccionServidor);

            System.out.println("Cliente UDP iniciado. Conectando al servidor en " + direccionServidor);
            System.out.println("Ingrese números para verificar si son primos (o 'salir' para terminar):");

            while (true) {
                System.out.print("> ");
                String entrada = scanner.nextLine().trim();

                if (entrada.equalsIgnoreCase("salir")) {
                    break;
                }

                try {
                    long numero = Long.parseLong(entrada);
                    enviarYRecibir(socket, direccionIP, entrada);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido o 'salir' para terminar.");
                }
            }
        } catch (SocketException e) {
            System.err.println("Error al crear el socket: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Cliente terminado.");
        }
    }

    private static void enviarYRecibir(DatagramSocket socket, InetAddress direccion, String mensaje) throws IOException {
        byte[] bufferEnvio = mensaje.getBytes();
        DatagramPacket paqueteEnvio = new DatagramPacket(bufferEnvio, bufferEnvio.length, direccion, PUERTO_SERVIDOR);

        byte[] bufferRecepcion = new byte[1024];
        DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);

        int intentos = 0;
        boolean recibido = false;

        while (intentos < MAX_INTENTOS && !recibido) {
            try {
                // Enviar el número al servidor
                socket.send(paqueteEnvio);
                System.out.println("Enviado: " + mensaje);

                // Esperar respuesta
                socket.receive(paqueteRecepcion);
                recibido = true;

                String respuesta = new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength());
                System.out.println("Respuesta del servidor: " + respuesta);

            } catch (SocketTimeoutException e) {
                intentos++;
                System.out.println("Timeout, intento " + intentos + " de " + MAX_INTENTOS);
                if (intentos == MAX_INTENTOS) {
                    System.out.println("Servidor no responde después de " + MAX_INTENTOS + " intentos.");
                }
            }
        }
    }
}
