package com.aspix.hub.fish;

import java.util.Random;

import org.bukkit.Material;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.inventory.ItemStack;
import org.cantaloupe.player.Player;
import org.cantaloupe.service.services.ParticleService;
import org.cantaloupe.service.services.ParticleService.ParticleType;
import org.cantaloupe.statue.ArmorStandStatue;
import org.cantaloupe.world.World;
import org.cantaloupe.world.WorldObject;
import org.cantaloupe.world.location.ImmutableLocation;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4d;

public class Fish extends WorldObject {
    private ArmorStandStatue  statue          = null;
    private ImmutableLocation startLocation   = null;
    private Random            random          = null;
    private ParticleService   particleService = null;

    public Fish(ImmutableLocation location) {
        this.startLocation = location.subtract(0.25, 1.75, 0.25);
        this.random = new Random();
        this.particleService = Cantaloupe.getServiceManager().provide(ParticleService.class);

        this.create();
    }

    private void create() {
        this.statue = ArmorStandStatue.builder()
                .location(this.startLocation)
                .helmet(ItemStack.of(Material.RAW_FISH))
                .invisible(true)
                .basePlate(false)
                .build();
    }

    public void place() {
        this.statue.place();

        this.statue.getLocation().getWorld().place(this);
    }

    public void remove() {
        this.statue.remove();

        this.statue.getLocation().getWorld().remove(this);
    }

    @Override
    public void tick() {
        this.animate();
    }

    private int      timer              = 0;
    private Vector3d direction          = null;
    private Vector3i lastBlockPos       = null;
    private int      lastCollision      = 0;
    private int      lastBlockPosChange = 0;
    private int      tries              = 0;

    private void animate() {
        if (this.lastBlockPos == null) {
            this.lastBlockPos = this.startLocation.getBlockPosition();
        }

        if (this.direction == null) {
            int randomX = this.random.nextInt(90 - -90) + -90;
            int randomZ = this.random.nextInt(90 - -90) + -90;

            this.direction = new Vector3d(randomX, 0, randomZ).normalize();
        } else {
            this.statue.setLocation(ImmutableLocation.of(this.statue.getLocation().getWorld(), this.statue.getPosition().add(new Vector3d(this.direction).mul(0.1)), new Vector2f((float) (-(Math.atan2(this.direction.x, this.direction.z)) * 180 / Math.PI) + 180, 0)));
            this.checkPlayer();

            if (this.timer % 20 == 0) {
                try {
                    this.particleService.display(ParticleType.WATER_BUBBLE, this.getLocation().add(0.5, 1.75, 0.5), 1, this.statue.getPlayers());
                } catch (IllegalArgumentException e) {}
            }

            if (this.timer % 2 == 0) {
                if (!this.checkBlocks()) {
                    this.direction.mul(-1);
                }
            }

            if (this.getLocation().getBlockPosition().distance(this.lastBlockPos) >= 4) {
                this.lastBlockPos = this.getLocation().getBlockPosition();
                this.lastBlockPosChange = this.timer;
            }

            if (this.tries >= 5) {
                this.statue.setLocation(this.startLocation);

                this.direction = null;
                this.tries = 0;
            }

            if (this.timer - this.lastBlockPosChange >= 100) {
                this.direction = null;
                this.lastBlockPosChange = this.timer;
                this.tries++;
            }

            if (this.timer - this.lastCollision >= 20) {
                if (this.timer % (this.random.nextInt(100) + 25) == 0) {
                    this.direction = null;
                }
            }
        }

        this.timer++;
    }

    private boolean checkBlocks() {
        World world = this.statue.getLocation().getWorld();

        for (int x = (int) (this.statue.getPosition().x - 1); x < this.statue.getPosition().x + 1; x++) {
            for (int z = (int) (this.statue.getPosition().z - 1); z < this.statue.getPosition().z + 1; z++) {
                if (!world.toHandle().getBlockAt(x, this.statue.getLocation().getBlockPosition().y + 2, z).isLiquid()) {
                    this.lastCollision = this.timer;

                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkPlayer() {
        if (this.timer - this.lastCollision >= 20) {
            World world = this.statue.getLocation().getWorld();

            for (Player player : world.getPlayers()) {
                double distance = player.getPosition().distance(this.getPosition());

                if (distance < 4) {
                    Vector4d direction = new Vector4d();

                    Matrix4d matrix = new Matrix4d();
                    matrix.lookAt(this.getPosition(), player.getPosition(), new Vector3d(0, 1, 0));
                    matrix.rotate(180, 0, 1, 0);
                    matrix.transform(direction);

                    this.direction = new Vector3d(direction.x, 0, direction.z).normalize();
                }
            }
        }

        return true;
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
        return this.statue.getLocation();
    }

    public ImmutableLocation getStartLocation() {
        return this.startLocation;
    }

    public Vector3d getPosition() {
        return this.statue.getPosition();
    }

    public Vector3d getStartPosition() {
        return this.startLocation.getPosition();
    }

    public Vector2f getRotation() {
        return this.statue.getRotation();
    }

    public Vector2f getStartRotation() {
        return this.startLocation.getRotation();
    }
}