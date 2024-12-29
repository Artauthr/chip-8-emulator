package art.chp8;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Renderer implements Disposable {
    private final ShapeRenderer shapeRenderer;
    private final ScreenViewport viewport;

    public Renderer() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        final OrthographicCamera camera = new OrthographicCamera(
            Processor.SCREEN_WIDTH ,
            Processor.SCREEN_HEIGHT
        );

        camera.position.set(
            (Processor.SCREEN_WIDTH ) / 2f,
            (Processor.SCREEN_HEIGHT ) / 2f,
            0
        );
        camera.update();

        viewport = new ScreenViewport(camera);
    }

    public void draw (boolean[][] grid) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        int width = viewport.getScreenWidth();
        int height = viewport.getScreenHeight();

        for (int row = 0; row < grid.length; row++) {
            float unitWidth = (float) width / grid.length;
            float unitHeight = (float) height / grid[row].length;

            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col]) {
                    float x = row * unitWidth;
                    float y = col * unitHeight;
                    shapeRenderer.rect(x, height - y, unitWidth, unitHeight);
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

