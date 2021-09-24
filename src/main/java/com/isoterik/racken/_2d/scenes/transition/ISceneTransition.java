package com.isoterik.racken._2d.scenes.transition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A scene transition is for animating the entrance and exit of scenes.
 *
 * @author imranabdulmalik
 */
public interface ISceneTransition {
    /**
     * Returns the total duration (in seconds) this transition will take
     * @return the total duration (in seconds) this transition will take
     */
    float getDuration();

    /**
     * Renders the current state of the transition.
     * @param batch a sprite batch for rendering
     * @param currentScreen the current scene
     * @param nextScreen the next scene
     * @param alpha the progress of the transition in a range of [0, 1]
     */
    void render(SpriteBatch batch, Texture currentScreen, Texture nextScreen, float alpha);
}
