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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuStructureBuilder {
  private final ArrayList<String> nodePositions = new ArrayList<>();
  private final ArrayList<String> leafPositions = new ArrayList<>();
  private final Map<Position, Leaf> leafMap = new HashMap<>();
  private final MenuStructure menuStructure = new MenuStructure();
  private String initialPosition = "root";
  private JSONObject rawMenuStructure;

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

  public void clear() {
    initialPosition = "root";
    rawMenuStructure = null;
    leafMap.clear();
    clearBuilt();
  }

  private void clearBuilt() {
    nodePositions.clear();
    leafPositions.clear();
    leafMap.clear();
    menuStructure.clear();
  }

  public MenuStructure build() {
    clearBuilt();
    findAllNodesAndLeaves();

    buildLeafMap();
    buildStructure();
    System.out.println("\n\nNODES\n");
    nodePositions.forEach(System.out::println);
    System.out.println("\n\nLEAVES\n");
    leafPositions.forEach(System.out::println);
    System.out.println();
    return menuStructure;
  }

  private void findAllNodesAndLeaves() {
    if (rawMenuStructure == null) {
      throw new IllegalStateException(
          Message.INVALID_STATE.getMessage(
              "MenuStructureBuilder#build", "RawMenuStructure can not be null."));
    }
    extractNodesAndLeavesFrom(rawMenuStructure);
    leafPositions.removeAll(nodePositions);
  }

  private void buildLeafMap() {}

  private void buildStructure() {}


  private void extractNodesAndLeavesFrom(JSONObject jsonObject) throws JSONException {
    String currentKey;
    String currentClass;
    JSONArray keys = jsonObject.names();
    if (keys == null) return;

    for (int i = 0; i < keys.length(); i++) {
      currentKey = keys.get(i).toString();
      currentClass = jsonObject.get(currentKey).getClass().getName();
      nodePositions.add(currentKey);

      switch (currentClass) {
        case "org.json.JSONObject":
          extractNodesAndLeavesFrom((JSONObject) jsonObject.get(currentKey));
          break;
        case "org.json.JSONArray":
          JSONArray array = (JSONArray) jsonObject.get(currentKey);

          for (int j = 0; j < array.length(); j++) {
            currentClass = array.get(j).getClass().getName();
            if ("org.json.JSONObject".equals(currentClass)) {
              extractNodesAndLeavesFrom((JSONObject) array.get(j));
            } else if ("java.lang.String".equals(currentClass)) {
              leafPositions.add(array.get(j).toString());
            }
          }
          break;
        case "java.lang.String":
          leafPositions.add(jsonObject.get(currentKey).toString());
          break;
      }
    }
  }

  // TODO: 2021. 05. 11. delete after tests
  public List<String> getNodePositions() {
    return nodePositions;
  }

  // TODO: 2021. 05. 11. delete after tests
  public List<String> getLeafPositions() {
    return leafPositions;
  }
}
