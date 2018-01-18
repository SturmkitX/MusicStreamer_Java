import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import com.rinke.solutions.io.HeatShrinkEncoder;

public class HeatShrink_test {
    private AudioInputStream stream;

    public void run() {
        // int windowSize = 8;
        // int lookaheadSize = 4;
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //
        // try {
        //     stream = AudioSystem.getAudioInputStream(new File("muxed.wav"));
        //     byte[] audio = new byte[stream.available()];
        //     System.out.println("Original size: " + stream.available());
        //
        //     HsOutputStream out = new HsOutputStream(baos, windowSize, lookaheadSize);
        //     stream.read(audio);
        //     out.write(audio);
        //
        //     HsInputStream hsi = new HsInputStream(new ByteArrayInputStream(baos.toByteArray()), windowSize, lookaheadSize);
        //     System.out.println("Compressed size: " + hsi.available());
        //     byte[] res = new byte[2500000];
        // 	int len = hsi.read(res);
        // 	System.out.println("Size 2: " + len);
        //
        // } catch(Exception e) {
        //     e.printStackTrace();
        // }
        FileInputStream in = new FileInputStream("muxed.wav");
        FileOutputStream out = new FileOutputStream("cacat");
        HeatShrinkEncoder en = new HeatShrinkEncoder(8, 4);
        en.encode(in, out);
        in.close();
        out.close();
    }

    public static void main(String[] args) {
        HeatShrink_test t = new HeatShrink_test();
        t.run();
    }
}
