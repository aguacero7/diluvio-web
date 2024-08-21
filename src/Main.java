import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

public class Main {

    final static int port = 9632;
    final static Path root = Paths.get("webdocs/").normalize().toAbsolutePath();

    public static void main(String[] args) {
        try {
            ServerSocket socketServeur = new ServerSocket(port);
            System.out.println("Lancement du serveur");

            while (true) {
                Socket socketClient = socketServeur.accept();

                System.out.println("Connexion avec : " + socketClient.getInetAddress());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socketClient.getInputStream()));
                PrintStream out = new PrintStream(socketClient.getOutputStream());

                String requestLine = in.readLine();
                System.out.println("Requête : " + requestLine);

                String[] requestParts = requestLine.split(" ");
                if (requestParts.length > 1) {
                    String path = requestParts[1];
                    System.out.println("Chemin demandé : " + path);
                    if(Objects.equals(path, "/")) {
                        try {
                            Path filePath = root.resolve("index.html");

                            HTTPAnswer answer = new HTTPAnswer(filePath);
                            answer.printContent(out);
                        } catch (IOException e) {
                            HTTPAnswer answer = new HTTPAnswer();
                            answer.printContent(out);

                        }
                    }
                    else {
                        System.out.println(path.compareTo("/"));


                        String extractedPath = path.substring(1); // enlever le premier '/'
                        System.out.println("Extraction : " + extractedPath);
                        try {
                            Path nioPath = root.resolve(Paths.get(extractedPath));
                            System.out.println("Chemin NIO : " + nioPath);
                            HTTPAnswer ans = new HTTPAnswer(nioPath);
                            ans.printContent(out);

                        } catch (FileSystemException e) {
                            HTTPAnswer ans = new HTTPAnswer(new String[]{"Content-Type : text/plain","Server: diluvio/0.0.1"}, "Fichier inexistant", 404);
                            ans.printContent(out);

                        }
                    }
                } else {
                    HTTPAnswer ans = new HTTPAnswer(new String[]{"Content-Type : text/plain","Server: diluvio/0.0.1"},"Methode invalide",400);
                    ans.printContent(out);

                }
                socketClient.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
