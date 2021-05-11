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

import hu.zza.clim.menu.MenuEntry.Leaf;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class MenuStructureBuilder {
  private String initialPosition = "root";
  private JSONObject rawMenuStructure;
  private final Map<Position, Leaf> leafMap = new HashMap<>();
  private final MenuStructure menuStructure = new MenuStructure();


  public MenuStructure build() {
    menuStructure.clear();
    buildDraftStructure();
    buildLeafMap();
    buildStructure();
    return menuStructure;
  }

  public void clear() {
    initialPosition = "root";
    rawMenuStructure = null;
    leafMap.clear();
    menuStructure.clear();
  }

  public MenuStructureBuilder setRawMenuStructure(JSONObject rawMenuStructure) {
    this.rawMenuStructure = rawMenuStructure;
    return this;
  }

  public MenuStructureBuilder setInitialPosition(String initialPosition) {
    this.initialPosition = initialPosition;
    return this;
  }

  public MenuStructureBuilder setLeaf(Leaf leaf) {
    leafMap.put(leaf.getPosition(), leaf);
    return this;
  }

  private void buildDraftStructure() {
  }

  private void buildLeafMap() {
  }

  private void buildStructure() {
  }



}
