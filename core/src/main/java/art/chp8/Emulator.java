package art.chp8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Emulator extends ApplicationAdapter {
    private Graphics graphics;

    private final boolean[][] pixels = new boolean[Processor.SCREEN_WIDTH][Processor.SCREEN_HEIGHT];
    @Override
    public void create() {
        graphics = new Graphics();

        pixels[0][0] = true;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        graphics.drawGrid(pixels);
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        graphics.onResize(width, height);
    }
}
