import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.net.Socket;

public class WSock {

    private Scanner sc;
    private AudioInputStream stream;
    private AudioFormat format;
    private Socket sock;

    private InputStream in;
    private OutputStream out;

    private Socket connectWireless(String address, int port) throws Exception {
        Socket x = new Socket(address, port);
        in = x.getInputStream();
        out = x.getOutputStream();

        return x;
    }

    private void playMusic(String filename) {
        try {
            stream = AudioSystem.getAudioInputStream(new File(filename));
            format = stream.getFormat();
            byte[] content = new byte[4096];
            boolean stream_ended = false;
            byte[] command = new byte[32];
            int audio_bytes_read;


            content[0] = 65;
            content[1] = 66;
            content[2] = 67;
            content[3] = 68;
            content[4] = 69;

            while(!stream_ended) {
                in.read(command, 0, 5);
                System.out.println("Received command: " + new String(command));

                if((audio_bytes_read = stream.read(content, 5, 256)) < 256) {
                    stream_ended = true;
                }

                for(int i=audio_bytes_read + 5; i<261; i++) {
                    content[i] = 0;
                }

                content[261] = 65;
                content[262] = 66;
                content[263] = 67;
                content[264] = 68;
                content[265] = 70;

                out.write(content, 0, 266);
                // System.out.println();
            }

            stream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendExitSignal() throws Exception {
        out.write(new String("QUITS").getBytes());
        in.close();
        out.close();
        sock.close();
    }

    private void sendReadySignal() throws Exception {
        out.write(new String("READY").getBytes());
    }

    public void run() {
        sc = new Scanner(System.in);
        String choice;
        boolean should_exit = false;
        while(!should_exit) {
            choice = sc.nextLine();
            String[] tokens = choice.split(" ");
            switch(tokens[0]) {
                case "connect": try {
                    sock = connectWireless(tokens[1], Integer.parseInt(tokens[2]));
                    sendReadySignal();
                } catch(Exception e) {
                    e.printStackTrace();
                }; break;
                case "play": playMusic(tokens[1]); break;
                case "exit": try {
                    sendExitSignal(); should_exit = true;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        WSock t = new WSock();
        t.run();
    }
}
