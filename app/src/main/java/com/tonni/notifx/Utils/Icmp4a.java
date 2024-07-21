package com.tonni.notifx.Utils;


import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Icmp4a {

    public static class PingResult {
        public static class Success extends PingResult {
            public final int packetSize;
            public final long ms;

            public Success(int packetSize, long ms) {
                this.packetSize = packetSize;
                this.ms = ms;
            }
        }

        public static class Failed extends PingResult {
            public final String message;

            public Failed(String message) {
                this.message = message;
            }
        }
    }

    public static class Status {
        public final InetAddress ip;
        public final PingResult result;

        public Status(InetAddress ip, PingResult result) {
            this.ip = ip;
            this.result = result;
        }
    }

    public Status ping(String host) throws UnknownHostException, java.net.UnknownHostException {
        InetAddress ip = InetAddress.getByName(host);
        ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c 1", ip.getHostAddress());
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            Pattern pattern = Pattern.compile("time=([0-9.]+) ms");
            Matcher matcher = pattern.matcher(output.toString());
            if (matcher.find()) {
                long ms = (long) Double.parseDouble(matcher.group(1));
                return new Status(ip, new PingResult.Success(64, ms));
            } else {
                return new Status(ip, new PingResult.Failed("Ping failed"));
            }

        } catch (Exception e) {
            return new Status(ip, new PingResult.Failed(e.getMessage()));
        }
    }

    public static class UnknownHostException extends Exception {
        public UnknownHostException(String message) {
            super(message);
        }
    }
}
