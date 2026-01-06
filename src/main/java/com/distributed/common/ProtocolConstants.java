package com.distributed.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ProtocolConstants {

    private ProtocolConstants() {}

    public static final String CMD_SET = "SET";
    public static final String CMD_GET = "GET";

    public static final String RESPONSE_OK = "OK";
    public static final String RESPONSE_ERROR = "ERROR";

    public static final int DEFAULT_LEADER_CLIENT_PORT = 6666;
    public static final int DEFAULT_LEADER_GRPC_PORT = 9001;
    public static final int DEFAULT_MEMBER_BASE_PORT = 9100;

    public static final long LOG_PERIOD_MS = 5000;
    public static final String MESSAGE_FILE_FORMAT = "message_%s.txt";
    public static final int GRPC_TIMEOUT_MS = 5000;

    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static final String SEPARATOR_LINE = "------------------------------------------------------------";
    public static final String DOUBLE_LINE = "============================================================";

    public static String getTimestamp() {
        return ZonedDateTime.now().format(ISO_FORMATTER);
    }

    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
