package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

class MainMenuScreen extends ScreenAdapter {
    private final TaxeGame game;
    private final OrthographicCamera camera;
    private final Rectangle playBounds;
    private final Rectangle exitBounds;
    private final Vector3 touchPoint;
    private final Texture mapTexture;
    private final Image mapImage;

    public MainMenuScreen(TaxeGame game) {
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);

        playBounds = new Rectangle(TaxeGame.WIDTH / 2 - 200, 350, 400, 100);
        exitBounds = new Rectangle(TaxeGame.WIDTH / 2 - 200, 200, 400, 100);
        touchPoint = new Vector3();

        //Loads the gameMap in
        mapTexture = new Texture(Gdx.files.internal("gamemap.png"));
        mapImage = new Image(mapTexture);
    }

    void update() {
        //Begins the game or exits the application based on where the user presses
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new GameScreen(game));
                return;
            }

            if (exitBounds.contains(touchPoint.x, touchPoint.y))
                Gdx.app.exit();
        }
    }

    void draw() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw transparent map in the background
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        Color c = game.batch.getColor();
        game.batch.setColor(c.r, c.g, c.b, (float) 0.3);
        game.batch.draw(mapTexture, 0, 0);
        game.batch.setColor(c);
        game.batch.end();

        //Draw rectangles, did not use TextButtons because it was easier not to
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.GREEN);
        game.shapeRenderer.rect(playBounds.getX(), playBounds.getY(), playBounds.getWidth(), playBounds.getHeight());
        game.shapeRenderer.setColor(Color.RED);
        game.shapeRenderer.rect(exitBounds.getX(), exitBounds.getY(), exitBounds.getWidth(), exitBounds.getHeight());
        game.shapeRenderer.end();

        //Draw text into rectangles
        game.batch.begin();
        String startGameString = "Start Game";
        game.font.draw(game.batch, startGameString,
                playBounds.getX() + playBounds.getWidth() / 2 - game.font.getBounds(startGameString).width / 2,
                playBounds.getY() + playBounds.getHeight() / 2 + game.font.getBounds(startGameString).height / 2);
        String exitGameString = "Exit";
        game.font.draw(game.batch, exitGameString,
                exitBounds.getX() + exitBounds.getWidth() / 2 - game.font.getBounds(exitGameString).width / 2,
                exitBounds.getY() + exitBounds.getHeight() / 2 + game.font.getBounds(exitGameString).height / 2);

        game.batch.end();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }
}