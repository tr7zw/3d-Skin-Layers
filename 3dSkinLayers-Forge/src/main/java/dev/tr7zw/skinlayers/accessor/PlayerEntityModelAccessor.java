package dev.tr7zw.skinlayers.accessor;

import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import dev.tr7zw.skinlayers.renderlayers.HeadLayerFeatureRenderer;

/**
 * Used to expose the thinArms setting of the player model
 *
 */
public interface PlayerEntityModelAccessor {
	public boolean hasThinArms();
	public HeadLayerFeatureRenderer getHeadLayer();
	public BodyLayerFeatureRenderer getBodyLayer();
}