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

    private Socket connectWireless(String address) throws Exception {
        return new Socket(address, 5678);
    }

    private void playMusic(String filename) {
        try {
            stream = AudioSystem.getAudioInputStream(new File(filename));
            format = stream.getFormat();
            byte[] content = new byte[4096];
            boolean stream_ended = false;
            byte[] command = new byte[32];
            int audio_bytes_read;

            in = sock.getInputStream();
            out = sock.getOutputStream();

            while(!stream_ended) {
                in.read(command, 0, 10);

                //System.out.println("Available: " + stream.available());
                if((audio_bytes_read = stream.read(content, 0, 4096)) < 4096) {
                    stream_ended = true;
                }

                // in case there are less than 64 bytes of data left in the song, pad the rest of the array with 0's
                for(int i=audio_bytes_read; i<4096; i++) {
                    content[i] = 0;
                }
                //System.out.println("Sending chunk of data");
                //ardOut.write(content, 0, 64);
                //System.out.println(bytes_sent);

                out.write(content, 0, 4096);
                System.out.println();
            }

            stream.close();
            in.close();
            out.close();
            sock.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
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
                    sock = connectWireless(tokens[1]);
                } catch(Exception e) {
                    e.printStackTrace();
                }; break;
                case "play": playMusic(tokens[1]); break;
                case "exit": should_exit = true;
            }
        }
    }

    public static void main(String[] args) {
        WSock t = new WSock();
        t.run();
    }
}
