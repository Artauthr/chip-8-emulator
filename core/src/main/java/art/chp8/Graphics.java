package art.chp8;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Graphics implements Disposable {
    private final ShapeRenderer shapeRenderer;
    private final ScreenViewport viewport;

    private static final int SCREEN_W = 8;
    private static final int SCREEN_H = 8;

    private static final int UNITS_PER_PIXEL = 10;

    public Graphics() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        final OrthographicCamera camera = new OrthographicCamera(
            SCREEN_W * UNITS_PER_PIXEL,
            SCREEN_H * UNITS_PER_PIXEL
        );

        // Adjust the camera to start at the bottom-left corner
        camera.position.set(
            (SCREEN_W * UNITS_PER_PIXEL) / 2f, // Center X
            (SCREEN_H * UNITS_PER_PIXEL) / 2f, // Center Y
            0
        );

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1); // Adjust camera scaling to fit the grid
    }

    public void drawGrid(boolean[][] grid) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col]) {
                    float x = col * UNITS_PER_PIXEL;
                    float y = (Processor.SCREEN_HEIGHT - 1 - row) * UNITS_PER_PIXEL; // Flip Y for screen coordinates
                    shapeRenderer.rect(x, y, UNITS_PER_PIXEL, UNITS_PER_PIXEL);
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

