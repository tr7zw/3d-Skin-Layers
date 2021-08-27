package net.fabricmc.example.render;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.core.Direction;

public class CustomizableCube extends Cube {

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
        super(0, 0, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, false, 0f, 0f); // unused
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

    @Override
    public void compile(Pose pose, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        Polygon polygon;
        for (int id = 0; id < polygonCount; id++) {
            polygon = polygons[id];
            Vector3f vector3f = polygon.normal.copy();
            vector3f.transform(matrix3f);
            float l = vector3f.x();
            float m = vector3f.y();
            float n = vector3f.z();
            for (int i = 0; i < 4; i++) {
                Vertex vertex = polygon.vertices[i];
                Vector4f vector4f = new Vector4f(vertex.o, vertex.p, vertex.q, 1.0F);
                vector4f.transform(matrix4f);
                vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay,
                        light, l, m, n);
            }
        }
    }

    private static class Polygon {
        public final Vertex[] vertices;

        public final Vector3f normal;

        public Polygon(Vertex[] vertexs, float f, float g, float h, float i, float j, float k, boolean bl,
                Direction direction) {
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
            this.normal = direction.step();
            if (bl)
                this.normal.mul(-1.0F, 1.0F, 1.0F);
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
            o = pos.x() / 16.0F;
            p = pos.y() / 16.0F;
            q = pos.z() / 16.0F;
        }
    }

}
