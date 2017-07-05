package win.i029.ll.languagelisten;

/**
 * Created by lvh on 7/5/17.
 */

import android.util.Log;

import com.intervigil.lame.Decoder;
import com.intervigil.lame.Constants;
import net.surina.soundtouch.SoundTouch;

import java.io.File;
import java.io.IOException;

public class MP3DecoderWithTempo {

    public static boolean decoder(File input, File output) { // to wave file , will long time
        int errorCode = 0;
        if(false == output.exists()) {
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
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
