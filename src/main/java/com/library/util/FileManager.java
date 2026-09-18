package com.library.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility class dedicated solely to saving and loading library state
 * to/from a local file using standard Java serialization.
 */
public class FileManager {
    public static final String DEFAULT_FILE_PATH = "data/library.dat";
    private final String filePath;

    public FileManager() {
        this(DEFAULT_FILE_PATH);
    }

    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Checks if the data file currently exists on disk.
     *
     * @return true if the file exists and is a normal file, false otherwise.
     */
    public boolean dataExists() {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Serializes and saves the LibraryData object to disk.
     * Automatically creates the parent directory if it does not exist.
     *
     * @param data The LibraryData object to persist.
     * @return true if saved successfully, false if an error occurred.
     */
    public boolean saveData(LibraryData data) {
        if (data == null) {
            System.err.println("[✗] Error: Cannot save null library data.");
            return false;
        }

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                System.err.println("[✗] Error: Failed to create storage directory '" + parentDir.getPath() + "'.");
                return false;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            System.err.println("[✗] Error saving library data to '" + filePath + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads and deserializes the LibraryData object from disk.
     * Returns null without error if the file simply does not exist yet.
     *
     * @return The restored LibraryData object, or null if missing or unreadable.
     */
    public LibraryData loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            return null; // Normal case on first run
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof LibraryData) {
                return (LibraryData) obj;
            } else {
                System.err.println("[✗] Error: File content in '" + filePath + "' is not a valid library data file.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[✗] Error reading library data from '" + filePath + "'. File may be corrupted: " + e.getMessage());
            return null;
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
