import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HTTPAnswer {
    private Map<String, String> headers= new Hashtable<>(){};
    private List<String> content=new ArrayList<String>();
    private int code;
    private final Map<String, String> contentTypes = new HashMap<String, String>() {
        {
            put("html", "text/html");
            put("htm", "text/html");
            put("css", "text/css");
            put("js", "application/javascript");
            put("json", "application/json");
            put("xml", "application/xml");
            put("jpg", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("png", "image/png");
            put("gif", "image/gif");
            put("bmp", "image/bmp");
            put("svg", "image/svg+xml");
            put("pdf", "application/pdf");
            put("txt", "text/plain");
            put("csv", "text/csv");
            put("mp3", "audio/mpeg");
            put("mp4", "video/mp4");
            put("avi", "video/x-msvideo");
            put("zip", "application/zip");
            put("rar", "application/x-rar-compressed");
            put("7z", "application/x-7z-compressed");
            put("doc", "application/msword");
            put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            put("xls", "application/vnd.ms-excel");
            put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            put("ppt", "application/vnd.ms-powerpoint");
            put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");

            // Default content type
            put("default", "application/octet-stream");
        }
    };
    private final Dictionary<Integer, String> codeMessages = new Hashtable<>();
    {
        codeMessages.put(100, "Continue");
        codeMessages.put(101, "Switching Protocols");
        codeMessages.put(200, "OK");
        codeMessages.put(201, "Created");
        codeMessages.put(202, "Accepted");
        codeMessages.put(203, "Non-Authoritative Information");
        codeMessages.put(204, "No Content");
        codeMessages.put(301, "Moved Permanently");
        codeMessages.put(302, "Found");
        codeMessages.put(400, "Bad Request");
        codeMessages.put(401, "Unauthorized");
        codeMessages.put(403, "Forbidden");
        codeMessages.put(404, "Not Found");
        codeMessages.put(500, "Internal Server Error");
        codeMessages.put(501, "Not Implemented");
        codeMessages.put(502, "Bad Gateway");
        codeMessages.put(503, "Service Unavailable");
        codeMessages.put(7, "SIUUUUUUUUU");
    }

    public Map<String,String> getHeaders(){
        return headers;
    }
    public List<String> getContent(){
        return content;
    }
    public int getCode(){
        return code;
    }

    // Constructeurs
    public HTTPAnswer(String[] headers, String message, int code) {
        this.headers = extractHeaders(headers);
        this.content.add(message);
        this.code = code;
    }

    public HTTPAnswer(String[] headers, int code) {
        this.headers = extractHeaders(headers);
        this.code = code;
        this.content.add("");
    }

    public HTTPAnswer(Path file, int code) throws IOException {
        this.content = Files.readAllLines(file, StandardCharsets.UTF_8);
        this.code = code;
        String extension = file.getFileName().toString().split("\\.")[1];
        String contentType=contentTypes.getOrDefault(extension.toLowerCase(), contentTypes.get("default"));
        this.headers = extractHeaders(new String[]{"Content-Type: "+contentType,"Server: diluvio/0.0.1"});
    }

    public HTTPAnswer(Path file) throws IOException {
        this(file, 200);
    }

    public HTTPAnswer(int code) {
        this(new String[0], code);
    }

    public HTTPAnswer() {
        this(new String[0], 200);
    }
    private Map<String, String> extractHeaders(String headers[]){
        Map<String, String> out = new java.util.HashMap<>();
        for(String header : headers){
            String[] splittedHeader = header.split(":");
            out.put(splittedHeader[0],splittedHeader[1]);
        }
        return out;
    }
    public void printContent(PrintStream output){
        output.println("HTTP/1.1 "+this.code + " "+ codeMessages.get(code) );
        for(String line : this.headers.keySet()){
            output.println(line+":"+this.headers.get(line));
        }
        output.println("\r");
        for(String line : this.content){
            output.println(line);
        }
    }
}


