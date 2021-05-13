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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuStructureBuilder {
  private final Set<String> nodePositions = new HashSet<>();
  private final Set<String> leafPositions = new HashSet<>();
  private final Map<String, List<String>> nodeLinks = new HashMap<>();
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
    menuStructure.clear();
  }

  public MenuStructure build() {
    clearBuilt();
    findAllNodesAndLeaves();
    buildStructure();
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

  private void buildStructure() {
    List<NodePosition> nodes =
        nodePositions.stream().map(NodePosition::new).collect(Collectors.toList());
    List<LeafPosition> leaves =
        leafPositions.stream().map(LeafPosition::new).collect(Collectors.toList());

    for (var pos : nodePositions) {
      System.out.printf("%n%n%S%n", pos);
      for (var link : nodeLinks.get(pos)) {
        System.out.println(link);
      }
    }
  }

  private void extractNodesAndLeavesFrom(JSONObject jsonObject) throws JSONException {
    String currentKey;
    String currentClass;
    ArrayList<String> linkBuffer = new ArrayList<>();

    JSONArray keys = jsonObject.names();
    if (keys == null) return;

    for (int i = 0; i < keys.length(); i++) {
      currentKey = keys.get(i).toString();
      currentClass = jsonObject.get(currentKey).getClass().getName();
      nodePositions.add(currentKey);

      switch (currentClass) {
        case "org.json.JSONObject":
          extractNodesAndLeavesFrom((JSONObject) jsonObject.get(currentKey));
          ((JSONObject) jsonObject.get(currentKey))
              .names().toList().stream().map(String.class::cast).forEach(linkBuffer::add);
          break;
        case "org.json.JSONArray":
          JSONArray array = (JSONArray) jsonObject.get(currentKey);

          for (int j = 0; j < array.length(); j++) {
            currentClass = array.get(j).getClass().getName();
            if ("org.json.JSONObject".equals(currentClass)) {
              extractNodesAndLeavesFrom((JSONObject) array.get(j));
              ((JSONObject) array.get(j))
                  .names().toList().stream().map(String.class::cast).forEach(linkBuffer::add);

            } else if ("java.lang.String".equals(currentClass)) {
              String str = array.get(j).toString();
              leafPositions.add(str);
              linkBuffer.add(str);
            }
          }
          break;
        case "java.lang.String":
          String str = jsonObject.get(currentKey).toString();
          leafPositions.add(str);
          linkBuffer.add(str);
          break;
      }

      nodeLinks.put(currentKey, List.copyOf(linkBuffer));
      linkBuffer.clear();
    }
  }
}
