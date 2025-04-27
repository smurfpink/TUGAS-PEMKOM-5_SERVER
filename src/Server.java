    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    import java.io.*;
    import java.net.*;
    /**
     *
     * @author makar
     */
    public class Server {
        public static void main(String[] args) {
            int port = 5000; // Port server
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server berjalan di port " + port + "...");
            System.out.println("Menunggu koneksi client...");

            while (true) { // Loop untuk menerima multiple clients
                try (Socket socket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                    
                    System.out.println("\nClient terhubung: " + socket.getInetAddress());
                    
                    // Terima metadata file
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();
                    
                    // Buat nama file unik
                    String outputFileName = "received_" + System.currentTimeMillis() + "_" + fileName;
                    
                    System.out.println("Menerima file: " + fileName);
                    System.out.println("Ukuran file: " + fileSize + " bytes");
                    System.out.println("Akan disimpan sebagai: " + outputFileName);

                    try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        long totalReceived = 0;
                        
                        while (totalReceived < fileSize) {
                            read = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalReceived));
                            if (read == -1) break;
                            fos.write(buffer, 0, read);
                            totalReceived += read;
                            
                            // Tampilkan progress
                            System.out.printf("Progress: %.2f%%\r", (totalReceived * 100.0 / fileSize));
                        }
                        
                        System.out.println("\nFile berhasil diterima dan disimpan sebagai: " + outputFileName);
                    }
                } catch (IOException e) {
                    System.err.println("Error saat menangani client: " + e.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("Server error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    }
