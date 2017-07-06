package win.i029.ll.languagelisten;

/**
 * Created by lvh on 7/5/17.
 */

import android.util.Log;

import com.intervigil.lame.Decoder;
import com.intervigil.lame.Constants;
import com.intervigil.wave.WaveWriter;
import com.tulskiy.camomile.audio.AudioFormat;
import com.tulskiy.camomile.audio.formats.mp3.MP3Decoder;

import net.surina.soundtouch.SoundTouch;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MP3DecoderWithTempo {

    public static boolean decoder_mpg123(File input, File output) {
        MP3Decoder decoder = new MP3Decoder();
        WaveWriter waveWriter;
        ByteBuffer reference;
        AudioFormat fmt;

        if(false == decoder.open(input)) {
            return false;
        }

        fmt = decoder.getAudioFormat();
        Log.d("camomile", "Format  " + fmt.toString());

        waveWriter = new WaveWriter(output, fmt.getSampleRate(), fmt.getChannels(), fmt.getSampleSizeInBits());

        try {
            if( false == waveWriter.createWaveFile() )
                return false;

            decoder.seek(0);


            byte[] buf = new byte[65536];

            int samplesDecoded = 0;
            while (true) {
                int len = decoder.decode(buf);
                if (len == -1) {
                    break;
                }
                samplesDecoded += len;
                waveWriter.write(buf, 0, len);
            }
            Log.d("camomile", "decoded " + samplesDecoded / fmt.getFrameSize() + " samples");
            waveWriter.closeWaveFile();
            return true;
        } catch ( Exception e ) {
            Log.e("camomile", "decoded Error ");
            e.printStackTrace();

        } finally {
            decoder.close();
        }

        return false;
    }

    public static boolean decoder(File input, File output) { // to wave file , will long time
        int errorCode = 0;

        Decoder lame = new Decoder(input, output);

        try {
            lame.initialize();
        } catch (IOException e) {
            // input is not an mp3 file or could not create file
            errorCode = Constants.LAME_ERROR_INIT_DECODER;
        }
        if (errorCode == 0) {
            try {
                lame.decode();
            } catch (IOException e) {
                // failed to read pcm data/failed to write mp3 data
                errorCode = Constants.LAME_ERROR_DECODE_IO;
            }
        }

        lame.cleanup();
        if(errorCode == 0)
            return true;
        return false;
    }

    public static boolean soundTouchTempo(File input, File output, int tempo /*same as soundTouch.Tempo*/) {
        SoundTouch st = new SoundTouch();
        Log.i("SoundTouch", "tempo : " + tempo);
        st.setTempo(tempo * 0.01f);
        st.setPitchSemiTones(0);
        Log.i("SoundTouch", "process file " + input.getAbsolutePath());
        long startTime = System.currentTimeMillis();
        int res = st.processFile(input.getAbsolutePath(), output.getAbsolutePath());
        long endTime = System.currentTimeMillis();
        float duration = (endTime - startTime) * 0.001f;

        Log.i("SoundTouch", "process file done, duration = " + duration);
        if (res == 0)
        {
            return true;
        }

        Log.i("SoundTouch", "ERROR:  " + SoundTouch.getErrorString());
        return false;
    }
}
