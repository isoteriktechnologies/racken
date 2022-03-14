package com.isoterik.racken.test;

import com.isoterik.racken.GameDriver;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.scenes.transition.SceneTransitions;

public class TestDriver extends GameDriver {
	@Override
	protected Scene initGame() {
		racken.assets.enqueueTexture("badlogic.jpg");
		racken.assets.loadAssetsNow();

		splashTransition = SceneTransitions.fade(1f);
		return new ActorMappingTest();
	}
}