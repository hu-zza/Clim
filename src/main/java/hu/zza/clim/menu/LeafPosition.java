/*
 *
 * clim  //  Command Line Interface Menu
 *       //  https://git.zza.hu/clim
 *
 * Copyright (C) 2020-2021 Szabó László András // hu-zza
 *
 * This file is part of clim.
 *
 * clim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * clim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.zza.clim.menu;

import hu.zza.clim.Menu;

/**
 * {@link LeafPosition} is not a real, "walkable" point instead of {@link NodePosition}: The inner
 * position of the {@link Menu} never points to a {@link LeafPosition}. (After calling the function
 * of the leaf, according to the result, the menu redirects itself to the proper {@link
 * NodePosition}.)
 */
public final class LeafPosition extends Position {
  public LeafPosition(String name) {
    super(name);
  }
}
