package art.chp8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntSet;

public class Keypad extends InputAdapter {
    private final IntIntMap keyMapping = new IntIntMap();
    private final IntSet pressedKeys = new IntSet();

    public final static int[] keys = {
        0x0, 0x1, 0x2, 0x3,
        0x4, 0x5, 0x6, 0x7,
        0x8, 0x9, 0xA, 0xB,
        0xC, 0xD, 0xE, 0xF
    };

    public Keypad () {
        Gdx.input.setInputProcessor(this);
        initMapping();
    }

    private void initMapping () {
        keyMapping.put(Input.Keys.NUM_1, keys[0x1]);
        keyMapping.put(Input.Keys.NUM_2, keys[0x2]);
        keyMapping.put(Input.Keys.NUM_3, keys[0x3]);
        keyMapping.put(Input.Keys.NUM_4, keys[0xC]);

        keyMapping.put(Input.Keys.Q, keys[0x4]);
        keyMapping.put(Input.Keys.W, keys[0x5]);
        keyMapping.put(Input.Keys.E, keys[0x6]);
        keyMapping.put(Input.Keys.R, keys[0xD]);

        keyMapping.put(Input.Keys.A, keys[0x7]);
        keyMapping.put(Input.Keys.S, keys[0x8]);
        keyMapping.put(Input.Keys.D, keys[0x9]);
        keyMapping.put(Input.Keys.F, keys[0xE]);

        keyMapping.put(Input.Keys.Z, keys[0xA]);
        keyMapping.put(Input.Keys.X, keys[0x0]);
        keyMapping.put(Input.Keys.C, keys[0xB]);
        keyMapping.put(Input.Keys.V, keys[0xF]);
    }

    @Override
    public boolean keyDown(int keycode) {
        int mappedKey = keyMapping.get(keycode, -1);
        if (mappedKey != -1) {
            pressedKeys.add(mappedKey);
            return true;
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        int mappedKey = keyMapping.get(keycode, -1);
        if (pressedKeys.remove(mappedKey)) {
            return true;
        }
        return super.keyUp(keycode);
    }

    public boolean isKeyDown (int key) {
        return pressedKeys.contains(key);
    }
}
