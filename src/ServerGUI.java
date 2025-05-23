/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author makar
 */
public class ServerGUI extends javax.swing.JFrame {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream dis;
    private FileOutputStream fos;
    private Thread serverThread;
    /**
     * Creates new form ServerGUI
     */
    public ServerGUI() {
        initComponents();
         setTitle("Server - File Receiver");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        startButton = new JButton("Start Server");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Server - File Receiver ");

        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        startButton.setText("Start Server");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(startButton)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
         startServer();
    }//GEN-LAST:event_startButtonActionPerformed
    private void startServer() {
    if (serverThread != null && serverThread.isAlive()) {
        appendMessage("Server sudah berjalan!");
        return;
    }

    serverThread = new Thread(() -> {
        try {
            serverSocket = new ServerSocket(5000); // Gunakan variabel class
            appendMessage("Server berjalan di port 5000...");
            
            while (!Thread.interrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(clientSocket.getInputStream())) {
                    
                    appendMessage("Client terhubung: " + clientSocket.getInetAddress());
                    
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();
                    
                    String outputFile = "received_" + System.currentTimeMillis() + "_" + fileName;
                    
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        long remaining = fileSize;
                        
                        while (remaining > 0 && !Thread.interrupted()) {
                            int read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                            if (read == -1) break;
                            fos.write(buffer, 0, read);
                            remaining -= read;
                        }
                        
                        if (!Thread.interrupted()) {
                            appendMessage("File berhasil diterima: " + outputFile);
                        }
                    }
                } catch (IOException e) {
                    if (!Thread.interrupted()) {
                        appendMessage("Error dengan client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            if (!Thread.interrupted()) {
                appendMessage("Server error: " + ex.getMessage());
            }
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                appendMessage("Error menutup server: " + e.getMessage());
            }
        }
    });
    
    serverThread.start();
    startButton.setText("Stop Server");
    // Hapus semua action listener sebelum menambahkan yang baru
    for (ActionListener al : startButton.getActionListeners()) {
        startButton.removeActionListener(al);
    }
    startButton.addActionListener(e -> stopServer());
}

private void stopServer() {
    if (serverThread != null) {
        serverThread.interrupt();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            appendMessage("Error menghentikan server: " + e.getMessage());
        }
        appendMessage("Server dihentikan");
        startButton.setText("Start Server");
        // Hapus semua action listener sebelum menambahkan yang baru
        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }
        startButton.addActionListener(e -> startServer());
    }
}

    private void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> textArea.append(msg + "\n"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton startButton;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
