package com.voicevanish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Settings stored in config/voicevanish.json. Change them with /voicevanish or edit the file. */
public class VanishConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("voicevanish.json");

    /** Master switch. */
    public boolean enabled = true;

    /** How many chunks around the speaker are affected. */
    public int radiusChunks = 12;

    /** If not empty, a phrase only works after this word, e.g. "delete stone". */
    public String wakeWord = "";

    /** How many chunks are scanned per server tick while clearing blocks (lower = less lag). */
    public int chunksPerTick = 6;

    /** The same name will not trigger again within this many milliseconds. */
    public int cooldownMs = 2500;

    /** Show what the speech recognizer heard on the speaker's action bar. */
    public boolean showHeard = true;

    public static VanishConfig load() {
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE)) {
                VanishConfig cfg = GSON.fromJson(reader, VanishConfig.class);
                if (cfg != null) {
                    cfg.clamp();
                    return cfg;
                }
            } catch (Exception e) {
                VoiceVanishMod.LOGGER.error("Could not read config, using defaults", e);
            }
        }
        VanishConfig cfg = new VanishConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        clamp();
        try (Writer writer = Files.newBufferedWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            VoiceVanishMod.LOGGER.error("Could not save config", e);
        }
    }

    private void clamp() {
        radiusChunks = Math.max(1, Math.min(64, radiusChunks));
        chunksPerTick = Math.max(1, Math.min(64, chunksPerTick));
        cooldownMs = Math.max(0, cooldownMs);
        if (wakeWord == null) wakeWord = "";
        wakeWord = wakeWord.trim().toLowerCase();
    }
}
