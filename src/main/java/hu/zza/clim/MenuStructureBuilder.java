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

import static hu.zza.clim.menu.Message.INITIALIZATION_FAILED;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
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

public final class MenuStructureBuilder {
  private final Set<String> nodePositions = new HashSet<>();
  private final Set<String> leafPositions = new HashSet<>();
  private final ArrayList<String> linkBuffer = new ArrayList<>();
  private final Map<String, List<String>> nodeLinks = new HashMap<>();
  private final Map<String, List<String>> leafLinks = new HashMap<>();
  private final Map<String, Function<ProcessedInput, Integer>> leafFunction = new HashMap<>();
  private JsonObject rawMenuStructure = new JsonObject();
  private String initialPosition = "";
  private MenuStructure menuStructure = new MenuStructure();

  public MenuStructureBuilder setRawMenuStructure(String rawMenuStructure)
      throws JsonParseException {
    Util.assertNonNull("rawMenuStructure", rawMenuStructure);
    return setRawMenuStructure(JsonParser.parseString(rawMenuStructure).getAsJsonObject());
  }

  public MenuStructureBuilder setRawMenuStructure(JsonObject rawMenuStructure) {
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
    rawMenuStructure = new JsonObject();
    initialPosition = "";
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
    try {
      return buildMenuStructure();
    } catch (Exception e) {
      throw new IllegalArgumentException(INITIALIZATION_FAILED.getMessage(e.getMessage()), e);
    }
  }

  private MenuStructure buildMenuStructure() {
    clearBuilt();
    findAllNodesAndLeaves();
    checkBeforeBuild();
    buildStructure();
    menuStructure.setFinalized();
    return menuStructure;
  }

  private void findAllNodesAndLeaves() {
    extractNodesAndLeavesFrom(rawMenuStructure);
    leafPositions.removeAll(nodePositions);
  }

  private void checkBeforeBuild() {
    checkNodes();
    checkInitialPosition();
  }

  private void checkNodes() {
    if (nodePositions.size() == 0) {
      throw new IllegalStateException("There are no node positions.");
    }
  }

  private void checkInitialPosition() {
    if (nodePositions.size() == 1) {
      initialPosition = String.valueOf(nodePositions.toArray()[0]);
    }

    if (!nodePositions.contains(initialPosition)) {
      throw new IllegalStateException("Initial position is invalid. No such node is there.");
    }
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

  private void extractNodesAndLeavesFrom(JsonObject jsonObject) {
    linkBuffer.clear();
    List<String> keys = new ArrayList<>(jsonObject.keySet());

    if (!keys.isEmpty()) {
      extractJsonObjectByKeys(jsonObject, keys);
    }
  }

  private void extractJsonObjectByKeys(JsonObject jsonObject, List<String> keys) {
    for (String key : keys) {
      nodePositions.add(key);
      processJsonElement(jsonObject.get(key));
      nodeLinks.put(key, List.copyOf(linkBuffer));
      linkBuffer.clear();
    }
  }

  private void processJsonElement(JsonElement item) {
    if (item.isJsonObject()) {
      processJsonObject(item.getAsJsonObject());
    } else if (item.isJsonArray()) {
      processJsonArray(item.getAsJsonArray());
    } else if (item.isJsonPrimitive()) {
      processLeafString(item.getAsString());
    }
  }

  private void processJsonObject(JsonObject jsonObject) {
    extractNodesAndLeavesFrom(jsonObject);
    linkBuffer.addAll(jsonObject.keySet());
  }

  private void processJsonArray(JsonArray jsonArray) {
    for (int j = 0; j < jsonArray.size(); j++) {
      JsonElement item = jsonArray.get(j);

      if (item.isJsonObject()) {
        processJsonObject(item.getAsJsonObject());
      } else if (item.isJsonPrimitive()) {
        processLeafString(item.getAsString());
      }
    }
  }

  private void processLeafString(String leafString) {
    leafPositions.add(leafString);
    linkBuffer.add(leafString);
  }
}
