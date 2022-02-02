package dev.tr7zw.skinlayers.render;

import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector3f;

import dev.tr7zw.skinlayers.Direction;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;

public class CustomizableCube {

    private final Direction[] hidden;
    private final Polygon[] polygons;
    private int polygonCount = 0;
    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;
    
    public CustomizableCube(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY,
            float extraZ, boolean mirror, float textureWidth, float textureHeight, Direction[] hide) {
        this.hidden = hide;
        this.minX = x;
        this.minY = y;
        this.minZ = z;
        this.maxX = x + sizeX;
        this.maxY = y + sizeY;
        this.maxZ = z + sizeZ;
        this.polygons = new Polygon[6];
        float pX = x + sizeX;
        float pY = y + sizeY;
        float pZ = z + sizeZ;
        x -= extraX;
        y -= extraY;
        z -= extraZ;
        pX += extraX;
        pY += extraY;
        pZ += extraZ;
        if (mirror) {
            float i = pX;
            pX = x;
            x = i;
        }
        Vertex vertex = new Vertex(x, y, z, 0.0F, 0.0F);
        Vertex vertex2 = new Vertex(pX, y, z, 0.0F, 8.0F);
        Vertex vertex3 = new Vertex(pX, pY, z, 8.0F, 8.0F);
        Vertex vertex4 = new Vertex(x, pY, z, 8.0F, 0.0F);
        Vertex vertex5 = new Vertex(x, y, pZ, 0.0F, 0.0F);
        Vertex vertex6 = new Vertex(pX, y, pZ, 0.0F, 8.0F);
        Vertex vertex7 = new Vertex(pX, pY, pZ, 8.0F, 8.0F);
        Vertex vertex8 = new Vertex(x, pY, pZ, 8.0F, 0.0F);

        float l = u + sizeZ + sizeX;
        float n = u + sizeZ + sizeX + sizeZ;

        float q = v + sizeZ;
        float r = v + sizeZ + sizeY;
        
        if(visibleFace(Direction.DOWN))
            this.polygons[polygonCount++] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.DOWN);
        if(visibleFace(Direction.UP))
            this.polygons[polygonCount++] = new Polygon(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.UP);
        if(visibleFace(Direction.WEST))
            this.polygons[polygonCount++] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.WEST);
        if(visibleFace(Direction.NORTH))
            this.polygons[polygonCount++] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.NORTH);
        if(visibleFace(Direction.EAST))
            this.polygons[polygonCount++] = new Polygon(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
        if(visibleFace(Direction.SOUTH))
            this.polygons[polygonCount++] = new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
    }
    
    private boolean visibleFace(Direction face) {
        for(Direction dir : hidden) {
            if(dir == face)return false;
        }
        return true;
    }

    public void render(WorldRenderer worldRenderer, boolean redTint) {
        Polygon polygon;
        for (int id = 0; id < polygonCount; id++) {
            polygon = polygons[id];
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            for (int i = 0; i < 4; i++) {
                Vertex vertex = polygon.vertices[i];
                worldRenderer
                .pos(vertex.pos.x, vertex.pos.y,
                        vertex.pos.z)
                .tex(vertex.u, vertex.v).color(1f, redTint ? 0.5f : 1f, redTint ? 0.5f : 1f, 1f).normal(polygon.normal.x, polygon.normal.y, polygon.normal.z)
                .endVertex();
            }
            Tessellator.getInstance().draw();
        }
    }

    private static class Polygon {
        public final Vertex[] vertices;

        public final Vector3f normal;

        public Polygon(Vertex[] vertexs, float f, float g, float h, float i, float j, float k, boolean bl,
                Direction dir) {
            this.vertices = vertexs;
            float l = 0.0F / j;
            float m = 0.0F / k;
            vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
            vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
            vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
            vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
            if (bl) {
                int n = vertexs.length;
                for (int o = 0; o < n / 2; o++) {
                    Vertex vertex = vertexs[o];
                    vertexs[o] = vertexs[n - 1 - o];
                    vertexs[n - 1 - o] = vertex;
                }
            }
            this.normal = dir.step();
            if (bl)
                this.normal.setX(this.normal.getX()*-1);
        }
    }

    private static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;
        public final float o,p,q;

        public Vertex(float f, float g, float h, float i, float j) {
            this(new Vector3f(f, g, h), i, j);
        }

        public Vertex remap(float f, float g) {
            return new Vertex(this.pos, f, g);
        }

        public Vertex(Vector3f vector3f, float f, float g) {
            this.pos = vector3f;
            this.u = f;
            this.v = g;
            o = pos.x / 16.0F;
            p = pos.y / 16.0F;
            q = pos.z / 16.0F;
        }
    }

}
