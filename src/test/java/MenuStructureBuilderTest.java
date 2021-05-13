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

import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.MenuStructureBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;

public class MenuStructureBuilderTest {
  public static void main(String[] args) throws IOException {
    var msb = new MenuStructureBuilder();

    Path structurePath = Path.of("src", "test", "resources", "MenuStructure.txt");

    JSONObject menuStructure = new JSONObject(String.join("", Files.readAllLines(structurePath)));

    msb.setRawMenuStructure(menuStructure);
    MenuStructure ms = msb.build();

  }

}
