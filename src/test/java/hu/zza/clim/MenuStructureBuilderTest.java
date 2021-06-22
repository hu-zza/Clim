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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

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

  /** Sets {@link MenuStructureBuilder} completely and correctly. */
  private void setBuilderCompletely() {
    builder.setRawMenuStructure(rawMenuStructure);
    builder.setInitialPosition("node3");
    setAllLeaf();
  }

  /** Set all leaves correctly. */
  private void setAllLeaf() {
    setLeaves(
        "leaf1", "leaf2", "leaf3", "leaf4", "leaf5", "leaf6", "leaf7", "leaf8", "leaf9", "leaf10",
        "leaf11", "leaf12");
  }

  private void setLeaves(String... leafNames) {
    for (var name : leafNames) {
      setOneLeaf(name);
    }
  }

  private void setOneLeaf(String leafName) {
    builder.setLeaf(leafName, a -> 0, "node1", "node2");
  }

  /**
   * Test {@link MenuStructureBuilder#build()} with completely missing components. The build should
   * fail if any of the three components is missing. (There are more than one node, so set initial
   * position is obligatory.)
   *
   * @param structure rawMenuStructure is set with {@link
   *     MenuStructureBuilder#setRawMenuStructure(String)}
   * @param initial initial position is set with {@link
   *     MenuStructureBuilder#setInitialPosition(String)}
   * @param leaves leaves are set with {@link MenuStructureBuilder#setLeaf(String, Function,
   *     String...)}
   */
  @DisplayName("testMissingSettings")
  @ParameterizedTest(
      name = "{displayName} [{index}]  rawStructure: {0}; initialPosition: {1}; leaves: {2}")
  @MethodSource()
  void testMissingSettings(boolean structure, boolean initial, boolean leaves) {

    if (structure) {
      builder.setRawMenuStructure(rawMenuStructure);
    }

    if (initial) {
      builder.setInitialPosition("node2");
    }

    if (leaves) {
      setAllLeaf();
    }
    Assertions.assertThrows(ClimException.class, builder::build);
  }

  /**
   * A simple permutation generator for boolean parameters.
   *
   * @return A boolean pattern.
   */
  static Stream<Arguments> testMissingSettings() {
    return IntStream.rangeClosed(0, 6)
        .mapToObj(i -> arguments(i % 2 == 1, (i >> 1) % 2 == 1, (i >> 2) % 2 == 1));
  }

  /**
   * Test {@link MenuStructureBuilder#build()} with various initial positions. The method should
   * throw exception, if the initial position is not equal with a name of a node. Null is excluded
   * from test values, because we got exception earlier than building.
   *
   * @param initialPosition the position to set up with {@link
   *     MenuStructureBuilder#setInitialPosition(String)}
   * @param isCorrect the validity of parameter initialPosition
   */
  @DisplayName("testGoodAndWrongInitialPosition")
  @ParameterizedTest(name = "{displayName} [{index}] {arguments}")
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

  /**
   * Tests {@link MenuStructureBuilder#build()} with one missing leaf setting. Build() should throw
   * exception, because setting up every leaf is obligatory. (Except pseudo-leaves.)
   */
  @DisplayName("testMissingLeafSettings")
  @ParameterizedTest(name = "{displayName} [{index}] {arguments}")
  @MethodSource
  void testMissingLeafSettings(List<String> leavesToSet) {
    builder.setRawMenuStructure(rawMenuStructure);
    builder.setInitialPosition("node3");
    setLeaves(leavesToSet.toArray(new String[0]));
    Assertions.assertThrows(ClimException.class, builder::build);
  }

  private static Stream<List<String>> testMissingLeafSettings() {
    return IntStream.range(0, 12).mapToObj(MenuStructureBuilderTest::returnAllLeafNameExceptNth);
  }

  private static List<String> returnAllLeafNameExceptNth(int i) {
    List<String> all =
        new ArrayList<>(
            List.of(
                "leaf1", "leaf2", "leaf3", "leaf4", "leaf5", "leaf6", "leaf7", "leaf8", "leaf9",
                "leaf10", "leaf11", "leaf12"));
    all.remove(i);
    return all;
  }

  /**
   * Test {@link MenuStructureBuilder#setLeaf(String, Function, String...)} with different leaf
   * names. It shouldn't throw exception, wrong parameters are simply omitted. How
   *
   * @param leafName name to set with setLeaf()
   */
  @DisplayName("testGoodAndWrongLeafName")
  @ParameterizedTest(name = "{displayName} [{index}] {arguments}")
  @CsvSource({
    "node1, false, true",
    "NoDe1, false, true",
    "leaf3, false, false",
    "leAF3, false, true",
    "node111, false, true",
    "'\n', false, true",
    "'\t', false, true",
    "'', false, true",
    ", true, true"
  })
  void testGoodAndWrongLeafName(String leafName, boolean throwAtSet, boolean throwAtBuild) {
    setBuilderCompletely();
    if (throwAtSet) {
      Assertions.assertThrows(
          ClimException.class, () -> builder.setLeaf(leafName, a -> 0, "node1", "node2"));
    } else {
      Assertions.assertDoesNotThrow(() -> builder.setLeaf(leafName, a -> 0, "node1", "node2"));

      if (throwAtBuild) {
        Assertions.assertThrows(ClimException.class, () -> builder.build());
      } else {
        Assertions.assertDoesNotThrow(() -> builder.build());
      }
    }
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
