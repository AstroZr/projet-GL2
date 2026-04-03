package Groupe6.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire centralisé des effets sonores du jeu.
 * 
 * Responsabilités:
 * - Charger et cacher les sons WAV en mémoire (byte[])
 * - Jouer les sons à la demande avec contrôle de volume indépendant
 * - Gérer deux canaux: effets (clics, transitions) et musique
 * 
 * Singleton: Une seule instance partagée dans toute l'application.
 * 
 * Exemple:
 *   SoundManager.getInstance().playClick();     // Clic bouton
 *   SoundManager.getInstance().playTransition(); // Changement écran
 */
public class SoundManager {

    private static final Logger logger = LoggerFactory.getLogger(SoundManager.class);

    // ====== SINGLETON ======
    private static SoundManager instance;  // Unique instance

    // ====== CHEMINS RESSOURCES ======
    private static final String CLICK_PATH = "/sounds/click.wav";           // Clic bouton
    private static final String TRANSITION_PATH = "/sounds/menu_whosh.wav";  // Transition écran
    private static final String MENU_BACK_PATH = "/sounds/menu_back.wav";    // Retour menu
    private static final String MUSIC_PATH = "/sounds/musique.wav";
    // ====== CACHE SONS ======
    /** Cache les données brutes WAV en mémoire (path -> byte[]) pour création rapide de clips. */
    private final Map<String, byte[]> soundCache = new HashMap<>();

    // ====== VOLUME ======
    private float volumeEffets = 0.0f;   // Volume des effets sonores (0.0 = muet, 1.0 = max)
    private float volumeMusique = 0.0f;  // Volume de la musique de fond

    private Clip musicClip;

    private SoundManager() {
        preloadSound(CLICK_PATH);
        preloadSound(TRANSITION_PATH);
        preloadSound(MENU_BACK_PATH);
        preloadSound(MUSIC_PATH);
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
            logger.debug("Impossible de jouer le son (environnement sans audio ?): {}", e.getMessage());
        }
    }

    public void startMusique(){
        stopMusique(); // éviter doublons si appelé plusieurs fois
        if(volumeMusique <= 0f) return;
        byte[] soundBytes = soundCache.get(MUSIC_PATH);
        if(soundBytes == null) return;
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(soundBytes));
            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            if(musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)){
                FloatControl gain = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float)(Math.log10(Math.max(volumeMusique, 0.0001)) * 20.0);
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
            }
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch(Exception e){
            logger.debug("Impossible de démarrer la musique : {}", e.getMessage());
        }
    }

    public void stopMusique(){
        if(musicClip != null && musicClip.isRunning()){
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    public void playClick()      { jouer(CLICK_PATH,      volumeEffets); }
    public void playTransition() { jouer(TRANSITION_PATH, volumeEffets); }
    public void playMenuBack()   { jouer(MENU_BACK_PATH,  volumeEffets); }

    public void setVolumeEffets(float v)  { this.volumeEffets  = Math.max(0f, Math.min(1f, v)); }
    public void setVolumeMusique(float v) { 
        this.volumeMusique = Math.max(0f, Math.min(1f, v));
        if(musicClip != null && musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float)(Math.log10(Math.max(volumeMusique, 0.0001)) * 20.0);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
        }
        if(volumeMusique <= 0f) stopMusique();
        else if(musicClip == null) startMusique();
    }
    public float getVolumeEffets()  { return volumeEffets; }
    public float getVolumeMusique() { return volumeMusique; }
}
