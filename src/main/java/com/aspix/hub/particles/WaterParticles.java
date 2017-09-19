package com.aspix.hub.particles;

import java.util.ArrayList;
import java.util.List;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.player.Player;
import org.cantaloupe.service.services.ParticleService;
import org.cantaloupe.service.services.ParticleService.ParticleType;
import org.cantaloupe.world.WorldObject;
import org.cantaloupe.world.location.ImmutableLocation;
import org.joml.Vector3f;

public class WaterParticles extends WorldObject {
    private final ImmutableLocation location;
    private final List<Player> players;
    
    private ParticleService particleService = null;
    
    public WaterParticles(ImmutableLocation location) {
        this.location = location.add(0.5, 0, 0.5);
        this.players = new ArrayList<Player>();
        
        this.particleService = Cantaloupe.getServiceManager().provide(ParticleService.class);  
    }

    public void place() {
        this.location.getWorld().place(this);
    }

    public void remove() {
        this.location.getWorld().remove(this);
    }

    public void placeFor(Player player) {
        if (!this.isPlacedFor(player)) {
            this.players.add(player);
        }
    }

    public void removeFor(Player player) {
        if (this.isPlacedFor(player)) {
            this.players.remove(player);
        }
    }

    @Override
    public void tickFor(Player player) {
        if(player.isDirty()) {
            this.removeFor(player);
        } else {
            if (player.getLocation().getPosition().distance(this.getLocation().getPosition()) <= 48 && player.getWorld() == this.getLocation().getWorld()) {
                this.placeFor(player);
            } else {
                this.removeFor(player);
            }
        }
    }

    @Override
    public void tick() {
        this.effect();
    }

    protected void onPlaced() {
        for (Player player : this.location.getWorld().getPlayers()) {
            this.tickFor(player);
        }
    }

    @Override
    protected void onRemoved() {
        this.players.clear();
    }

    public boolean isPlacedFor(Player player) {
        return this.players.contains(player);
    }

    private void effect() {
        this.particleService.display(ParticleType.WATER_BUBBLE, this.location, new Vector3f(0.75f, 0.25f, 0.75f), 3, this.players);
    }

    @Override
    public ImmutableLocation getLocation() {
        return this.location;
    }
    
    public List<Player> getPlayers() { 
        return this.players;
    }
}