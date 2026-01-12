package com.distributed.client;

/**
 * Client modülü - Ebubekir Yılmaz
 * Dağıtık mesajlaşma sistemi istemci uygulaması
 */

import com.distributed.common.ProtocolConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final String leaderHost;
    private final int leaderPort;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client(String leaderHost, int leaderPort) {
        this.leaderHost = leaderHost;
        this.leaderPort = leaderPort;
    }

    public void connect() throws IOException {
        socket = new Socket(leaderHost, leaderPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("[CLIENT] Lidere bağlanıldı: " + leaderHost + ":" + leaderPort);
    }

    public void disconnect() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[CLIENT] Bağlantı kapatma hatası: " + e.getMessage());
        }
    }

    public String set(String messageId, String message) throws IOException {
        writer.println(ProtocolConstants.CMD_SET + " " + messageId + " " + message);
        return reader.readLine();
    }

    public String get(String messageId) throws IOException {
        writer.println(ProtocolConstants.CMD_GET + " " + messageId);
        return reader.readLine();
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nKomutlar: SET <id> <mesaj> | GET <id> | quit");

        while (true) {
            System.out.print(">> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("quit")) break;

            try {
                writer.println(input);
                System.out.println("<< " + reader.readLine());
            } catch (IOException e) {
                System.err.println("[CLIENT] Hata: " + e.getMessage());
                break;
            }
        }
    }

    public void bulkTestMode(int count) {
        System.out.println("[CLIENT] Toplu test: " + count + " mesaj");
        int success = 0, error = 0;
        long start = System.currentTimeMillis();

        for (int i = 1; i <= count; i++) {
            try {
                String response = set("msg_" + i, "mesaj başarılı #" + i);
                if (ProtocolConstants.RESPONSE_OK.equals(response)) success++;
                else error++;
                if (i % 100 == 0) System.out.println("[CLIENT] " + i + "/" + count);
            } catch (IOException e) {
                error++;
                break;
            }
        }

        double duration = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("\nBaşarılı: " + success + ", Hata: " + error + ", Süre: " + duration + "s");
    }

    public void getTestMode(int start, int end) {
        System.out.println("[CLIENT] GET testi: " + start + "-" + end);
        int found = 0, notFound = 0;

        for (int i = start; i <= end; i++) {
            try {
                String response = get("msg_" + i);
                if (!ProtocolConstants.RESPONSE_ERROR.equals(response)) found++;
                else notFound++;
            } catch (IOException e) {
                break;
            }
        }
        System.out.println("Bulunan: " + found + ", Bulunamayan: " + notFound);
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = ProtocolConstants.DEFAULT_LEADER_CLIENT_PORT;
        String mode = "interactive";
        int count = 1000, getStart = 1, getEnd = 100;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--host": host = args[++i]; break;
                case "--port": port = Integer.parseInt(args[++i]); break;
                case "--mode": mode = args[++i]; break;
                case "--count": count = Integer.parseInt(args[++i]); break;
                case "--get-start": getStart = Integer.parseInt(args[++i]); break;
                case "--get-end": getEnd = Integer.parseInt(args[++i]); break;
            }
        }

        Client client = new Client(host, port);
        try {
            client.connect();
            switch (mode) {
                case "interactive": client.interactiveMode(); break;
                case "bulk": client.bulkTestMode(count); break;
                case "get": client.getTestMode(getStart, getEnd); break;
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] Bağlantı hatası: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
}
