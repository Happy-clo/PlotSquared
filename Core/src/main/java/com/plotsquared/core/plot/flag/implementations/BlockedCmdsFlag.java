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
package com.plotsquared.core.plot.flag.implementations;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.plot.flag.FlagParseException;
import com.plotsquared.core.plot.flag.types.ListFlag;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class BlockedCmdsFlag extends ListFlag<String, BlockedCmdsFlag> {
    public static final BlockedCmdsFlag BLOCKED_CMDS_FLAG_NONE =
            new BlockedCmdsFlag(Collections.emptyList());
    protected BlockedCmdsFlag(List<String> valueList) {
        super(valueList, TranslatableCaption.of("flags.flag_category_string_list"),
                TranslatableCaption.of("flags.flag_description_blocked_cmds")
        );
    }
    @Override
    public BlockedCmdsFlag parse(@NonNull String input) throws FlagParseException {
        return flagOf(Arrays.asList(input.split(",")));
    }
    @Override
    public String getExample() {
        return "gamemode survival, spawn";
    }
    @Override
    protected BlockedCmdsFlag flagOf(@NonNull List<String> value) {
        return new BlockedCmdsFlag(value);
    }
}