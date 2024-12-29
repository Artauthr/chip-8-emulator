package art.chp8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

public class Emulator extends ApplicationAdapter {
    private Renderer renderer;
    private Processor processor;

    private static class EmulatorConfig {
        String romName;
        int speed;
    }

    private EmulatorConfig config;

    private void readConfig () {
        this.config = new EmulatorConfig();

        FileHandle configFile = Gdx.files.internal("emulation_config.xml");
        if (!configFile.exists()) {
            throw new RuntimeException("Could not find emulation_config.xml file");
        }

        XmlReader reader = new XmlReader();
        XmlReader.Element parsed = reader.parse(configFile);

        config.romName = parsed.get("romName");
        config.speed = parsed.getInt("emulationSpeed");
    }

    @Override
    public void create() {
        readConfig();
        renderer = new Renderer();
        processor = new Processor();
        processor.loadROM(config.romName);
    }

    @Override
    public void render () {
        for (int i = 0; i < config.speed; i++) {
            processor.tick();
            renderer.draw(processor.getPixels());
        }
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        renderer.onResize(width, height);
    }
}
