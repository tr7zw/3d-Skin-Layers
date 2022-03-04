package dev.tr7zw.skinlayers.render;

import java.util.HashMap;
import java.util.Map;

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
    protected final Polygon[] polygons;
    protected int polygonCount = 0;
    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;
    
    public CustomizableCube(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY,
            float extraZ, boolean mirror, float textureWidth, float textureHeight, Direction[] hide, Direction[][] hideCorners) {
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
        Vertex vertexNNN = new Vertex(x, y, z, 0.0F, 0.0F);
        Vertex vertexPNN = new Vertex(pX, y, z, 0.0F, 8.0F);
        Vertex vertexPPN = new Vertex(pX, pY, z, 8.0F, 8.0F);
        Vertex vertexNPN = new Vertex(x, pY, z, 8.0F, 0.0F);
        Vertex vertexNNP = new Vertex(x, y, pZ, 0.0F, 0.0F);
        Vertex vertexPNP = new Vertex(pX, y, pZ, 0.0F, 8.0F);
        Vertex vertexPPP = new Vertex(pX, pY, pZ, 8.0F, 8.0F);
        Vertex vertexNPP = new Vertex(x, pY, pZ, 8.0F, 0.0F);

        float minU = u;
        float maxU = u + 1.0F;

        float minV = v;
        float maxV = v + 1.0F;

        Map<Direction.Axis, Direction[]> axisToCorner = new HashMap<>();
        nextCorner: for(Direction[] corner : hideCorners) {
            nextAxis: for(Direction.Axis axis : Direction.Axis.VALUES) {
                for(Direction dir : corner) {
                    if(dir.getAxis() == axis) continue nextAxis;
                }

                axisToCorner.put(axis, corner);
                continue nextCorner;
            }
        }
        
        if(visibleFace(Direction.DOWN))
            this.polygons[this.polygonCount++] = new Polygon(removeCornerVertex(new Vertex[]{vertexPNP, vertexNNP, vertexNNN, vertexPNN}, axisToCorner.get(Direction.Axis.Y)), minU, minV, maxU, maxV, textureWidth, textureHeight, mirror, Direction.DOWN);
        if(visibleFace(Direction.UP))
            this.polygons[this.polygonCount++] = new Polygon(removeCornerVertex(new Vertex[]{vertexPPN, vertexNPN, vertexNPP, vertexPPP}, axisToCorner.get(Direction.Axis.Y)), minU, minV, maxU, maxV, textureWidth, textureHeight, mirror, Direction.UP);
        if(visibleFace(Direction.NORTH))
            this.polygons[this.polygonCount++] = new Polygon(removeCornerVertex(new Vertex[]{vertexPNN, vertexNNN, vertexNPN, vertexPPN}, axisToCorner.get(Direction.Axis.Z)), minU, minV, maxU, maxV, textureWidth, textureHeight, mirror, Direction.NORTH);
        if(visibleFace(Direction.SOUTH))
            this.polygons[this.polygonCount++] = new Polygon(removeCornerVertex(new Vertex[]{vertexNNP, vertexPNP, vertexPPP, vertexNPP}, axisToCorner.get(Direction.Axis.Z)), minU, minV, maxU, maxV, textureWidth, textureHeight, mirror, Direction.SOUTH);
        if(visibleFace(Direction.WEST))
            this.polygons[this.polygonCount++] = new Polygon(removeCornerVertex(new Vertex[]{vertexNNN, vertexNNP, vertexNPP, vertexNPN}, axisToCorner.get(Direction.Axis.X)), minU, minV, maxU, maxV, textureWidth, textureHeight, mirror, Direction.WEST);
        if(visibleFace(Direction.EAST))
            this.polygons[this.polygonCount++] = new Polygon(removeCornerVertex(new Vertex[]{vertexPNP, vertexPNN, vertexPPN, vertexPPP}, axisToCorner.get(Direction.Axis.X)), minU, minV, maxU, maxV, textureWidth, textureHeight, mirror, Direction.EAST);
    }
    
    private boolean visibleFace(Direction face) {
        for(Direction dir : hidden) {
            if(dir == face) return false;
        }
        return true;
    }

    private static Vertex[] removeCornerVertex(Vertex[] vertices, Direction[] corner) {
        if(corner == null) {
            return vertices;
        }

        Vertex except = vertices[0];
        for(int i = 1; i < 4; i++) {
            except = compareVertices(except, vertices[i], corner);
        }

        int index = 0;
        for(int i = 0; i < 4; i++) {
            if(vertices[i] == except) continue;
            vertices[index++] = vertices[i];
        }
        vertices[3] = vertices[2];

        return vertices;
    }

    private static Vertex compareVertices(Vertex vertex1, Vertex vertex2, Direction[] corner) {
        for(Direction dir : corner) {
            double d = dir.getAxis().choose(vertex1.pos.x() - vertex2.pos.x(), vertex1.pos.y() - vertex2.pos.y(), vertex1.pos.z() - vertex2.pos.z()) * dir.getAxisDirection().getStep();
            if(d > 0) {
                return vertex1;
            } else if(d < 0) {
                return vertex2;
            }
        }
        return vertex1;
    }

    @Override
    public void compile(Pose pose, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        Polygon polygon;
        for (int id = 0; id < this.polygonCount; id++) {
            polygon = this.polygons[id];
            Vector3f vector3f = polygon.normal.copy();
            vector3f.transform(matrix3f);
            float x = vector3f.x();
            float y = vector3f.y();
            float z = vector3f.z();
            for (int i = 0; i < 4; i++) {
                Vertex vertex = polygon.vertices[i];
                Vector4f vector4f = new Vector4f(vertex.scaledX, vertex.scaledY, vertex.scaledZ, 1.0F);
                vector4f.transform(matrix4f);
                vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay,
                        light, x, y, z);
            }
        }
    }

    protected static class Polygon {
        public final Vertex[] vertices;

        public final Vector3f normal;

        public Polygon(Vertex[] vertexs, float minU, float minV, float maxU, float maxV, float textureWidth, float textureHeight, boolean mirror,
                       Direction direction) {
            this.vertices = vertexs;
            float zeroWidth = 0.0F / textureWidth;
            float zeroHeight = 0.0F / textureHeight;
            vertexs[0] = vertexs[0].remap(maxU / textureWidth - zeroWidth, minV / textureHeight + zeroHeight);
            vertexs[1] = vertexs[1].remap(minU / textureWidth + zeroWidth, minV / textureHeight + zeroHeight);
            vertexs[2] = vertexs[2].remap(minU / textureWidth + zeroWidth, maxV / textureHeight - zeroHeight);
            vertexs[3] = vertexs[3].remap(maxU / textureWidth - zeroWidth, maxV / textureHeight - zeroHeight);
            if (mirror) {
                int vertexCount = vertexs.length;
                for (int i = 0; i < vertexCount / 2; i++) {
                    Vertex vertex = vertexs[i];
                    vertexs[i] = vertexs[vertexCount - 1 - i];
                    vertexs[vertexCount - 1 - i] = vertex;
                }
            }
            this.normal = direction.step();
            if (mirror)
                this.normal.mul(-1.0F, 1.0F, 1.0F);
        }
    }

    protected static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;
        public final float scaledX, scaledY, scaledZ;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f vector3f, float u, float v) {
            this.pos = vector3f;
            this.u = u;
            this.v = v;
            this.scaledX = pos.x() / 16.0F;
            this.scaledY = pos.y() / 16.0F;
            this.scaledZ = pos.z() / 16.0F;
        }
    }

}
