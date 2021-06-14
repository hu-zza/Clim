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

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MenuStructureBuilderTest {
  private static final Path structurePath =
      Path.of("src", "test", "resources", "MenuStructure.txt");
  private static MenuStructureBuilder builder = new MenuStructureBuilder();
  private static String rawMenuStructure;

  @BeforeEach
  void resetAll() {
    builder = new MenuStructureBuilder();
    resetRawMenuStructureString();
  }

  private static void resetRawMenuStructureString() {
    try {
      rawMenuStructure = String.join("", Files.readAllLines(structurePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @ParameterizedTest(name = "[{index}]  rawStructure: {0}; initialPosition: {1}; leaves: {2}")
  @MethodSource()
  void testMissingSettings(boolean structure, boolean initial, boolean leaves) {

    if (structure) {
      builder.setRawMenuStructure(rawMenuStructure);
    }

    if (initial) {
      builder.setInitialPosition("node2");
    }

    if (leaves) {
      setLeaves();
    }
    Assertions.assertThrows(ClimException.class, builder::build);
  }

  static Stream<Arguments> testMissingSettings() {
    return IntStream.rangeClosed(0, 6).mapToObj(
        i -> arguments(i % 2 == 1, (i >> 1) % 2 == 1, (i >> 2) % 2 == 1)
    );
  }

  private void setLeaves() {
    builder
        .setLeaf("leaf1", a -> 0, "node2", "node3", "node1")
        .setLeaf("leaf2", a -> 1, "node3", "node4", "node1")
        .setLeaf("leaf3", a -> 2, "node2", "node3", "node1")
        .setLeaf("leaf4", a -> 0, "root", "node1", "node2")
        .setLeaf("leaf5", a -> 1, "node2", "node3", "node1")
        .setLeaf("leaf6", a -> 2, "node4", "node7", "node9")
        .setLeaf("leaf7", a -> 0, "root", "node3", "node1")
        .setLeaf("leaf8", a -> 1, "node2", "root", "node1")
        .setLeaf("leaf9", a -> 2, "root", "node3", "node10")
        .setLeaf("leaf10", a -> 0, "node5")
        .setLeaf("leaf11", a -> 1, "node2", "node3", "root")
        .setLeaf("leaf12", a -> 2, "node2", "root", "node5");
  }

  @ParameterizedTest
  @CsvSource({"node1, true", "NoDe1, false", "node9, true", "leaf3, false", "'', false"})
  void testGoodAndWrongInitialPosition(String initialPosition, boolean isCorrect) {
    setBuilderCompletely();
    builder.setInitialPosition(initialPosition);
    if (isCorrect) {
      Assertions.assertDoesNotThrow(builder::build);
    } else {
      Assertions.assertThrows(ClimException.class, builder::build);
    }
  }

  private void setBuilderCompletely() {
    builder.setRawMenuStructure(rawMenuStructure);
    builder.setInitialPosition("node3");
    setLeaves();
  }

  @Test
  void testMissingLeaf() {
    builder.setRawMenuStructure(rawMenuStructure);
    builder.setInitialPosition("node3");
    builder
        .setLeaf("leaf1", a -> 0, "node2", "node3", "node1")
        .setLeaf("leaf2", a -> 1, "node3", "node4", "node1")
        .setLeaf("leaf3", a -> 2, "node2", "node3", "node1")
        .setLeaf("leaf4", a -> 0, "root", "node1", "node2")
        .setLeaf("leaf5", a -> 1, "node2", "node3", "node1")
        .setLeaf("leaf6", a -> 2, "node4", "node7", "node9")
        .setLeaf("leaf7", a -> 0, "root", "node3", "node1")
        // .setLeaf("leaf8", a -> 1, "node2", "root", "node1")
        .setLeaf("leaf9", a -> 2, "root", "node3", "node10")
        .setLeaf("leaf10", a -> 0, "node5")
        .setLeaf("leaf11", a -> 1, "node2", "node3", "root")
        .setLeaf("leaf12", a -> 2, "node2", "root", "node5");
    Assertions.assertThrows(ClimException.class, builder::build);
  }

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {"node1", "true", "NoDe1", "leaf3", "node9", "leaf7", "leaf111", "\n", "\t"})
  void testGoodAndWrongLeafName(String leafName) {
    setBuilderCompletely();
    Assertions.assertDoesNotThrow(() -> builder.setLeaf(leafName, a -> 0, "node1", "node2"));
  }

  @Test
  void testWrongLeafLinks() {
    setBuilderCompletely();
    // missing links
    Assertions.assertThrows(ClimException.class, () -> builder.setLeaf("leaf3", a -> 2));
    // not valid, throws @ build()
    Assertions.assertDoesNotThrow(() -> builder.setLeaf("leaf3", a -> 0, "INVALID"));
    Assertions.assertThrows(ClimException.class, builder::build);
  }
}
