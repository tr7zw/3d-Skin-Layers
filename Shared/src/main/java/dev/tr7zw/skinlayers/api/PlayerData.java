package dev.tr7zw.skinlayers.api;

public interface PlayerData {

    /**
     * @return The Mesh or null if there is none.
     */
    public Mesh getHeadMesh();

    /**
     * @return The Mesh or null if there is none.
     */
    public Mesh getTorsoMesh();

    /**
     * @return The Mesh or null if there is none.
     */
    public Mesh getLeftArmMesh();

    /**
     * @return The Mesh or null if there is none.
     */
    public Mesh getRightArmMesh();

    /**
     * @return The Mesh or null if there is none.
     */
    public Mesh getLeftLegMesh();

    /**
     * @return The Mesh or null if there is none.
     */
    public Mesh getRightLegMesh();

}
