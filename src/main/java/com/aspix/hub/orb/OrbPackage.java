package com.aspix.hub.orb;

import java.util.Random;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.inventory.Skull;
import org.cantaloupe.player.Player;
import org.cantaloupe.service.services.ParticleService;
import org.cantaloupe.service.services.ParticleService.ParticleType;
import org.cantaloupe.statue.ArmorStandStatue;
import org.cantaloupe.world.WorldObject;
import org.cantaloupe.world.location.ImmutableLocation;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class OrbPackage extends WorldObject {
    private ImmutableLocation location = null;
    private ArmorStandStatue  statue   = null;

    public OrbPackage(ImmutableLocation location) {
        this.location = location.subtract(0, 1.75, 0);

        this.create();
    }

    private void create() {
        this.statue = ArmorStandStatue.builder()
                .location(this.location)
                .helmet(Skull.fromTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZjYzQ4NmMyYmUxY2I5ZGZjYjJlNTNkZDlhM2U5YTg4M2JmYWRiMjdjYjk1NmYxODk2ZDYwMmI0MDY3In19fQ=="))
                .invisible(true)
                .basePlate(false)
                .build();
    }

    public void place() {
        this.statue.place();

        this.location.getWorld().place(this);
    }

    public void remove() {
        this.statue.remove();

        this.location.getWorld().remove(this);
    }

    @Override
    public void tick() {
        this.animate();
    }

    private int     timer        = 0;
    private double  heightOffset = 0.0D;
    private boolean up           = false;

    private void animate() {
        Vector3d newPosition = null;
        Vector2f newRotation = null;

        // Movement
        if (this.timer % 2 == 0) {
            float newYaw = this.statue.getLocation().getRotation().x + 3.6f;

            if (newYaw > 360f) {
                newYaw = 0f;
            }

            if (this.up) {
                if (this.heightOffset >= 0.125D) {
                    this.up = false;
                }

                this.heightOffset += 0.025D;
            } else {
                if (this.heightOffset <= -0.125D) {
                    this.up = true;
                }

                this.heightOffset -= 0.025D;
            }

            newPosition = new Vector3d(this.getLocation().getPosition().x, this.getLocation().getPosition().y + heightOffset, this.getLocation().getPosition().z);
            newRotation = new Vector2f(newYaw, 0f);
            
            this.statue.setLocation(ImmutableLocation.of(this.location.getWorld(), newPosition, newRotation));
        }
        
        // Particles
        if (this.timer % 2 == 0) {
            Random random = new Random();
        
            ParticleService service = Cantaloupe.getServiceManager().provide(ParticleService.class);
            service.display(ParticleType.PORTAL, this.location.add(0.5, 0.5, 0.5), new Vector3f(random.nextFloat() / 3.5f, -0.25f, random.nextFloat() / 3.5f), this.statue.getPlayers());
        }
        
        this.timer++;
    }

    @Override
    protected void onPlaced() {
        
    }

    @Override
    protected void onRemoved() {
        
    }

    public boolean isPlacedFor(Player player) {
        return this.statue.isPlacedFor(player);
    }

    @Override
    public ImmutableLocation getLocation() {
        return this.location;
    }
}