package com.aspix.hub.orb;

import java.util.ArrayList;
import java.util.List;

import org.cantaloupe.Cantaloupe;
import org.cantaloupe.math.color.ImmutableColorRGB;
import org.cantaloupe.player.Player;
import org.cantaloupe.service.services.ParticleService;
import org.cantaloupe.service.services.ParticleService.ParticleType;
import org.cantaloupe.world.WorldObject;
import org.cantaloupe.world.location.ImmutableLocation;

public class OrbRing extends WorldObject {
    private final Player owner;
    private final List<Player> players;
    
    private ImmutableColorRGB[] colours = new ImmutableColorRGB[] {
            ImmutableColorRGB.of(255, 0, 0), // Red
            ImmutableColorRGB.of(0, 255, 0), // Green
            ImmutableColorRGB.of(0, 0, 200), // Blue
            ImmutableColorRGB.of(0, 127, 0), // Dark Green
            ImmutableColorRGB.of(138, 43, 226), // Violet
            ImmutableColorRGB.of(255, 153, 0), // Orange
            ImmutableColorRGB.of(0, 255, 255), // Cyan
            ImmutableColorRGB.of(255, 105, 180) // Pink
    };

    public OrbRing(Player owner) {
        this.owner = owner;
        this.players = new ArrayList<Player>();
    }

    public void place() {
        this.owner.getWorld().place(this);
    }

    public void remove() {
        this.owner.getWorld().remove(this);
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
        if(this.owner.isDirty()) {
            this.markDirty();
        }
        
        this.effect();
    }

    protected void onPlaced() {
        for (Player player : this.owner.getWorld().getPlayers()) {
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

    private int amount = 8;
    private double timer = 0;
    private void effect() {
        ParticleService service = Cantaloupe.getServiceManager().provide(ParticleService.class);
        
        double increment = (2.0 * Math.PI) / this.amount;
        
        for(Player player : this.players) {
            for(int i = 0; i < this.amount; i++)
            {
                double angle = i * increment;
                double x = 1.25D * Math.cos(angle + timer);
                double z = 1.25D * Math.sin(angle + timer);
                
                service.display(ParticleType.REDSTONE, this.owner.getLocation().add(x, 2.5, z), this.colours[i], 2, player);
            }
        }
        
        this.timer += 0.0125D;
    }

    @Override
    public ImmutableLocation getLocation() {
        return this.owner.getLocation().add(0, 2.5, 0);
    }
    
    public List<Player> getPlayers() { 
        return this.players;
    }
}