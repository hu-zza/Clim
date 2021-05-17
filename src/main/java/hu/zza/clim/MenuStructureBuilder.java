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

package hu.zza.clim;

import hu.zza.clim.menu.LeafPosition;
import hu.zza.clim.menu.MenuEntry.Leaf;
import hu.zza.clim.menu.MenuEntry.Node;
import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.NodePosition;
import hu.zza.clim.menu.Position;
import hu.zza.clim.menu.ProcessedInput;
import hu.zza.clim.menu.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class MenuStructureBuilder {
  private JSONObject rawMenuStructure = new JSONObject("{\"root\":\"\"}");
  private String initialPosition = "root";
  private MenuStructure menuStructure = new MenuStructure();

  private final Set<String> nodePositions = new HashSet<>();
  private final Set<String> leafPositions = new HashSet<>();
  private final Map<String, List<String>> nodeLinks = new HashMap<>();
  private final Map<String, List<String>> leafLinks = new HashMap<>();
  private final Map<String, Function<ProcessedInput, Integer>> leafFunction = new HashMap<>();


  public MenuStructureBuilder setRawMenuStructure(String rawMenuStructure) throws JSONException {
    Util.assertNonNull("rawMenuStructure", rawMenuStructure);
    return setRawMenuStructure(new JSONObject(rawMenuStructure));
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

  public MenuStructureBuilder setLeaf(
      String name, Function<ProcessedInput, Integer> function, String... links) {
    Util.assertNonNull(Map.of("name", name, "function", function, "links", links));
    leafFunction.put(name, function);
    leafLinks.put(name, Arrays.asList(links));
    return this;
  }

  public void clear() {
    rawMenuStructure = new JSONObject("{\"root\":\"\"}");
    initialPosition = "root";
    leafLinks.clear();
    leafFunction.clear();
    clearBuilt();
  }

  private void clearBuilt() {
    nodePositions.clear();
    leafPositions.clear();
    nodeLinks.clear();
    menuStructure = new MenuStructure();
  }

  public MenuStructure build() {
    clearBuilt();
    findAllNodesAndLeaves();
    buildStructure();
    menuStructure.setFinalized();
    return menuStructure;
  }

  private void findAllNodesAndLeaves() {
    extractNodesAndLeavesFrom(rawMenuStructure);
    leafPositions.removeAll(nodePositions);
  }

  private void buildStructure() {
    Map<String, NodePosition> nodeMap =
        nodePositions.stream()
            .map(NodePosition::new)
            .collect(Collectors.toMap(Position::getName, Function.identity()));

    Map<String, LeafPosition> leafMap =
        leafPositions.stream()
            .map(LeafPosition::new)
            .collect(Collectors.toMap(Position::getName, Function.identity()));

    menuStructure.setInitialPosition(nodeMap.get(initialPosition));

    nodePositions.forEach(
        e ->
            menuStructure.put(
                new Node(
                    nodeMap.get(e),
                    e,
                    nodeLinks.get(e).stream()
                        .map(f -> nodeMap.containsKey(f) ? nodeMap.get(f) : leafMap.get(f))
                        .map(Position.class::cast)
                        .toArray(Position[]::new))));

    leafPositions.forEach(
        e ->
            menuStructure.put(
                new Leaf(
                    leafMap.get(e),
                    e,
                    leafFunction.get(e),
                    leafLinks.get(e).stream()
                        .filter(nodePositions::contains)
                        .map(nodeMap::get)
                        .toArray(NodePosition[]::new))));
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
