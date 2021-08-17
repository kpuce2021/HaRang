package cafe.adriel.androidaudioconverter.model;

public enum AudioFormat {
    AAC,
    MP3,
    M4A,
    WMA,
    WAV,
    FLAC,
    AVI,
    MP4;

    public String getFormat() {
        return name().toLowerCase();
    }
}