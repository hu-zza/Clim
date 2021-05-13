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

import hu.zza.clim.ControlType;
import hu.zza.clim.Menu;
import hu.zza.clim.MenuBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuBuilderTest {
  public static void main(String[] args) throws IOException {

    Path structurePath = Path.of("src", "test", "resources", "MenuStructure.txt");

    String menuStructure =
        String.join(
            "", Files.readAllLines(structurePath));

    Menu menu =
        new MenuBuilder()
            .setControlType(ControlType.ORDINAL)
            .setMenuStructure(menuStructure)
            .setLeaf("leaf1", a -> 0, "node2", "node3", "node1")  // node2  pibling
            .setLeaf("leaf2", a -> 1, "node3", "node4", "node1")  // node4  hidden top
            .setLeaf("leaf3", a -> 2, "node2", "node3", "node1")  // node1  pibling
            .setLeaf("leaf4", a -> 0, "root", "node1", "node2")   // root   great-grandparent
            .setLeaf("leaf5", a -> 1, "node2", "node3", "node1")  // node3  sibling
            .setLeaf("leaf6", a -> 2, "node4", "node7", "node9")  // node9  hidden bottom
            .setLeaf("leaf7", a -> 0, "root", "node3", "node1")   // root   parent
            .setLeaf("leaf8", a -> 1, "node2", "root", "node1")   // root   great-grandparent
            .setLeaf("leaf9", a -> 2, "root", "node3", "node10")  // node10 first-cousin once removed
            .setLeaf("leaf10", a -> 0, "node5", "root", "node1")  // node5  pibling
            .setLeaf("leaf11", a -> 1, "node2", "node3", "root")  // node3  visible bottom
            .setLeaf("leaf12", a -> 2, "node2", "root", "node5")  // node5  pibling
            .build();

    menu.listOptions();
    menu.chooseOption("0");
    menu.listOptions();
    menu.chooseOption("0");
    menu.listOptions();
    menu.chooseOption("0");

  }
}
