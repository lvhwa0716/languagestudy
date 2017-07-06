package com.tulskiy.camomile.audio;

/**
 * Author: Denis_Tulskiy
 * Date: 1/10/12
 */
public class AudioFormat {
    private int sampleRate;
    private int channels;
    private int sampleSizeInBits;
    private int frameSize;

    private int encoding;
    private int channelConfig;

    public AudioFormat(int sampleRate, int channels, int sampleSizeInBits) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.sampleSizeInBits = sampleSizeInBits;
        frameSize = ((sampleSizeInBits + 7) / 8) * channels;

        if (sampleSizeInBits == 8) {
            encoding = android.media.AudioFormat.ENCODING_PCM_8BIT;
        } else if (sampleSizeInBits == 16) {
            encoding = android.media.AudioFormat.ENCODING_PCM_16BIT;
        } else {
            encoding = android.media.AudioFormat.ENCODING_INVALID;
        }

        if (channels == 1) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_MONO;
        } else if (channels == 2) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        } else {
            channelConfig = android.media.AudioFormat.CHANNEL_INVALID;
        }
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public int getEncoding() {
        return encoding;
    }

    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public int getFrameSize() {
        return frameSize;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("SampleRate : %d\n", sampleRate));
        sb.append(String.format("Channels : %d\n", channels));
        sb.append(String.format("Encoding : %d\n", encoding));
        sb.append(String.format("SampleSizeInBits : %d\n", sampleSizeInBits));
        sb.append(String.format("ChannelConfig : %d\n", channelConfig));
        sb.append(String.format("FrameSize : %d\n", frameSize));
        return sb.toString();
    }
}
