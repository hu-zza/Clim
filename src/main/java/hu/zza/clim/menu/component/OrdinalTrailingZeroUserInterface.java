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

package hu.zza.clim.menu.component;

import static hu.zza.clim.menu.Message.MENU_ORDINAL_OPTION_DECORATOR;
import static hu.zza.clim.menu.Message.UNKNOWN_COMMAND;

import hu.zza.clim.menu.Position;
import java.util.List;
import java.util.stream.IntStream;

public class OrdinalTrailingZeroUserInterface implements UserInterfaceService {
  public void printOptionList(List<String> options) {
    IntStream.range(1, options.size())
        .forEach(
            e -> System.out.print(MENU_ORDINAL_OPTION_DECORATOR.getMessage(e, options.get(e))));
    System.out.print(MENU_ORDINAL_OPTION_DECORATOR.getMessage(0, options.get(0)));
  }

  public Position chooseOption(String input, Position[] options) {
    return getValidatedPositionOrThrow(parseInputIntoPosition(input, options), options);
  }

  public Position parseInputIntoPosition(String input, Position[] options) {
    int ordinal = Integer.parseInt(input);

    if (0 <= ordinal && ordinal < options.length) {
      return options[ordinal];
    }
    throw new IllegalArgumentException(UNKNOWN_COMMAND.getMessage(input));
  }
}
