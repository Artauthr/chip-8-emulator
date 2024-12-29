package art.chp8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Renderer implements Disposable {
    private final ShapeRenderer shapeRenderer;
    private final ScreenViewport viewport;

    private static final int SCREEN_W = 64;
    private static final int SCREEN_H = 32;

    private static final int UNITS_PER_PIXEL = 128;

    public Renderer() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        final OrthographicCamera camera = new OrthographicCamera(
            SCREEN_W * UNITS_PER_PIXEL,
            SCREEN_H * UNITS_PER_PIXEL
        );

        camera.position.set(
            (SCREEN_W * UNITS_PER_PIXEL) / 2f,
            (SCREEN_H * UNITS_PER_PIXEL) / 2f,
            0
        );
        camera.update();

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1);
    }

    public void drawGrid(boolean[][] grid) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        int width = viewport.getScreenWidth();
        int height = viewport.getScreenHeight();

        float unitWidth = (float) width / grid.length;
        float unitHeight = (float) height / grid[0].length;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col]) {
                    float x = row * unitWidth;
                    float y = col * unitHeight;
                    shapeRenderer.rect(x, Gdx.graphics.getHeight() - y, unitWidth, unitHeight);
                }
            }
        }

        shapeRenderer.end();
    }

    public void onResize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}

