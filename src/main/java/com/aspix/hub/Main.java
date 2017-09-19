package com.aspix.hub;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.inventory.ItemStack;
import org.cantaloupe.inventory.Skull;
import org.cantaloupe.player.Player;
import org.cantaloupe.player.PlayerManager;
import org.cantaloupe.plugin.CantaloupePlugin;
import org.cantaloupe.scoreboard.Objective;
import org.cantaloupe.scoreboard.Objective.DisplaySlot;
import org.cantaloupe.scoreboard.Scoreboard;
import org.cantaloupe.scoreboard.entry.SpaceEntry;
import org.cantaloupe.scoreboard.entry.TextEntry;
import org.cantaloupe.service.services.ScheduleService;
import org.cantaloupe.statue.ArmorStandStatue;
import org.cantaloupe.text.Text;
import org.cantaloupe.world.World;
import org.cantaloupe.world.location.ImmutableLocation;
import org.joml.Vector2f;
import org.joml.Vector3d;

import com.aspix.hub.fish.Fish;
import com.aspix.hub.orb.OrbPackage;
import com.aspix.hub.particles.LeafParticles;
import com.aspix.hub.particles.WaterParticles;

public class Main extends CantaloupePlugin {
    @Override
    public void onPreInit() {
        Cantaloupe.getPlayerManager().inject(PlayerManager.Scopes.LOAD, player -> {
            //OrbRing orb = new OrbRing(player);
            //orb.place();
            
            player.setScoreboard(this.createScoreboard(player));
        });
    }
    
    @Override
    public void onInit() {
        World world = Cantaloupe.getWorldManager().getWorld("lobby1");
        
        ItemStack chestplate = ItemStack.of(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta m = (LeatherArmorMeta) chestplate.getItemMeta();
        m.setColor(Color.WHITE);
        chestplate.setItemMeta(m);
        
        ItemStack leggings = ItemStack.of(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta m2 = (LeatherArmorMeta) leggings.getItemMeta();
        m2.setColor(Color.GRAY);
        leggings.setItemMeta(m2);
        
        ArmorStandStatue statue = ArmorStandStatue.builder()
                .location(ImmutableLocation.of(world, new Vector3d(-3, 65.5, 8), new Vector2f(-45, 0)))
                .small(true)
                .basePlate(false)
                .arms(true)
                .chestplate(chestplate)
                .leggings(leggings)
                .helmet(Skull.fromTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ4NDJhNzVlZTA5ZTFjY2U1NjczNDU2YzUxMjliNGU4YmEyODdjM2QzNDBhNjNkOWNjNjhiYTY1MGIwIn19fQ=="))
                .build();
        statue.place();
        
        OrbPackage orbPackage = new OrbPackage(ImmutableLocation.of(world, new Vector3d(-9, 78, 38), new Vector2f(0, 0)));
        orbPackage.place();
        
        for(int i = 0; i < 20; i++) {
            Fish fish = new Fish(ImmutableLocation.of(world, new Vector3d(-18, 46.5, -49), new Vector2f(0, 0)));
            fish.place();
        }
        
        for(int i = 0; i < 20; i++) {
            Fish fish = new Fish(ImmutableLocation.of(world, new Vector3d(-45, 46.5, -46), new Vector2f(0, 0)));
            fish.place();
        }
        
        for(int i = 0; i < 20; i++) {
            Fish fish = new Fish(ImmutableLocation.of(world, new Vector3d(-12, 46.5, -31), new Vector2f(0, 0)));
            fish.place();
        }
        
        LeafParticles p1 = new LeafParticles(ImmutableLocation.of(world, new Vector3d(-29, 53, -43)));
        p1.place();
        
        WaterParticles p2 = new WaterParticles(ImmutableLocation.of(world, new Vector3d(6, 58.5, -4)));
        p2.place();
    }

    @Override
    public void onDeinit() {
        
    }
    
    private Scoreboard createScoreboard(Player player) {
        Scoreboard scoreboard = Scoreboard.of();
        Objective objective = scoreboard.createObjective("side", "dummy");
        objective.setSlot(DisplaySlot.SIDEBAR);
        objective.setTitle(Text.fromLegacy("      &lASPIX     "));
        objective.addEntry(0, SpaceEntry.of());
        objective.addEntry(1, TextEntry.of(Text.of(player.getName())));
        
        ScheduleService service = Cantaloupe.getServiceManager().provide(ScheduleService.class);
        service.repeat("scoreboard:test:" + player.getUUID(), new Runnable() {
            private boolean s = false;
            
            @Override
            public void run() {
                if(s) {
                    objective.setTitle(Text.fromLegacy("      &lASPIX     "));
                } else {
                    objective.setTitle(Text.fromLegacy("      &d&lASPIX     "));
                }

                s = !s;
            }
        }, 20L);
        
        return scoreboard;
    }
}