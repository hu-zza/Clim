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

import static hu.zza.clim.menu.Message.MENU_OPTION_DECORATOR;
import static hu.zza.clim.menu.Message.MENU_POSITION_DECORATOR;
import static hu.zza.clim.menu.Message.SHORT_LICENCE;
import static hu.zza.clim.menu.Message.UNKNOWN_COMMAND;

import hu.zza.clim.UserInterface;
import hu.zza.clim.menu.Message;
import hu.zza.clim.menu.Position;
import hu.zza.clim.menu.ProcessedInput;
import java.util.Arrays;
import java.util.List;

public interface UserInterfaceService {

  static UserInterfaceService of(UserInterface userInterface) {
    switch (userInterface) {
      case NOMINAL:
        return new NominalUserInterface();
      case ORDINAL:
        return new OrdinalUserInterface();
      case ORDINAL_TRAILING_ZERO:
        return new OrdinalTrailingZeroUserInterface();
      case PARAMETRIC:
        return new ParametricUserInterface();
      default:
        throw new IllegalArgumentException();
    }
  }

  /** Prints a short licence information about clim. */
  default void printShortLicence() {
    System.out.println(SHORT_LICENCE.getMessage());
  }

  default void printHeader(String currentPosition) {
    System.out.print(MENU_POSITION_DECORATOR.getMessage(currentPosition));
  }

  default void printOptionList(List<String> options) {
    options.forEach(e -> System.out.print(MENU_OPTION_DECORATOR.getMessage(e)));
  }

  default Position chooseOption(ProcessedInput input, Position[] options) {
    return getValidatedPositionOrThrow(parseInputIntoPosition(input), options);
  }

  default Position parseInputIntoPosition(ProcessedInput input) {
    if (Position.existsByName(input.getCommandString())) {
      return Position.getByName(input.getCommandString());
    }
    throw new IllegalArgumentException(UNKNOWN_COMMAND.getMessage(input));
  }

  default Position getValidatedPositionOrThrow(Position position, Position[] options) {
    if (Arrays.asList(options).contains(position)) {
      return position;
    }
    throw new IllegalArgumentException(Message.INVALID_POSITION.getMessage(position.getName()));
  }
}
