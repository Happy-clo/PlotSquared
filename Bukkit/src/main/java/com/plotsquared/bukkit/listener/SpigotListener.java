/*
 * PlotSquared, a land and world management plugin for Minecraft.
 * Copyright (C) IntellectualSites <https://intellectualsites.com>
 * Copyright (C) IntellectualSites team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.plotsquared.bukkit.listener;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.FlagContainer;
import com.plotsquared.core.plot.flag.implementations.BeaconEffectsFlag;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
/**
 * Fallback listener for paper events on spigot
 */
public class SpigotListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEffect(@NonNull EntityPotionEffectEvent event) {
        if (event.getCause() != EntityPotionEffectEvent.Cause.BEACON) {
            return;
        }
        Entity entity = event.getEntity();
        Location location = BukkitUtil.adapt(entity.getLocation());
        Plot plot = location.getPlot();
        if (plot == null) {
            return;
        }
        FlagContainer container = plot.getFlagContainer();
        BeaconEffectsFlag effectsEnabled = container.getFlag(BeaconEffectsFlag.class);
        if (effectsEnabled != null && !effectsEnabled.getValue()) {
            event.setCancelled(true);
        }
    }
}