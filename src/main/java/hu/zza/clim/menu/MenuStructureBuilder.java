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
import hu.zza.clim.menu.MenuEntry.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuStructureBuilder {
  private final Set<NodePosition> nodePositions = new HashSet<>();
  private final Set<LeafPosition> leafPositions = new HashSet<>();
  private final Map<Position, Node> nodePositionMap = new HashMap<>();
  private final Map<Position, Leaf> leafPositionMap = new HashMap<>();
  private final Map<Position, Node> nodeMap = new HashMap<>();
  private final Map<Position, Leaf> leafMap = new HashMap<>();
  private final MenuStructure menuStructure = new MenuStructure();
  private String initialPosition = "root";
  private JSONObject rawMenuStructure;

  public MenuStructure build() {
    clearBuilt();
    buildDraftStructure();
    buildLeafMap();
    buildStructure();
    return menuStructure;
  }

  private void clearBuilt() {
    nodePositions.clear();
    leafPositions.clear();
    nodePositionMap.clear();
    leafPositionMap.clear();
    nodeMap.clear();
    leafMap.clear();
    menuStructure.clear();
  }

  private void buildDraftStructure() {
    if (rawMenuStructure == null) {
      throw new IllegalStateException(
          Message.INVALID_STATE.getMessage(
              "MenuStructureBuilder#build", "RawMenuStructure can not be null."));
    }

    extractNodesAndLeaves(rawMenuStructure);
  }

  private void buildLeafMap() {}

  private void buildStructure() {}

  public void clear() {
    initialPosition = "root";
    rawMenuStructure = null;
    leafMap.clear();
    clearBuilt();
  }

  public MenuStructureBuilder setRawMenuStructure(JSONObject rawMenuStructure) {
    Util.assertNonNull("rawMenuStructure", rawMenuStructure);
    this.rawMenuStructure = rawMenuStructure;
    return this;
  }

  public MenuStructureBuilder setInitialPosition(String initialPosition) {
    Util.assertNonNull("initialPosition", initialPosition);
    this.initialPosition = initialPosition;
    return this;
  }

  public MenuStructureBuilder putLeaf(Leaf leaf) {
    Util.assertNonNull("leaf", leaf);
    leafMap.put(leaf.getPosition(), leaf);
    return this;
  }

  private void extractNodesAndLeaves(JSONObject jsonObject) throws JSONException {
    String currentKey;
    String currentClass;
    JSONArray keys = jsonObject.names();
    if (keys == null) return;

    for (int i = 0; i < keys.length(); i++) {
      currentKey = keys.get(i).toString();
      currentClass = jsonObject.get(currentKey).getClass().getName();
      nodePositions.add(new NodePosition(currentKey));

      switch (currentClass) {
        case "org.json.JSONObject":
          extractNodesAndLeaves((JSONObject) jsonObject.get(currentKey));
          break;
        case "org.json.JSONArray":
          JSONArray array = (JSONArray) jsonObject.get(currentKey);

          for (int j = 0; j < array.length(); j++) {
            currentClass = array.get(j)
                .getClass()
                .getName();
            if ("org.json.JSONObject".equals(currentClass)) {
              nodePositions.add(new NodePosition(currentKey));
              extractNodesAndLeaves((JSONObject) array.get(j));
            } else if ("java.lang.String".equals(currentClass)) {
              leafPositions.add(new LeafPosition(array.get(j)
                  .toString()));
            }
          }
          break;
        case "java.lang.String":
          leafPositions.add(new LeafPosition(jsonObject.get(currentKey)
              .toString()));
          break;
      }
    }
  }

  // TODO: 2021. 05. 11. delete after tests
  public Set<NodePosition> getNodePositions() {
    return nodePositions;
  }

  // TODO: 2021. 05. 11. delete after tests
  public Set<LeafPosition> getLeafPositions() {
    return leafPositions;
  }
}
