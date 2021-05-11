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
import java.util.Objects;

/**
 * Represents positions that construct the structure of a {@link Menu} object: It can symbolize a
 * {@link NodePosition node} or a {@link LeafPosition leaf} by its subinterfaces. This and its
 * subtypes are marker interfaces for user defined enums. These enums function as ID collections.
 */
public abstract class Position {
  private final String name;

  public Position(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Position position = (Position) o;
    return name.equals(position.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
