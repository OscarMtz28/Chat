package mx.uam.chat;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Servidor {
    private static final int PUERTO = 6789;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            System.out.println("Servidor UDP de números primos iniciado en el puerto " + PUERTO);
            System.out.println("Esperando conexiones...");

            while (true) {
                try {
                    // Buffer para recibir datos
                    byte[] bufferRecepcion = new byte[BUFFER_SIZE];
                    DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
                    
                    // Recibir solicitud del cliente
                    socket.receive(paqueteRecepcion);
                    
                    // Obtener dirección y puerto del cliente
                    InetAddress direccionCliente = paqueteRecepcion.getAddress();
                    int puertoCliente = paqueteRecepcion.getPort();
                    
                    // Procesar el número recibido
                    String numeroStr = new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength());
                    String respuesta;
                    
                    try {
                        long numero = Long.parseLong(numeroStr);
                        boolean esPrimo = esPrimo(numero);
                        respuesta = "El número " + numero + (esPrimo ? " ES primo" : " NO ES primo");
                    } catch (NumberFormatException e) {
                        respuesta = "Error: '" + numeroStr + "' no es un número válido";
                    }
                    
                    // Enviar respuesta al cliente
                    byte[] bufferEnvio = respuesta.getBytes();
                    DatagramPacket paqueteEnvio = new DatagramPacket(
                        bufferEnvio, 
                        bufferEnvio.length, 
                        direccionCliente, 
                        puertoCliente
                    );
                    socket.send(paqueteEnvio);
                    
                    System.out.println("Procesada solicitud de " + direccionCliente + ": " + respuesta);
                    
                } catch (IOException e) {
                    System.err.println("Error al procesar la solicitud: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    /**
     * Método para verificar si un número es primo
     * @param numero El número a verificar
     * @return true si es primo, false si no lo es
     */
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
