package org.spatialduck;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.util.List;

public class SharingWebSocket extends WebSocketServer {

    private final List<WebSocket> connections = new ArrayList<>();

    public SharingWebSocket(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("WebSocket Open: Conn -> " + conn + " handshake -> " + handshake);
        connections.add(conn);
        conn.send("Hello welcome!: ");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("WebSocket close: Conn -> " + conn + " remote -> " + remote + " Reason -> " + reason);
        connections.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server started!");
        startImageStreaming();
    }

    public void sendImageToClients(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", baos);

        ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());

        for (WebSocket conn : connections) {
            conn.send(buffer);
        }
    }
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
    private void startImageStreaming() {
        new Thread(() -> {
            try {
                Robot robot = new Robot();
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

                while (true) {
                    BufferedImage screenImage = this.resizeImage(robot.createScreenCapture(screenRect), 1280, 720);

                    sendImageToClients(screenImage);


                    Thread.sleep(1);
                }
            } catch (AWTException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        String host = "IP_PC";
        int port = 8484;

        SharingWebSocket server = new SharingWebSocket(new InetSocketAddress(host, port));
        server.start();
    }
}
