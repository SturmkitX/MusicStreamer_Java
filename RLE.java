import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class RLE {
    private AudioInputStream stream;


    private int compressIt() throws Exception {
        byte[] comp = new byte[10000000];
        int offset = 0;
        int byteread, lastread, len;

        lastread = stream.read();
        len = 1;
        while((byteread = stream.read()) >= 0) {
            if(byteread == lastread) {
                len++;
            } else {
                comp[offset++] = (byte)len;
                comp[offset++] = (byte)lastread;
                len = 1;
            }
            lastread = byteread;
            if(len == 255) {
                comp[offset++] = (byte)len;
                comp[offset++] = (byte)lastread;
                len = 1;
            }
        }

        return offset;
    }

    public void run() {
        try {
            stream = AudioSystem.getAudioInputStream(new File("muxed.wav"));
            System.out.println("Uncompressed size: " + stream.available() / 1024 + " KB");
            System.out.println("Compressed size: " + compressIt() / 1024 + " KB");
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        RLE t = new RLE();
        t.run();
    }
}
