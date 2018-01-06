import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import com.fazecast.jSerialComm.*;

public class Trier {

    private Scanner sc;
    private SerialPort arduino;
    private AudioInputStream stream;
    private AudioFormat format;

    private InputStream ardIn;
    private OutputStream ardOut;

    private SerialPort connectArduino(String address, String baud_rate) {
        SerialPort x =  SerialPort.getCommPort(address);
        x.setBaudRate(Integer.parseInt(baud_rate));

        return x;
    }

    private void playMusic(String filename) {
        try {
            stream = AudioSystem.getAudioInputStream(new File(filename));
            format = stream.getFormat();
            byte[] content = new byte[64];
            boolean stream_ended = false;
            byte[] command = new byte[1];
            byte[] sent_len = new byte[1];
            int audio_bytes_read;
            int bytes_sent;

            ardIn = arduino.getInputStream();
            ardOut = arduino.getOutputStream();

            while(!stream_ended) {
                ardIn.read(command, 0, 1);

                System.out.println("Available: " + stream.available());
                if((audio_bytes_read = stream.read(content, 0, 64)) < 64) {
                    stream_ended = true;
                }

                // in case there are less than 64 bytes of data left in the song, pad the rest of the array with 0's
                for(int i=audio_bytes_read; i<64; i++) {
                    content[i] = 0;
                }

                ardOut.write(content, 0, 64);
            }

            stream.close();
            ardIn.close();
            ardOut.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        sc = new Scanner(System.in);
        for(SerialPort x : SerialPort.getCommPorts()) {
            System.out.println(x.getDescriptivePortName() + " " + x.getSystemPortName() + " " + x.getNumDataBits() + " " + x.getParity() + " " + x.getNumStopBits());
        }
        String choice;
        boolean should_exit = false;
        while(!should_exit) {
            choice = sc.nextLine();
            String[] tokens = choice.split(" ");
            switch(tokens[0]) {
                case "connect": arduino = connectArduino(tokens[1], tokens[2]);
                    if(arduino.openPort()) {
                        System.out.println("Successfully connected");
                    } else {
                        System.out.println("Failed to connect");
                    }; break;
                case "play": playMusic(tokens[1]); break;
                case "exit": should_exit = true; arduino.closePort();
            }
        }
    }

    public static void main(String[] args) {
        Trier t = new Trier();
        t.run();
    }
}
