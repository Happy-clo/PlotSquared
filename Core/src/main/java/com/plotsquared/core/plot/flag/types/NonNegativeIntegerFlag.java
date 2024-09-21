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
package com.plotsquared.core.plot.flag.types;
import com.plotsquared.core.configuration.caption.Caption;
import org.checkerframework.checker.nullness.qual.NonNull;
public abstract class NonNegativeIntegerFlag<F extends IntegerFlag<F>> extends IntegerFlag<F> {
    protected NonNegativeIntegerFlag(int value, @NonNull Caption flagDescription) {
        super(value, 0, Integer.MAX_VALUE, flagDescription);
    }
    public NonNegativeIntegerFlag(int value, int maximum, @NonNull Caption flagDescription) {
        super(value, 0, maximum, flagDescription);
    }
}