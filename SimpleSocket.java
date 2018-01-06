import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;

public class SimpleSocket {
    private Socket client;
    private InputStream clIn;
    private OutputStream clOut;
    private Scanner scanner;
    private byte[] readBytes;

    public SimpleSocket() {
        try {
            client = new Socket("192.168.4.1", 80);
            clIn = client.getInputStream();
            clOut = client.getOutputStream();
            scanner = new Scanner(System.in);
            readBytes = new byte[1024];
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(true) {
            try {
                System.out.println(">>>>>>>>>>>>>>>>>>>>");
                String choice = scanner.nextLine();
                switch(choice) {
                    case "write" : System.out.println("Write choice"); clOut.write(new String("Ce faci bai?!").getBytes()); break;
                    case "read" : System.out.println("Read choice\n " + clIn.available()); clIn.read(readBytes, 0, clIn.available()); System.out.println(new String(readBytes)); break;
                    default : System.out.println("Default choice"); clOut.close(); client.close(); return;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        SimpleSocket ss = new SimpleSocket();
        ss.run();
    }
}
