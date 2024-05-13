/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package webserver;

/**
 *
 * @author ProGaming
 */


//Library thingsssssssss
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import java.util.prefs.Preferences;

public class WebServerUI extends Application {
    //variables dan instances dari WebServer
    private WebServer webServer;
    private TextField filePathField;
    private TextField logsPathField;
    private TextField portField;
    private final Preferences preferences = Preferences.userNodeForPackage(WebServerUI.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        
        primaryStage.setTitle("Web Server Control");

        // Membuat field untuk input file path dan ada tombol browsenya, defaultnya adalah D:\\Web\Files
        Label pathLabel = new Label("FilePath:");
        filePathField = new TextField(preferences.get("filePath", "D:\\Web\\Files"));
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> browseFilePath(primaryStage));
        HBox filePathBox = new HBox(5, pathLabel, filePathField, browseButton);
        filePathBox.setAlignment(Pos.CENTER_LEFT);
       
        //membuat field untuk logs path dan ada tombol browsenya juga, defaultnya adalah D:\\Web\Logs
        Label logsPathLabel = new Label("Logs Path:");
        logsPathField = new TextField(preferences.get("logsPath", "D:\\Web\\Logs"));
        Button logsBrowseButton = new Button("Browse");
        logsBrowseButton.setOnAction(e -> browseLogsPath(primaryStage));
        HBox logsPathBox = new HBox(5, logsPathLabel, logsPathField, logsBrowseButton);
        logsPathBox.setAlignment(Pos.CENTER_LEFT);
        
        // untuk membuat field yang dapat diisi dengan port yang diinginkan. port defaultnya adalah 808080
        Label portLabel = new Label("Port:");
        portField = new TextField(preferences.get("port", "8080"));
        VBox portBox = new VBox(5, portLabel, portField);

        // untuk membuat tombol start
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> startWebServer());

        //untuk membuat tobol stop
        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> stopWebServer());

        //menempatkan tombol start dan stop di GUInya
        VBox buttonBox = new VBox(10, startButton, stopButton);

        // tampilan GUInya
        VBox layout = new VBox(10, filePathBox, logsPathBox, portBox, buttonBox);
        //membuat guinya dengan ukuran 300x200
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //metode biar pas browse di file path di klik, nanti bisa browse direktori yang mau dipakai lewat file explorer (dialog)
    private void browseFilePath(Stage primaryStage) {
        //membuat objek directorychooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        //menampilkan judul dialog
        directoryChooser.setTitle("Select File Path");
        // Menampilkan dialog dan mendapatkan direktori yang dipilihh
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            //menempatkan direktori yang dipilih di browse/dialog ke dalam field path
            filePathField.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    //metode sama kayak browsefilepath
    private void browseLogsPath(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Logs Path");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            logsPathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    //metode untuk start webservernya
    private void startWebServer() {
        //untuk mendapatkan filepath, logspath, dan port yang sudah diinput di gui tadi
        String filePath = filePathField.getText();
        String logsPath = logsPathField.getText();
        int port = Integer.parseInt(portField.getText());
        
        //menyimpan preferensi dari path file, logs, dan port
        preferences.put("filePath", filePath);
        preferences.put("logsPath", logsPath);
        preferences.put("port", String.valueOf(port));

        //memulai server saat tidak sedang berjalan
        if (webServer == null || !webServer.isAlive()) {
            webServer = new WebServer(filePath, logsPath, port);
            webServer.start();
        } else {
            System.out.println("server sudah berjalan.");
        }
    }
        
    //metode untuk stop webservernya
    private void stopWebServer() {
        //buat ngecek apakah webservernya masih berjalan
        if (webServer != null && webServer.isAlive()) {
            //servernya di stop dengan metode stopServer pada class WebServer
            webServer.stopServer();
            //biar keluar dari aplikasi
            System.exit(0);
        }
    }
    
}