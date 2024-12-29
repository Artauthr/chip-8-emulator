package art.chp8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;

public class Emulator extends ApplicationAdapter {
    private Renderer renderer;
    private Processor processor;

    private float counter;

    @Override
    public void create() {
        renderer = new Renderer();
        processor = new Processor();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        counter += Gdx.graphics.getDeltaTime();
        if (counter >= 0.001f) {
            processor.tick();
            counter = 0;
        }
        renderer.drawGrid(processor.getPixels());
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
