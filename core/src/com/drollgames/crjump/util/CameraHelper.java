package com.drollgames.crjump.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.drollgames.crjump.objects.AbstractGameObject;

public class CameraHelper {

    private final float MAX_ZOOM_IN = 0.25f;
    private final float MAX_ZOOM_OUT = 10.0f;
    private final float FOLLOW_SPEED = 5.0f;

    public Vector2 position;
    private float zoom;
    private AbstractGameObject target;

    private final static float MOVE_CAMERA_BY = 4.5f;

    private Vector2 adjustedCameraPosition;

    private Vector2 previewVelocity;

    public CameraHelper() {
        position = new Vector2();

        /* adjust the initial position of the camera: */
        position.y = 4.2f;
        position.x = 0.99999f;

        zoom = 1.0f;

        adjustedCameraPosition = new Vector2();

        previewVelocity = new Vector2();
        previewVelocity.set(16, 12);
    }


    public void update(float deltaTime) {
        if (!hasTarget()) return;

        adjustedCameraPosition.x = target.position.x;
        adjustedCameraPosition.y = target.position.y + 1f;
        position.lerp(adjustedCameraPosition, FOLLOW_SPEED * deltaTime);

        // Prevent camera from moving down too far
        position.y = Math.max(-1f, position.y);
    }

    public void updateForPreview(float deltaTime) {
        position.x += previewVelocity.x * deltaTime;
        adjustedCameraPosition.x = position.x;
        position.lerp(adjustedCameraPosition, FOLLOW_SPEED * deltaTime);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void addZoom(float amount) {
        setZoom(zoom + amount);
    }

    public void setZoom(float zoom) {
        this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
    }

    public float getZoom() {
        return zoom;
    }

    public void setTarget(AbstractGameObject target) {
        this.target = target;
    }

    public AbstractGameObject getTarget() {
        return target;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public boolean hasTarget(AbstractGameObject target) {
        return hasTarget() && this.target.equals(target);
    }

    public void applyTo(OrthographicCamera camera) {
        camera.position.x = position.x + MOVE_CAMERA_BY;
        camera.position.y = position.y;
        camera.zoom = zoom;
        camera.update();
    }

}
