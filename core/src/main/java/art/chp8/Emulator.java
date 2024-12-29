package art.chp8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class Emulator extends ApplicationAdapter {
    private Renderer renderer;
    private Processor processor;

    private static final float UPDATE_MS = 1 / 60f; // 60hz, can be changed to anything

    private float tickCounter;

    @Override
    public void create() {
        renderer = new Renderer();
        processor = new Processor();
    }

    @Override
    public void render () {
        tickCounter += Gdx.graphics.getDeltaTime();
        if (tickCounter >= UPDATE_MS) {
            processor.tick();
            tickCounter = 0;
        }
        renderer.draw(processor.getPixels());
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
