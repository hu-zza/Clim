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
import static hu.zza.clim.menu.Message.INVALID_INITIAL_POSITION;
import static hu.zza.clim.menu.Message.INVALID_LEAF_POSITION;
import static hu.zza.clim.menu.Message.INVALID_NODE_POSITION;
import static hu.zza.clim.menu.Message.INVALID_POSITION;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import hu.zza.clim.menu.LeafPosition;
import hu.zza.clim.menu.MenuEntry.Leaf;
import hu.zza.clim.menu.MenuEntry.Node;
import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.Message;
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

/**
 * Builder class for {@link MenuStructure}.
 *
 * @since 0.1
 */
public final class MenuStructureBuilder {
  private final Set<String> nodePositions = new HashSet<>();
  private final Set<String> leafPositions = new HashSet<>();
  private final Map<String, NodePosition> nodeMap = new HashMap<>();
  private final Map<String, LeafPosition> leafMap = new HashMap<>();
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
    checkLinksPresence(links);
    leafFunction.put(name, function);
    leafLinks.put(name, Arrays.asList(links));
    return this;
  }

  private void checkLinksPresence(String[] links) {
    if (links.length == 0) {
      throw new ClimException(Message.INVALID_NONEMPTY_ARGUMENT.getMessage("links"));
    }
  }

  public void clear() {
    rawMenuStructure = new JsonObject();
    initialPosition = "";
    leafFunction.clear();
    leafLinks.clear();
    clearBuilt();
  }

  private void clearBuilt() {
    nodePositions.clear();
    leafPositions.clear();
    nodeMap.clear();
    leafMap.clear();
    nodeLinks.clear();
    menuStructure = new MenuStructure();
  }

  /**
   * @return a {@link MenuStructure} object
   * @since 0.1
   */
  public MenuStructure build() {
    try {
      return buildMenuStructure();
    } catch (Exception e) {
      throw new ClimException(INITIALIZATION_FAILED.getMessage(e.getMessage()), e);
    }
  }

  private MenuStructure buildMenuStructure() {
    clearBuilt();
    findAllNodesAndLeaves();
    inferInitialPosition();
    checkInitialPosition();
    buildNameMapsFromNameSets();
    buildStructure();
    return menuStructure;
  }

  /**
   * Process the {@code rawMenuStructure} and prepare objects for menu building. The prepared
   * objects are nodePositions, nodeLinks, leafPositions.
   */
  private void findAllNodesAndLeaves() {
    extractNodesAndLeavesFrom(rawMenuStructure);
    leafPositions.removeAll(nodePositions);
  }

  private void extractNodesAndLeavesFrom(JsonObject jsonObject) {
    for (String key : jsonObject.keySet()) {
      linkBuffer.clear();
      nodePositions.add(key);
      processJsonElement(jsonObject.get(key));
      nodeLinks.put(key, List.copyOf(linkBuffer));
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

  private void inferInitialPosition() {
    if (nodePositions.size() == 1) {
      initialPosition = nodePositions.toArray(new String[0])[0];
    }
  }

  private void checkInitialPosition() {
    if (!nodePositions.contains(initialPosition)) {
      throw new IllegalStateException(INVALID_INITIAL_POSITION.getMessage(initialPosition));
    }
  }

  /** Create real {@link Position} objects from String sets and store them in String-keyed maps. */
  private void buildNameMapsFromNameSets() {
    nodeMap.putAll(
        nodePositions.stream()
            .map(NodePosition::new)
            .collect(Collectors.toMap(Position::getName, Function.identity())));

    leafMap.putAll(
        leafPositions.stream()
            .map(LeafPosition::new)
            .collect(Collectors.toMap(Position::getName, Function.identity())));
  }

  private void buildStructure() {
    menuStructure.setInitialPosition(nodeMap.get(initialPosition));
    createNodesAndAddToMenuStructure();
    createLeavesAndAddToMenuStructure();
    menuStructure.setFinalized();
  }

  private void createNodesAndAddToMenuStructure() {
    nodePositions.forEach(
        e -> menuStructure.put(new Node(nodeMap.get(e), e, getLinksArrayByNodeName(e))));
  }

  private Position[] getLinksArrayByNodeName(String key) {
    if (nodeLinks.get(key) == null) {
      throw new IllegalStateException(INVALID_NODE_POSITION.getMessage(key));
    }

    return nodeLinks.get(key).stream()
        .map(getPositionByNameFunction())
        .map(Position.class::cast)
        .toArray(Position[]::new);
  }

  private Function<String, Position> getPositionByNameFunction() {
    return f -> {
      if (nodeMap.containsKey(f)) {
        return nodeMap.get(f);
      } else if (leafMap.containsKey(f)) {
        return leafMap.get(f);
      } else {
        throw new IllegalStateException(INVALID_POSITION.getMessage(f));
      }
    };
  }

  private void createLeavesAndAddToMenuStructure() {
    leafPositions.forEach(
        e ->
            menuStructure.put(
                new Leaf(leafMap.get(e), e, leafFunction.get(e), getLinksArrayByLeafName(e))));
  }

  private NodePosition[] getLinksArrayByLeafName(String key) {
    if (leafLinks.get(key) == null) {
      throw new IllegalStateException(INVALID_LEAF_POSITION.getMessage(key));
    }

    return leafLinks.get(key).stream()
        .map(getNodePositionByNameFunction())
        .toArray(NodePosition[]::new);
  }

  private Function<String, NodePosition> getNodePositionByNameFunction() {
    return f -> {
      if (nodeMap.containsKey(f)) {
        return nodeMap.get(f);
      } else {
        throw new IllegalStateException(INVALID_LEAF_POSITION.getMessage(f));
      }
    };
  }
}
