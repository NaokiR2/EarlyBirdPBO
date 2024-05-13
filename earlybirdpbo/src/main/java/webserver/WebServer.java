/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package webserver;

/**
 *
 * @author ProGaming
 */

//Library thingssssss
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ProGaming
 */
public class WebServer extends Thread {
    //Variables
//    private static final int DEFAULT_PORT = 8080;
//    private static final String WEB_ROOT = "D:\\Web\\Files";
//    private static final String LOG_DIR = "D:\\Web\\Logs";
    private volatile boolean shouldStop = false;
    private ServerSocket serverSocket;
    private static String webRoot;
    private int port;
    private String logsPath;


    //Konstruktor class WebServer dengan nilai default Web_root,log_dir, dan default port
//    public WebServer() {
//        this(WEB_ROOT, LOG_DIR, DEFAULT_PORT);
//    }
//    
    
    //Konstruktor class WebServer dengan nilai default
    public WebServer(String webRoot, String logsPath, int port) {
        this.webRoot = webRoot;
        this.logsPath = logsPath;
        this.port = port;
    }
    
    //untuk mengatur path webroot agar sesuai dengan path yang diatur pada GUI
//    public static void setFilePath(String filePath) {
//        this.webRoot = filePath;
//    }

    
//    public static void setFilePath(String filePath) {
//        WebServer.webRoot = filePath;
//    }
    
    public static String getWebRoot() {
        return webRoot;
    }
    
    

    //untuk mengatur port agar sesuai dengan port yang diatur di GUI
//    public void setPort(int port) {
//        this.port = port;
//    }
//    
//    //untuk mengatur logsnya biar sesuai sama yang diatur di GUI
//    public void setLogsPath(String logsPath) {
//        this.logsPath = logsPath;
//    }
       
    //untuk menghentikan servernya.
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server stopped.");
            } else {
                System.out.println("Server is already stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    //untuk menjalankan logika utama atau alur dari webserver
    public void run() {
        try {
            //membuat socket(penghubung) menggunakan port yang ditentukan di GUI nya
            serverSocket = new ServerSocket(port);
            System.out.println("Web server started on port " + port + "...");

            //loop selama nilai shouldstop true
            while (!shouldStopserver()) {
                Socket clientSocket = serverSocket.accept();
                //untuk menangani request dari klien
                HttpRequestHandler requestHandler = new HttpRequestHandler(clientSocket, logsPath, this);
                requestHandler.start();
            }
        } catch (IOException e) {
            if (!shouldStop) {
                //cetak stacktrace kalo misal server masih jalan pas ada kesalahan input/output
                e.printStackTrace();
            }
        } finally {
            try {
                //mastiin kalau serversocket bener bener off
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                //kalau misal pas ditutup ada error, di print tuh stacktrace atau errornya.
                e.printStackTrace();
            }
        }
    }

    //untuk ngeset nilai boolean yang dipakai saat ngerun 
    public boolean shouldStopserver() {
        return shouldStop;
    }

    
//    public void setShouldStop(boolean shouldStop) {
//        this.shouldStop = shouldStop;
//    }
    
    
//    private static class HttpRequestHandler extends Thread {
//        private Socket socket; //socket buat koneksi ke klien
//        private String logsPath; //path ke direktori logs yang disetel sesuai dengan GUI nya
//
//        //konstruktor untuk httpsrequesthandlernya
//        public HttpRequestHandler(Socket socket, String logsPath) {
//            this.socket = socket;
//            this.logsPath = logsPath;
//        }
//
//        
//        @Override
//        //ini menangani logika atau alur dari httprequesthandler
//        public void run() {
//            try {
//                //membaca input seperti GET /index.html HTTP/1.1
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                //menulis outputnya
//                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//
//
//                String requestLine = in.readLine(); //membaca baris permintaan dari klien
//                String[] tokens = requestLine.split(" ");//dipecah permintaannya jadi beberapa token
//                String method = tokens[0];//ndapetin metode httpnya (biasanya get)
//                String requestURL = tokens[1];//ndapetin urlnya
//
//                // ngecek metodenya, kalau get ya diproses nantinya
//                if (method.equals("GET")) {
//                    serveFile(requestURL, out);
//                } else {
//                    //metode selain get bakal di respon not implemented
//                    String response = "HTTP/1.1 501 Not Implemented\r\n\r\n";
//                    out.writeBytes(response);
//                }
//                //nyatet di lognya.
//                logAccess(requestURL, socket.getInetAddress().getHostAddress(), requestURL);
//
//                //close / nutup input output socketnya.
//                out.close();
//                in.close();
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//              
//        //metode buat ngehandle file yang direquest klien
//        private void serveFile(String requestURL, DataOutputStream out) throws IOException {
//            try {
//                //ndapetin path filenya
//                String filePath = webRoot + requestURL.replace("/", "\\");
//                //mbuat objeknya
//                File file = new File(filePath);
//
//                
//                if (file.exists()) {
//                    if (file.isDirectory()) {
//                        if (requestURL.endsWith("/")) {
//                            // melayani daftar direktori kalau filenya akhir akhirannya "/"
//                            listDirectory(file, out, getParentDirectory(requestURL));
//                        } else {
//                            // mengarahkan ke URL dengan akhiran "/" (kalo gaada / nanti dikasih /)
//                            String redirectURL = requestURL + "/";
//                            String response = "HTTP/1.1 301 Moved Permanently\r\nLocation: " + redirectURL + "\r\n\r\n";
//                            out.writeBytes(response);
//                        }
//                    } else {
//                        // Menentukan jenis konten berdasarkan ekstensi file
//                        String contentType = getContentType(file);
//
//                        // Membaca konten file dan mengirimkannya sebagai respons
//                        byte[] fileData = Files.readAllBytes(file.toPath());
//                        String response = "HTTP/1.1 200 OK\r\nContent-Length: " + fileData.length +
//                                "\r\nContent-Type: " + contentType + "\r\n\r\n";
//                        out.writeBytes(response);
//                        out.write(fileData);
//                    }
//                } else {
//                    // Kalau File not Found
//                    String response = "HTTP/1.1 404 Not Found\r\n\r\n";
//                    out.writeBytes(response);
//                }
//            } catch (IOException e) {
//                //kalau error di catch errornya apa
//                String errorMessage = e.getMessage();
//                String response = "HTTP/1.1 500 Internal Server Error\r\n\r\n";
//                out.writeBytes(response);
//                
//                //kalaupun tidak error, nanti akan tetap ditulis logs nya.
////                logAccess(requestURL, socket.getInetAddress().getHostAddress(), errorMessage);
//            }
//        }
//
//        //ngambil type file nya
//        private String getContentType(File file) {
//            String fileName = file.getName().toLowerCase();
//            if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
//                return "text/html";
//            } else if (fileName.endsWith(".pdf")) {
//                return "application/pdf";
//            } else if (fileName.endsWith(".docx")) {
//                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
//            } else if (fileName.endsWith(".txt")) {
//                return "text/plain";
//            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
//                return "image/jpeg";
//            } else if (fileName.endsWith(".png")) {
//                return "image/png";
//            } else if (fileName.endsWith(".gif")) {
//                return "image/gif";
//            } else if (fileName.endsWith(".css")) {
//                 return "text/css";
//            } else {
//                // Default content type for unknown file types
//                return "application/octet-stream";
//            }
//        }
//
//        
//        private void listDirectory(File directory, DataOutputStream out, String parentDirectory) throws IOException {
//            // Mendapatkan daftar file dalam direktori yang diberikan
//            File[] files = directory.listFiles();
//             // Membangun respons HTML untuk menampilkan daftar file
//            StringBuilder responseBuilder = new StringBuilder("<html><body><h1>Directory Listing</h1>");
//
//            // Menambahkan tombol "Back" jika tidak berada di direktori root
//            if (parentDirectory != null) {
//                responseBuilder.append("<button onclick=\"goBack()\">Back</button><br>");
//            }
//
//            // mbuat daftar file yang ada dalam suatu direktori dalam bentuk list dan hyperlink
//            responseBuilder.append("<ul>");
//            for (File file : files) {
//                String fileName = file.getName();
//                //untuk memberi hyperlink kepada setiap file yang ada di dalam list
//                responseBuilder.append("<li><a href=\"").append(fileName).append("\">").append(fileName).append("</a></li>");
//            }
//
//            //supaya tombol backnya bisa digunakan (javascript)
//            responseBuilder.append("</ul>");
//            responseBuilder.append("<script>");
//            responseBuilder.append("function goBack() { window.history.back(); }"); // Script JavaScript untuk kembali
//            responseBuilder.append("</script>");
//            responseBuilder.append("</body></html>");
//
//            //untuk merespon permintaan klien saat merequest suatu file untuk dibuka
//            String response = "HTTP/1.1 200 OK\r\nContent-Length: " + responseBuilder.length() +
//                    "\r\nContent-Type: text/html\r\n\r\n" + responseBuilder.toString();
//            //untuk menampilkan request file yang mau dibuka tadi (menampilkan isi konten file dalam bentuk htlm)
//            out.writeBytes(response);
//        }
//
//
//        private String getParentDirectory(String requestURL) {
//            // Mencari indeks posisi terakhir tanda '/' dalam URL permintaan
//            int lastSlashIndex = requestURL.lastIndexOf("/");
//            // Memeriksa apakah URL bukan merupakan root directory
//            if (lastSlashIndex > 0) {
//                 // Mengembalikan substring URL dari awal hingga sebelum tanda '/'
//                return requestURL.substring(0, lastSlashIndex);
//            }
//            // Jika URL adalah root directory, mengembalikan null
//            return null;
//        }
//        
//        //digunakan untuk membuat logs
//        private void logAccess(String requestURL, String ipAddress, String errorMessage) {
//            //Tanggal Saat ini  dalam format yyyy-mm-dd
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            // Mendapatkan tanggal saat ini dalam format yang ditentukan
//            String logFileName = dateFormat.format(new Date()) + ".log";
//            // Menggabungkan path dari direktori logs dengan nama file log
//            String logFilePath = Paths.get(logsPath, logFileName).toString();
//            try {
//                //membuat objek logsdir
//                File logsDir = new File(logsPath);
//                if (!logsDir.exists()) {
//                    //membuat direktori logs kalau semisal belum ada direktori
//                    logsDir.mkdirs();
//                }
//                //membuat objek file
//                File logFile = new File(logFilePath);
//                if (!logFile.exists()) {
//                    //Membuat FileLog kalau pada direktori tersebut belum ada file log
//                    logFile.createNewFile();
//                }
//                 // Format pesan log dengan tanggal, alamat IP, dan URL permintaan
//                String logEntry = String.format("[%s] %s - %s\n", new Date(), ipAddress, requestURL);
//                //Tulis log entry ke dalam file log, di append biar log sebelumnya tidak terhapus
//                Files.write(Paths.get(logFilePath), logEntry.getBytes(), java.nio.file.StandardOpenOption.APPEND);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }   
}