
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GUIClient extends JFrame {

    // Networking
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // GUI components
    private JTextField ipField, portField;
    private JButton connectBtn, disconnectBtn;
    private JTextArea outputArea;

    // POST
    private JTextField postX, postY, postColor;
    private JTextArea postMsg;
    private JButton postBtn;

    // PIN/UNPIN
    private JTextField pinX, pinY;
    private JButton pinBtn, unpinBtn;

    // GET
    private JButton getPinsBtn, getNotesBtn;
    private JTextField getColor, getContains, getRefersTo;

    // CLEAR/ SHAKE
    private JButton clearBtn, shakeBtn;

    public GUIClient() {
        super("Bulletin Board Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Top panel for IP/Port
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("IP:"));
        ipField = new JTextField("localhost", 10);
        topPanel.add(ipField);

        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("6000", 5);
        topPanel.add(portField);

        connectBtn = new JButton("Connect");
        disconnectBtn = new JButton("Disconnect");
        disconnectBtn.setEnabled(false);
        topPanel.add(connectBtn);
        topPanel.add(disconnectBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center: output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);

        // Right panel for commands
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // POST panel
        JPanel postPanel = new JPanel(new GridLayout(5, 2));
        postPanel.setBorder(BorderFactory.createTitledBorder("POST Note"));
        postPanel.add(new JLabel("X:"));
        postX = new JTextField();
        postPanel.add(postX);
        postPanel.add(new JLabel("Y:"));
        postY = new JTextField();
        postPanel.add(postY);
        postPanel.add(new JLabel("Color:"));
        postColor = new JTextField();
        postPanel.add(postColor);
        postPanel.add(new JLabel("Message:"));
        postMsg = new JTextArea(3, 10);
        postPanel.add(new JScrollPane(postMsg));
        postBtn = new JButton("POST");
        postPanel.add(postBtn);
        rightPanel.add(postPanel);

        // PIN panel
        JPanel pinPanel = new JPanel(new GridLayout(3, 2));
        pinPanel.setBorder(BorderFactory.createTitledBorder("PIN / UNPIN"));
        pinPanel.add(new JLabel("X:"));
        pinX = new JTextField();
        pinPanel.add(pinX);
        pinPanel.add(new JLabel("Y:"));
        pinY = new JTextField();
        pinPanel.add(pinY);
        pinBtn = new JButton("PIN");
        unpinBtn = new JButton("UNPIN");
        pinPanel.add(pinBtn);
        pinPanel.add(unpinBtn);
        rightPanel.add(pinPanel);

        // GET panel
        JPanel getPanel = new JPanel(new GridLayout(4, 2));
        getPanel.setBorder(BorderFactory.createTitledBorder("GET Notes / Pins"));
        getPanel.add(new JLabel("Color:"));
        getColor = new JTextField();
        getPanel.add(getColor);
        getPanel.add(new JLabel("Contains (x y):"));
        getContains = new JTextField();
        getPanel.add(getContains);
        getPanel.add(new JLabel("RefersTo:"));
        getRefersTo = new JTextField();
        getPanel.add(getRefersTo);
        getPinsBtn = new JButton("GET PINS");
        getNotesBtn = new JButton("GET NOTES");
        getPanel.add(getPinsBtn);
        getPanel.add(getNotesBtn);
        rightPanel.add(getPanel);

        // CLEAR / SHAKE
        JPanel clearPanel = new JPanel(new GridLayout(1, 2));
        clearPanel.setBorder(BorderFactory.createTitledBorder("Board Actions"));
        clearBtn = new JButton("CLEAR");
        shakeBtn = new JButton("SHAKE");
        clearPanel.add(clearBtn);
        clearPanel.add(shakeBtn);
        rightPanel.add(clearPanel);

        add(rightPanel, BorderLayout.EAST);

        // Button actions
        connectBtn.addActionListener(e -> connect());
        disconnectBtn.addActionListener(e -> disconnect());
        postBtn.addActionListener(e -> sendPost());
        pinBtn.addActionListener(e -> sendPin());
        unpinBtn.addActionListener(e -> sendUnpin());
        getPinsBtn.addActionListener(e -> sendGetPins());
        getNotesBtn.addActionListener(e -> sendGetNotes());
        clearBtn.addActionListener(e -> sendClear());
        shakeBtn.addActionListener(e -> sendShake());
    }

    // Networking methods
    private void connect() {
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            socket = new Socket(ip, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read handshake (3 lines)
            outputArea.append(in.readLine() + "\n");
            outputArea.append(in.readLine() + "\n");
            outputArea.append(in.readLine() + "\n");

            connectBtn.setEnabled(false);
            disconnectBtn.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage());
        }
    }

    private void disconnect() {
        try {
            sendCommand("DISCONNECT");
            socket.close();
            connectBtn.setEnabled(true);
            disconnectBtn.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error disconnecting: " + ex.getMessage());
        }
    }

    private void sendCommand(String cmd) {
        try {
            out.println(cmd);
            out.flush();

            String first = in.readLine();
            if (first != null) {
                outputArea.append(first + "\n");
                String[] parts = first.split(" ");
                if (parts.length == 2) {
                    try {
                        int n = Integer.parseInt(parts[1]);
                        for (int i = 0; i < n; i++) {
                            outputArea.append(in.readLine() + "\n");
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            outputArea.append("Server error: " + e.getMessage() + "\n");
        }
    }

    // POST
    private void sendPost() {
        String x = postX.getText().trim();
        String y = postY.getText().trim();
        String color = postColor.getText().trim();
        String msg = postMsg.getText().trim();
        sendCommand("POST " + x + " " + y + " " + color + " " + msg);
    }

    // PIN / UNPIN
    private void sendPin() {
        sendCommand("PIN " + pinX.getText().trim() + " " + pinY.getText().trim());
    }
    private void sendUnpin() {
        sendCommand("UNPIN " + pinX.getText().trim() + " " + pinY.getText().trim());
    }

    // GET
    private void sendGetPins() {
        sendCommand("GET PINS");
    }

    private void sendGetNotes() {
        String cmd = "GET";
        if (!getColor.getText().trim().isEmpty()) cmd += " color=" + getColor.getText().trim();
        if (!getContains.getText().trim().isEmpty()) cmd += " contains=" + getContains.getText().trim();
        if (!getRefersTo.getText().trim().isEmpty()) cmd += " refersTo=" + getRefersTo.getText().trim();
        sendCommand(cmd);
    }

    // CLEAR / SHAKE
    private void sendClear() { sendCommand("CLEAR"); }
    private void sendShake() { sendCommand("SHAKE"); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIClient client = new GUIClient();
            client.setVisible(true);
        });
    }
}
