package com.isoterik.racken.test;

import com.badlogic.gdx.math.Interpolation;
import com.isoterik.racken.GameDriver;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.scenes.transition.SceneTransitionDirection;
import com.isoterik.racken._2d.scenes.transition.SceneTransitions;

public class TestDriver extends GameDriver {
	@Override
	protected Scene initGame() {
		//splashTransition = SceneTransitions.fade(1f);
		splashTransition = SceneTransitions.slice(3f, SceneTransitionDirection.UP_DOWN, 15,
				Interpolation.swing);
		return new SceneManagerTest();
	}
}