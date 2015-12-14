package de.cwiep.vampires.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.cwiep.vampires.GameController;
import de.cwiep.vampires.GameRulesConstants;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(GameRulesConstants.V_WIDTH, GameRulesConstants.V_HEIGHT);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new GameController();
        }
}