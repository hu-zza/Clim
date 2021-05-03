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

import hu.zza.clim.Menu;
import hu.zza.clim.MenuEntry;
import hu.zza.clim.MenuStructure;
import hu.zza.clim.Position;

public class MenuTest implements Position {
  public static void main(String[] args) {
    MenuStructure menuStructure = new MenuStructure();
    menuStructure.put(new MenuEntry.Node(NodeEnumTest.NODE, "...", NodeEnumTest.NODE));

    Menu.of(menuStructure, LeafEnumTest.class, NodeEnumTest.class);
  }

  @Override
  public String name() {
    return null;
  }
}