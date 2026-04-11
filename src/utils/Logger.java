package utils;

import java.io.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;


public class Logger {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
    private static BufferedWriter fileWriter = null;
    private static boolean fileLogging = false;

    private static final AtomicLong totalRequests = new AtomicLong(0);
    private static final AtomicLong totalBytesSent = new AtomicLong(0);

    public static synchronized void init(boolean enableFileLog) {
        fileLogging = enableFileLog;
        if (fileLogging) {
            try {
                fileWriter = new BufferedWriter(new FileWriter("server.log", true));
                info("File logging enabled (server.log)");
            } catch (IOException e) {
                fileLogging = false;
                System.err.println("Unable to open log file: " + e.getMessage());
            }
        }
    }

    public static synchronized void close() {
        if (fileWriter != null) {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void logRequest(String clientIp, String path, String method, Instant timestamp, String userAgent) {
        totalRequests.incrementAndGet();
        String ts = TIMESTAMP.format(timestamp);
        String ua = (userAgent != null) ? userAgent : "-";
        String msg = String.format("%s - %s \"%s %s\" UA:\"%s\"", ts, clientIp, method, path, ua);
        info(msg);
    }

    public static synchronized void info(String message) {
        String line = format("INFO", message);
        System.out.println(line);
        appendToFile(line);
    }

    public static synchronized void warning(String message) {
        String line = format("WARN", message);
        System.out.println(line);
        appendToFile(line);
    }

    public static synchronized void error(String message) {
        String line = format("ERROR", message);
        System.err.println(line);
        appendToFile(line);
    }

    private static String format(String level, String message) {
        String ts = TIMESTAMP.format(Instant.now());
        return String.format("%s %s: %s", ts, level, message);
    }

    private static void appendToFile(String line) {
        if (!fileLogging || fileWriter == null) return;
        try {
            fileWriter.write(line);
            fileWriter.newLine();
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to server.log: " + e.getMessage());
            fileLogging = false;
        }
    }


    public static void addBytesSent(long bytes) {
        totalBytesSent.addAndGet(bytes);
    }

    public static void printStats() {
        long reqs = totalRequests.get();
        long bytes = totalBytesSent.get();
        double kb = bytes / 1024.0;
        
        String stats = String.format(
            "--- Server Statistics ---\nTotal Requests: %d\nTotal Data Sent: %.2f KB\n-------------------------",
            reqs, kb
        );
        info(stats);
    }

    public static long getTotalRequests() {
        return totalRequests.get();
    }

    public static long getTotalBytesSent() {
        return totalBytesSent.get();
    }
}
