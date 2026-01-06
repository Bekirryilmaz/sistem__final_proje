package com.distributed.leader;

import com.distributed.common.ConfigManager;
import com.distributed.common.MemberInfo;
import com.distributed.common.ProtocolConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeaderServer {

    private final int clientPort;
    private final int grpcPort;
    private final String localIP;
    private final ConfigManager configManager;
    private final MemberManager memberManager;
    private final LeaderGrpcServer grpcServer;
    private final ExecutorService clientExecutor;
    private final ScheduledExecutorService logScheduler;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public LeaderServer(int clientPort, int grpcPort, String configPath) {
        this.clientPort = clientPort;
        this.grpcPort = grpcPort;
        this.localIP = ProtocolConstants.getLocalIP();
        this.configManager = new ConfigManager(configPath);
        this.memberManager = new MemberManager(configManager);
        this.grpcServer = new LeaderGrpcServer(memberManager, grpcPort);
        this.clientExecutor = Executors.newCachedThreadPool();
        this.logScheduler = Executors.newSingleThreadScheduledExecutor();
        this.running = false;
    }

    public void start() throws IOException {
        running = true;
        grpcServer.start();
        startPeriodicLogging();
        serverSocket = new ServerSocket(clientPort);
        printFamilyHeader();

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientExecutor.submit(() -> handleClient(clientSocket));
            } catch (SocketException e) {
                if (running) System.err.println("[LEADER] Socket hatası: " + e.getMessage());
            }
        }
    }

    private void printFamilyHeader() {
        System.out.println(ProtocolConstants.DOUBLE_LINE);
        System.out.println("Family at " + localIP + ":" + clientPort + " (me)");
        System.out.println("Time: " + ProtocolConstants.getTimestamp());
        System.out.println(ProtocolConstants.DOUBLE_LINE);
        System.out.println("Role: LEADER");
        System.out.println("Telnet Port: " + clientPort);
        System.out.println("gRPC Port: " + grpcPort);
        System.out.println("Tolerance: " + configManager.getTolerance());
        System.out.println(ProtocolConstants.SEPARATOR_LINE);
        System.out.println("Waiting for family members and clients...");
        System.out.println(ProtocolConstants.DOUBLE_LINE);
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(processCommand(line.trim()));
            }
        } catch (IOException e) {
            System.out.println("[LEADER] İstemci bağlantısı kapandı");
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private String processCommand(String command) {
        if (command == null || command.isEmpty()) return ProtocolConstants.RESPONSE_ERROR;

        String[] parts = command.split("\\s+", 3);
        if (parts.length < 2) return ProtocolConstants.RESPONSE_ERROR;

        String cmd = parts[0].toUpperCase();
        String messageId = parts[1];

        if (ProtocolConstants.CMD_SET.equals(cmd) && parts.length >= 3) {
            return handleSet(messageId, parts[2]);
        } else if (ProtocolConstants.CMD_GET.equals(cmd)) {
            return handleGet(messageId);
        }
        return ProtocolConstants.RESPONSE_ERROR;
    }

    private String handleSet(String messageId, String messageContent) {
        int tolerance = configManager.getTolerance();
        if (memberManager.getAliveCount() < tolerance) {
            System.out.println("[LEADER] Yetersiz üye. Gerekli: " + tolerance);
            return ProtocolConstants.RESPONSE_ERROR;
        }

        List<String> selectedMembers = memberManager.selectMembersForMessage(tolerance);
        if (selectedMembers.size() < tolerance) return ProtocolConstants.RESPONSE_ERROR;

        boolean success = memberManager.storeMessageToMembersAsync(messageId, messageContent, selectedMembers);
        if (success) {
            System.out.println("[LEADER] SET: " + messageId + " -> " + selectedMembers);
            return ProtocolConstants.RESPONSE_OK;
        }
        return ProtocolConstants.RESPONSE_ERROR;
    }

    private String handleGet(String messageId) {
        String content = memberManager.retrieveMessage(messageId);
        if (content != null) return content;
        return ProtocolConstants.RESPONSE_ERROR;
    }

    private void startPeriodicLogging() {
        logScheduler.scheduleAtFixedRate(this::printStats, 
            ProtocolConstants.LOG_PERIOD_MS, ProtocolConstants.LOG_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private void printStats() {
        System.out.println();
        System.out.println(ProtocolConstants.DOUBLE_LINE);
        System.out.println("Family at " + localIP + ":" + clientPort + " (me)");
        System.out.println("Time: " + ProtocolConstants.getTimestamp());
        System.out.println(ProtocolConstants.SEPARATOR_LINE);
        System.out.println("Members:");
        for (MemberInfo member : memberManager.getAllMembers()) {
            String status = member.isAlive() ? "[ALIVE]" : "[DEAD]";
            System.out.println("  - " + member.getMemberId() + " at " + member.getHost() + ":" + member.getPort() + " " + status + " (" + member.getMessageCount() + " msg)");
        }
        System.out.println(ProtocolConstants.SEPARATOR_LINE);
        System.out.println("Total Messages: " + memberManager.getTotalMessageCount());
        System.out.println("Alive Members: " + memberManager.getAliveCount() + "/" + memberManager.getTotalMemberCount());
        System.out.println("Tolerance: " + configManager.getTolerance());
        System.out.println(ProtocolConstants.DOUBLE_LINE);
    }

    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        grpcServer.stop();
        memberManager.shutdown();
        clientExecutor.shutdown();
        logScheduler.shutdown();
    }

    public static void main(String[] args) {
        int clientPort = ProtocolConstants.DEFAULT_LEADER_CLIENT_PORT;
        int grpcPort = ProtocolConstants.DEFAULT_LEADER_GRPC_PORT;
        String configPath = "tolerance.conf";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--client-port": clientPort = Integer.parseInt(args[++i]); break;
                case "--grpc-port": grpcPort = Integer.parseInt(args[++i]); break;
                case "--config": configPath = args[++i]; break;
            }
        }

        LeaderServer server = new LeaderServer(clientPort, grpcPort, configPath);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("[LEADER] Başlatma hatası: " + e.getMessage());
        }
    }
}
