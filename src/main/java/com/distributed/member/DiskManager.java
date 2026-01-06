package com.distributed.member;

import com.distributed.common.ProtocolConstants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DiskManager {

    public enum WriteStrategy { ZERO_COPY, BUFFERED }

    private final String storageDirectory;
    private final Set<String> storedMessageIds;
    private final WriteStrategy writeStrategy;

    public DiskManager(String memberId) {
        this(memberId, WriteStrategy.ZERO_COPY);
    }

    public DiskManager(String memberId, WriteStrategy strategy) {
        this.storageDirectory = "data" + File.separator + memberId;
        this.storedMessageIds = ConcurrentHashMap.newKeySet();
        this.writeStrategy = strategy;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Files.createDirectories(Paths.get(storageDirectory));
            loadExistingMessages();
        } catch (IOException e) {
            throw new RuntimeException("Dizin oluşturulamadı: " + storageDirectory);
        }
    }

    private void loadExistingMessages() {
        File storageDir = new File(storageDirectory);
        File[] files = storageDir.listFiles((dir, name) -> name.startsWith("message_") && name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                storedMessageIds.add(fileName.substring(8, fileName.length() - 4));
            }
        }
    }

    public boolean storeMessage(String messageId, String content) {
        return writeStrategy == WriteStrategy.ZERO_COPY 
            ? storeWithFileChannel(messageId, content)
            : storeWithBufferedStream(messageId, content);
    }

    private boolean storeWithFileChannel(String messageId, String content) {
        String fileName = String.format(ProtocolConstants.MESSAGE_FILE_FORMAT, messageId);
        Path filePath = Paths.get(storageDirectory, fileName);

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw");
             FileChannel channel = raf.getChannel()) {
            
            byte[] data = content.getBytes();
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.put(data);
            buffer.flip();
            
            channel.truncate(0);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.force(true);
            
            storedMessageIds.add(messageId);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean storeWithBufferedStream(String messageId, String content) {
        String fileName = String.format(ProtocolConstants.MESSAGE_FILE_FORMAT, messageId);
        Path filePath = Paths.get(storageDirectory, fileName);

        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(filePath.toFile()), 8192)) {
            bos.write(content.getBytes());
            bos.flush();
            storedMessageIds.add(messageId);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String retrieveMessage(String messageId) {
        String fileName = String.format(ProtocolConstants.MESSAGE_FILE_FORMAT, messageId);
        Path filePath = Paths.get(storageDirectory, fileName);

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
             FileChannel channel = raf.getChannel()) {
            
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            buffer.flip();
            return new String(buffer.array(), 0, buffer.limit());
        } catch (IOException e) {
            return null;
        }
    }

    public int getMessageCount() {
        return storedMessageIds.size();
    }

    public String getStorageDirectory() {
        return storageDirectory;
    }

    public WriteStrategy getWriteStrategy() {
        return writeStrategy;
    }
}
