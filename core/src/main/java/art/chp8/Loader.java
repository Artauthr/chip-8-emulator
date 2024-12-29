package art.chp8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.nio.file.Files;

public class Loader {
    public static byte[] readBytesFromFile (FileHandle file) {
        try {
            return Files.readAllBytes(file.file().toPath());
        } catch (IOException e) {
            Gdx.app.log("Failed reading rom", file.name(), e);
            return null;
        }
    }



}
