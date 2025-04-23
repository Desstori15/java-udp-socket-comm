
import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    private int port;
    private int number;
    private List<Integer> receivedNumbers = new ArrayList<>();

    public Main(int port, int number) {
        this.port = port;
        this.number = number;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Main <port> <number>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            int number = Integer.parseInt(args[1]);

            Main app = new Main(port, number);
            app.start();

        } catch (NumberFormatException e) {
            System.err.println("Port and number must be integers.");
        }
    }

    public void start() {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println("Master mode activated on port " + port);
            masterMode(socket);
        } catch (SocketException e) {
            System.out.println("Slave mode activated.");
            slaveMode();
        }
    }

    private void masterMode(DatagramSocket socket) {
        try {
            byte[] buffer = new byte[1024];

            if (number != 0 && number != -1) {
                receivedNumbers.add(number);
                System.out.println("Stored number: " + number);
            }

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                int receivedNumber = Integer.parseInt(message);

                if (receivedNumber == 0) {
                    handleAverage(socket, packet.getAddress(), packet.getPort());
                } else if (receivedNumber == -1) {
                    handleTermination(socket);
                    break;
                } else {
                    System.out.println("Received number: " + receivedNumber);
                    receivedNumbers.add(receivedNumber);
                }
            }
        } catch (IOException e) {
            System.err.println("Error in master mode: " + e.getMessage());
        }
    }

    private void handleAverage(DatagramSocket socket, InetAddress clientAddress, int clientPort) throws IOException {
        int sum = receivedNumbers.stream().mapToInt(Integer::intValue).sum();
        int average = (int) Math.floor((double) sum / receivedNumbers.size()); // Округление вниз
        System.out.println("Average calculated: " + average);

        String response = String.valueOf(average);

   
        byte[] data = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
        socket.send(responsePacket);
        System.out.println("Average sent to client: " + clientAddress + ":" + clientPort);
    }

    private void handleTermination(DatagramSocket socket) throws IOException {
        System.out.println("Termination signal received. Broadcasting -1 and shutting down.");
        broadcastMessage(socket, "-1");
        socket.close();
    }

    private void broadcastMessage(DatagramSocket socket, String message) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), port);
        socket.send(packet);
    }

    private void slaveMode() {
        try (DatagramSocket socket = new DatagramSocket()) {

            String message = String.valueOf(number);
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), port);
            socket.send(packet);

            System.out.println("Sent number: " + number);


            if (number == 0) {
                System.out.println("Waiting for master's response...");
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);


                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    System.out.println("Average received from master: " + response);
                } catch (IOException e) {
                    System.err.println("Error while waiting for master's response: " + e.getMessage());
                }
            }

            System.out.println("Slave terminating after sending number.");
        } catch (IOException e) {
            System.err.println("Error in slave mode: " + e.getMessage());
        }
    }
}
