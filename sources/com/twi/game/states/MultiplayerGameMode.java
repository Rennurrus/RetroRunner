package com.twi.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MultiplayerGameMode extends State {
    private Texture background;
    private List<String> bluetoothDevicesList;
    private OrthographicCamera camera = new OrthographicCamera();
    private Skin skin;
    private Stage stage;
    private Table table;

    public MultiplayerGameMode(GameStatesManager gsm) {
        super(gsm);
        this.camera.setToOrtho(false, 800.0f, 480.0f);
        int random = (int) ((Math.random() * 4.0d) + 1.0d);
        this.background = new Texture("Menu/Background/1.png");
        this.bluetoothDevicesList = new List<>(new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json")));
        this.bluetoothDevicesList.setItems((T[]) new String[]{"test1", "test2", "test3", "test4"});
        this.bluetoothDevicesList.setWidth(100.0f);
        this.bluetoothDevicesList.setHeight(100.0f);
        List<String> list = this.bluetoothDevicesList;
        list.setPosition(400.0f - (list.getWidth() / 2.0f), 240.0f - (this.bluetoothDevicesList.getHeight() / 2.0f));
        this.table = new Table();
        this.table.setPosition(0.0f, 0.0f);
        this.table.setHeight(480.0f);
        this.table.setWidth(800.0f);
        this.table.add(this.bluetoothDevicesList).width(300.0f).center();
        this.stage = new Stage();
        this.stage.addActor(this.table);
        Gdx.input.setInputProcessor(this.stage);
    }

    /* access modifiers changed from: protected */
    public void handleInput() {
    }

    public void update(float dTime) {
    }

    public void render(SpriteBatch SpB) {
        SpB.setProjectionMatrix(this.camera.combined);
        SpB.begin();
        SpB.draw(this.background, 0.0f, 0.0f);
        this.bluetoothDevicesList.draw(SpB, 1.0f);
        SpB.end();
    }

    public void dispose() {
        this.background.dispose();
        this.skin.dispose();
    }

    public void pause() {
    }

    public void resume() {
    }
}
