package com.tulskiy.camomile.audio.formats.mp3;

import android.util.Log;

import com.tulskiy.camomile.audio.model.AudioFileReader;
import com.tulskiy.camomile.audio.model.Track;
import org.jaudiotagger.audio.mp3.MP3File;

import java.io.File;

/**
 * Author: Denis_Tulskiy
 * Date: 5/14/12
 */
public class MP3FileReader extends AudioFileReader {

    @Override
    protected Track read(Track track, File file) {
        try {
            MP3File mp3File = new MP3File(file, MP3File.LOAD_ALL, true);

            copyHeaderFields(mp3File.getMP3AudioHeader(), track);
            copyTagFields(mp3File.getTag(), track);
            return track;
        } catch (Exception e) {
            Log.e("MP3FileReader","could not read tags for file: " + track.path, e);
        }
        return null;
    }
}
