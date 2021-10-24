package dev.tr7zw.skinlayers;

import static dev.tr7zw.skinlayers.render.SolidPixelWrapper.getOffsetPosition;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.tr7zw.skinlayers.render.SolidPixelWrapper.Dimensions;
import dev.tr7zw.skinlayers.render.SolidPixelWrapper.UV;
import net.minecraft.core.Direction;

public class OffsetTest {

    @Test
    public void topToSides() {
        Dimensions dims = new Dimensions(8, 8, 8);
        UV texturePos = new UV(32, 0);
        // up
        assertEquals(new UV(63, 8), getOffsetPosition(new UV(40, 0), new UV(0, -1), dims, texturePos, Direction.UP));
        assertEquals(new UV(56, 8), getOffsetPosition(new UV(47, 0), new UV(0, -1), dims, texturePos, Direction.UP));
        // down
        assertEquals(new UV(40, 8), getOffsetPosition(new UV(40, 7), new UV(0, 1), dims, texturePos, Direction.UP));
        assertEquals(new UV(47, 8), getOffsetPosition(new UV(47, 7), new UV(0, 1), dims, texturePos, Direction.UP));
        // left
        assertEquals(new UV(32, 8), getOffsetPosition(new UV(40, 0), new UV(-1, 0), dims, texturePos, Direction.UP));
        assertEquals(new UV(39, 8), getOffsetPosition(new UV(40, 7), new UV(-1, 0), dims, texturePos, Direction.UP));
        // right
        assertEquals(new UV(55, 8), getOffsetPosition(new UV(47, 0), new UV(1, 0), dims, texturePos, Direction.UP));
        assertEquals(new UV(48, 8), getOffsetPosition(new UV(47, 7), new UV(1, 0), dims, texturePos, Direction.UP));
    }

    @Test
    public void downToSides() {
        Dimensions dims = new Dimensions(8, 8, 8);
        UV texturePos = new UV(32, 0);
        // up
        assertEquals(new UV(56, 15), getOffsetPosition(new UV(48, 0), new UV(0, -1), dims, texturePos, Direction.DOWN));
        assertEquals(new UV(63, 15), getOffsetPosition(new UV(55, 0), new UV(0, -1), dims, texturePos, Direction.DOWN));
        // down
        assertEquals(new UV(40, 15), getOffsetPosition(new UV(48, 7), new UV(0, 1), dims, texturePos, Direction.DOWN));
        assertEquals(new UV(47, 15), getOffsetPosition(new UV(55, 7), new UV(0, 1), dims, texturePos, Direction.DOWN));
        // left
        assertEquals(new UV(32, 15), getOffsetPosition(new UV(48, 0), new UV(-1, 0), dims, texturePos, Direction.DOWN));
        assertEquals(new UV(39, 15), getOffsetPosition(new UV(48, 7), new UV(-1, 0), dims, texturePos, Direction.DOWN));
        // right
        assertEquals(new UV(55, 15), getOffsetPosition(new UV(55, 0), new UV(1, 0), dims, texturePos, Direction.DOWN));
        assertEquals(new UV(48, 15), getOffsetPosition(new UV(55, 7), new UV(1, 0), dims, texturePos, Direction.DOWN));
    }

    @Test
    public void leftToSides() {
        Dimensions dims = new Dimensions(8, 8, 8);
        UV texturePos = new UV(32, 0);
        // up
        assertEquals(new UV(40, 0), getOffsetPosition(new UV(32, 8), new UV(0, -1), dims, texturePos, Direction.EAST));
        assertEquals(new UV(40, 7), getOffsetPosition(new UV(39, 8), new UV(0, -1), dims, texturePos, Direction.EAST));
        // down
        assertEquals(new UV(48, 0), getOffsetPosition(new UV(32, 15), new UV(0, 1), dims, texturePos, Direction.EAST));
        assertEquals(new UV(48, 7), getOffsetPosition(new UV(39, 15), new UV(0, 1), dims, texturePos, Direction.EAST));
        // left
        assertEquals(new UV(63, 8), getOffsetPosition(new UV(32, 8), new UV(-1, 0), dims, texturePos, Direction.EAST));
        assertEquals(new UV(63, 15),
                getOffsetPosition(new UV(32, 15), new UV(-1, 0), dims, texturePos, Direction.EAST));
        // right
        assertEquals(new UV(40, 8), getOffsetPosition(new UV(39, 8), new UV(1, 0), dims, texturePos, Direction.EAST));
        assertEquals(new UV(40, 15), getOffsetPosition(new UV(39, 15), new UV(1, 0), dims, texturePos, Direction.EAST));
    }
    
    @Test
    public void frontToSides() {
        Dimensions dims = new Dimensions(8, 8, 8);
        UV texturePos = new UV(32, 0);
        // up
        assertEquals(new UV(40, 7), getOffsetPosition(new UV(40, 8), new UV(0, -1), dims, texturePos, Direction.SOUTH));
        assertEquals(new UV(47, 7), getOffsetPosition(new UV(47, 8), new UV(0, -1), dims, texturePos, Direction.SOUTH));
        // down
        assertEquals(new UV(48, 7), getOffsetPosition(new UV(40, 15), new UV(0, 1), dims, texturePos, Direction.SOUTH));
        assertEquals(new UV(55, 7), getOffsetPosition(new UV(47, 15), new UV(0, 1), dims, texturePos, Direction.SOUTH));
        // left
        assertEquals(new UV(39, 8), getOffsetPosition(new UV(40, 8), new UV(-1, 0), dims, texturePos, Direction.SOUTH));
        assertEquals(new UV(39, 15),
                getOffsetPosition(new UV(40, 15), new UV(-1, 0), dims, texturePos, Direction.SOUTH));
        // right
        assertEquals(new UV(48, 8), getOffsetPosition(new UV(47, 8), new UV(1, 0), dims, texturePos, Direction.SOUTH));
        assertEquals(new UV(48, 15), getOffsetPosition(new UV(47, 15), new UV(1, 0), dims, texturePos, Direction.SOUTH));
    }
    
    @Test
    public void rightToSides() {
        Dimensions dims = new Dimensions(8, 8, 8);
        UV texturePos = new UV(32, 0);
        // up
        assertEquals(new UV(47, 7), getOffsetPosition(new UV(48, 8), new UV(0, -1), dims, texturePos, Direction.WEST));
        assertEquals(new UV(47, 0), getOffsetPosition(new UV(55, 8), new UV(0, -1), dims, texturePos, Direction.WEST));
        // down
        assertEquals(new UV(55, 7), getOffsetPosition(new UV(48, 15), new UV(0, 1), dims, texturePos, Direction.WEST));
        assertEquals(new UV(55, 0), getOffsetPosition(new UV(55, 15), new UV(0, 1), dims, texturePos, Direction.WEST));
        // left
        assertEquals(new UV(47, 8), getOffsetPosition(new UV(48, 8), new UV(-1, 0), dims, texturePos, Direction.WEST));
        assertEquals(new UV(47, 15),
                getOffsetPosition(new UV(48, 15), new UV(-1, 0), dims, texturePos, Direction.WEST));
        // right
        assertEquals(new UV(56, 8), getOffsetPosition(new UV(55, 8), new UV(1, 0), dims, texturePos, Direction.WEST));
        assertEquals(new UV(56, 15), getOffsetPosition(new UV(55, 15), new UV(1, 0), dims, texturePos, Direction.WEST));
    }
    
    @Test
    public void backToSides() {
        Dimensions dims = new Dimensions(8, 8, 8);
        UV texturePos = new UV(32, 0);
        // up
        assertEquals(new UV(47, 0), getOffsetPosition(new UV(56, 8), new UV(0, -1), dims, texturePos, Direction.NORTH));
        assertEquals(new UV(40, 0), getOffsetPosition(new UV(63, 8), new UV(0, -1), dims, texturePos, Direction.NORTH));
        // down
        assertEquals(new UV(55, 0), getOffsetPosition(new UV(56, 15), new UV(0, 1), dims, texturePos, Direction.NORTH));
        assertEquals(new UV(48, 0), getOffsetPosition(new UV(63, 15), new UV(0, 1), dims, texturePos, Direction.NORTH));
        // left
        assertEquals(new UV(55, 8), getOffsetPosition(new UV(56, 8), new UV(-1, 0), dims, texturePos, Direction.NORTH));
        assertEquals(new UV(55, 15),
                getOffsetPosition(new UV(56, 15), new UV(-1, 0), dims, texturePos, Direction.NORTH));
        // right
        assertEquals(new UV(32, 8), getOffsetPosition(new UV(63, 8), new UV(1, 0), dims, texturePos, Direction.NORTH));
        assertEquals(new UV(32, 15), getOffsetPosition(new UV(63, 15), new UV(1, 0), dims, texturePos, Direction.NORTH));
    }



}
