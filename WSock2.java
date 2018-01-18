import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.net.Socket;

public class WSock2 {

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
            File filepath = new File(filename);
            // stream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_UNSIGNED, AudioSystem.getAudioInputStream(filepath));
            // // stream = AudioSystem.getAudioInputStream(filepath);
            // format = stream.getFormat();
            // File file = new File("filename.mp3");
            AudioInputStream in = AudioSystem.getAudioInputStream(filepath);
            AudioInputStream stream = null;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat =
                new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
                                8000,
                                8,
                                1,
                                1,
                                8000,
                                false);
            stream = AudioSystem.getAudioInputStream(decodedFormat, in);

            byte[] content = new byte[4096];
            boolean stream_ended = false;
            byte[] command = new byte[32];
            int audio_bytes_read;

            // get the author and name of song
            // 0 = author, 1 = song name
            String songInfo = filepath.getName();
            while(songInfo.length() < 32) {
                songInfo += ".";
            }
            String finalInfo = "<new>" + songInfo;
            byte[] infoBytes = finalInfo.getBytes();

            out.write(infoBytes);

            while(!stream_ended) {
                in.read(command, 0, 7);
                System.out.print("Received command: " + new String(command));

                if((audio_bytes_read = stream.read(content, 0, 1000)) < 1000) {
                    stream_ended = true;
                }

                for(int i=audio_bytes_read; i<1000; i++) {
                    content[i] = 0;
                }

                out.write(content, 0, 1000);
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
                    // sendReadySignal();
                } catch(Exception e) {
                    e.printStackTrace();
                }; break;
                case "play": playMusic(tokens[1]); break;
                case "exit": try {
                    // sendExitSignal();
                    should_exit = true;
                } catch(Exception e) {
                    e.printStackTrace();
                }; break;
                case "test": try {
                    sock = connectWireless("192.168.4.1", 5678);
                    playMusic("muxed.wav");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        WSock2 t = new WSock2();
        t.run();
    }
}
