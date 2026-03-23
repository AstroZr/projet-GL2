package Groupe6.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de sons : charge les clips WAV et les joue à la demande.
 * Singleton. Volume indépendant pour effets et musique.
 */
public class SoundManager {

    private static SoundManager instance;

    private static final String CLICK_PATH = "/sounds/click.wav";
    private static final String TRANSITION_PATH = "/sounds/menu_whosh.wav";
    private static final String MENU_BACK_PATH = "/sounds/menu_back.wav";

    // Cache les sons en memoire pour accelerer la creation de clips concurrents.
    private final Map<String, byte[]> soundCache = new HashMap<>();

    private float volumeEffets = 0.0f;
    private float volumeMusique = 0.0f;

    private SoundManager() {
        preloadSound(CLICK_PATH);
        preloadSound(TRANSITION_PATH);
        preloadSound(MENU_BACK_PATH);
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void preloadSound(String path) {
        byte[] soundBytes = chargerBytes(path);
        if (soundBytes != null) {
            soundCache.put(path, soundBytes);
        }
    }

    private byte[] chargerBytes(String path) {
        try {
            InputStream is = SoundManager.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("[SoundManager] Ressource introuvable : " + path);
                return null;
            }
            // On conserve les octets bruts du fichier (avec l'en-tete WAV),
            // pour pouvoir recreer correctement un AudioInputStream a chaque lecture.
            try (InputStream in = is;
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            System.err.println("[SoundManager] Impossible de charger : " + path + " — " + e.getMessage());
            return null;
        }
    }

    private void jouer(String path, float volume) {
        if (volume <= 0f) return;
        byte[] soundBytes = soundCache.get(path);
        if (soundBytes == null) return;
        try {
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(soundBytes))) {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log10(Math.max(volume, 0.0001)) * 20.0);
                    gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
                }
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.start();
            }
        } catch (Exception e) {
            // Silencieux : ne pas crasher si le son échoue
        }
    }

    public void playClick()      { jouer(CLICK_PATH,      volumeEffets); }
    public void playTransition() { jouer(TRANSITION_PATH, volumeEffets); }
    public void playMenuBack()   { jouer(MENU_BACK_PATH,  volumeEffets); }

    public void setVolumeEffets(float v)  { this.volumeEffets  = Math.max(0f, Math.min(1f, v)); }
    public void setVolumeMusique(float v) { this.volumeMusique = Math.max(0f, Math.min(1f, v)); }
    public float getVolumeEffets()  { return volumeEffets; }
    public float getVolumeMusique() { return volumeMusique; }
}
