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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

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


  @RepeatedTest(7)
  @DisplayName("[batch] Build omitting (some) parts completely")
  void testMissingSettings(RepetitionInfo repetitionInfo) {
    int i = repetitionInfo.getCurrentRepetition() - 1; // range [0 - 6]

    if (i % 2 == 1) {
      builder.setRawMenuStructure(rawMenuStructure);
    }

    if ((i >> 1) % 2 == 1) {
      builder.setInitialPosition("node2");
    }

    if ((i >> 2) % 2 == 1) {
      setLeaves();
    }
    Assertions.assertThrows(ClimException.class, builder::build);
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

  @Test
  void testWrongInitialPosition() {
    setBuilderCompletely();
    builder.setInitialPosition("NOWHERE");
    Assertions.assertThrows(ClimException.class, builder::build);
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

  @Test
  void testWrongLeafName() {
    setBuilderCompletely();
    Assertions.assertDoesNotThrow(() -> builder.setLeaf("INVALID", a -> 0, "node1", "node2"));
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
