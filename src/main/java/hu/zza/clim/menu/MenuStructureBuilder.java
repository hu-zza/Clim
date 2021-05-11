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
import java.util.TreeSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuStructureBuilder {
  private final Set<NodePosition> nodePositions = new HashSet<>();
  private final Set<NodePosition> leafPositions = new HashSet<>();
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

    JSONObject obj = rawMenuStructure;
    Set<NodePosition> nodePositions = new HashSet<>();
    TreeSet<String> forProcessing = new TreeSet<>(rawMenuStructure.keySet());
    String key;

    while (!forProcessing.isEmpty()) {
      key = forProcessing.pollFirst();
      nodePositions.add(new NodePosition(key));

      // forProcessing.addAll()
    }
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

    JSONArray keys = jsonObject.names();
    for (int i = 0; i < keys.length(); i++) {
      String currentKey = keys.get(i).toString();
      if (jsonObject.get(currentKey).getClass().getName().equals("org.json.JSONObject")) {
        nodePositions.add(new NodePosition(currentKey));
        extractNodesAndLeaves((JSONObject) jsonObject.get(currentKey));
      } else if (jsonObject
          .get(currentKey)
          .getClass()
          .getName()
          .equals("org.json.JSONArray")) {
        for (int j = 0; j < ((JSONArray) jsonObject.get(currentKey)).length(); j++) {
          if (((JSONArray) jsonObject.get(currentKey))
              .get(j)
              .getClass()
              .getName()
              .equals("org.json.JSONObject")) {
            nodePositions.add(new NodePosition(currentKey));
            extractNodesAndLeaves((JSONObject) ((JSONArray) jsonObject.get(currentKey)).get(j));
          }
        }
      } else {
        nodePositions.add(new NodePosition(currentKey));
      }
    }
  }
}
