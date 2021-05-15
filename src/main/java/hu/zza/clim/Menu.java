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

import static hu.zza.clim.menu.Message.GNU_GPL;
import static hu.zza.clim.menu.Message.MENU_OPTION_DECORATOR;
import static hu.zza.clim.menu.Message.MENU_ORDINAL_OPTION_DECORATOR;
import static hu.zza.clim.menu.Message.MENU_POSITION_DECORATOR;
import static hu.zza.clim.menu.Message.PROCESSING_FAILED;
import static hu.zza.clim.menu.Message.SHORT_LICENCE;
import static hu.zza.clim.menu.Message.UNKNOWN_COMMAND;

import hu.zza.clim.input.ProcessedInput;
import hu.zza.clim.menu.MenuEntry;
import hu.zza.clim.menu.MenuEntry.Leaf;
import hu.zza.clim.menu.MenuEntry.Node;
import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.Message;
import hu.zza.clim.menu.NodePosition;
import hu.zza.clim.menu.Position;
import hu.zza.clim.parameter.ParameterMatcher;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/** Represents the menu that the user can interact with: navigation, function calls, etc. */
public final class Menu {

  private static final List<String> licenseCommands =
      List.of(
          "license",
          "warranty",
          "liability",
          "about license",
          "about warranty",
          "about liability",
          "show license",
          "show warranty",
          "show liability");

  private final MenuStructure menuStructure;
  private final ControlType[] controlTypes;
  private final InputType inputType;
  private final ParameterMatcher parameterMatcher;
  private NodePosition position;
  private Deque<NodePosition> positionHistory = new ArrayDeque<>();
  private Position command;
  private Position[] options;

  Menu(
      MenuStructure menuStructure,
      ControlType[] controlTypes,
      NodePosition initialPosition,
      ParameterMatcher parameterMatcher) {

    this.menuStructure = menuStructure;
    this.controlTypes = controlTypes;
    this.position = initialPosition;
    this.parameterMatcher = parameterMatcher;
    positionHistory.offer(position);
    inputType =
        (InputType)
            Arrays.stream(controlTypes)
                .filter(e -> e instanceof InputType)
                .findFirst()
                .orElse(InputType.ORDINAL);



    refreshOptions();
  }

  private void refreshOptions() {
    options = menuStructure.get(position).getLinks();
  }

  /** Prints a short licence information about clim. */
  public void printShortLicence() {
    System.out.println(SHORT_LICENCE.getMessage());
  }

  /** Prints the available options from the current position of the {@link Menu}. */
  public void listOptions() {
    refreshOptions();
    printHeader();
    if (options.length != 0) {
      switch (inputType) {
        case ORDINAL:
        case ORDINAL_TRAILING_ZERO:
          printOrdinalMenu();
          break;

        case NOMINAL:
        case PARAMETRIC:
          Arrays.stream(options)
              .map(menuStructure::get)
              .map(MenuEntry::getName)
              .forEach(System.out::println);
          break;

        default:
          break;
      }
    } else {
      System.out.println(Message.NO_OPTIONS.getMessage());
    }
  }

  private void printHeader() {
    System.out.print(MENU_POSITION_DECORATOR.getMessage(position.getName()));
  }

  private void printOrdinalMenu() {
    boolean trailingZero = inputType == InputType.ORDINAL_TRAILING_ZERO;

    for (int i = trailingZero ? 1 : 0; i < options.length; i++) {
      printMenuEntry(menuStructure.get(options[i]), i);
    }

    if (trailingZero) {
      printMenuEntry(menuStructure.get(options[0]), 0);
    }
  }

  private void printMenuEntry(MenuEntry menuEntry, Integer ordinal) {
    if (menuEntry != null) {
      if (ordinal != null) {
        System.out.print(MENU_ORDINAL_OPTION_DECORATOR.getMessage(ordinal, menuEntry.getName()));
      } else {
        System.out.print(MENU_OPTION_DECORATOR.getMessage(menuEntry.getName()));
      }
    }
  }

  /**
   * Choose an option from the {@link Menu#listOptions available ones}. If it is a {@link Node
   * node}, {@link Menu} navigates itself to this position. If it is a {@link Leaf leaf}, {@link
   * Menu} call its function, and according to the result, it navigates itself toward.
   *
   * <p>The desired format of {@code input} depends on the {@link InputType} of the {@link Menu}
   * object:<br>
   * {@link InputType#NOMINAL} requires the exact name of the choosen option.<br>
   * {@link InputType#ORDINAL} and {@link InputType#ORDINAL_TRAILING_ZERO} require a numeric string
   * which is parsable with {@link Integer#parseInt(String)}.<br>
   * {@link InputType#PARAMETRIC} is more flexible (depends on the configuration of the {@link
   * ParameterMatcher}).
   *
   * @param input the choice of the user, its desired format (single integer, alphanumeric text,
   *     etc.) depends on {@link InputType}
   */
  public void chooseOption(String input) {
    if (input == null || input.isBlank()) {
      return;
    }

    if ("<".equals(input)) {
      position = 0 < positionHistory.size() ? positionHistory.pollFirst() : position;
    } else if (licenseCommands.contains(input.toLowerCase())) {
      System.out.println(GNU_GPL.getMessage());
    } else {
      refreshOptions();
      positionHistory.offerFirst(position);
      try {
        switch (inputType) {
          case NOMINAL:
            Position nominal = getPositionByName(input);
            setMenuPosition(getValidatedPositionOrThrow(nominal), ProcessedInput.NULL);
            break;

          case ORDINAL:
          case ORDINAL_TRAILING_ZERO:
            int ordinal = Integer.parseInt(input);
            setMenuPosition(getValidatedPositionOrThrow(ordinal), ProcessedInput.NULL);
            break;

          case PARAMETRIC:
            extractAndUpdateCommandField(input);
            getValidatedPositionOrThrow(command);
            parameterMatcher.setText(input);
            setMenuPosition(command, parameterMatcher.processText(command));
            break;

          default:
            break;
        }
      } catch (Exception e) {
        positionHistory.pollFirst();
        warnAboutInput(input, e);
      }
    }
    refreshOptions();
  }

  private Position getPositionByName(String name) {
    if (Position.existsBy(name)) {
      return Position.getBy(name);
    } else {
      throw new IllegalArgumentException(UNKNOWN_COMMAND.getMessage(name));
    }
  }

  private void setMenuPosition(Position key, ProcessedInput parameterMap) {
    position = menuStructure.get(key).select(parameterMap);
  }

  private Position getValidatedPositionOrThrow(Object choosenOption) {
    String notValid = "";

    switch (inputType) {
      case ORDINAL:
      case ORDINAL_TRAILING_ZERO:
        int ordinal = (Integer) choosenOption;

        if (0 <= ordinal || ordinal < options.length) {
          return options[ordinal];
        } else {
          notValid = String.valueOf(ordinal);
        }
        break;

      case NOMINAL:
      case PARAMETRIC:
        Position nominal = (Position) choosenOption;
        if (Arrays.asList(options).contains(nominal)) {
          return nominal;
        } else {
          notValid = String.valueOf(nominal);
        }
        break;
    }

    throw new IllegalArgumentException(Message.INVALID_POSITION.getMessage(notValid));
  }

  private void extractAndUpdateCommandField(String commandString) {
    parameterMatcher
        .getCommandRegex()
        .matcher(commandString)
        .results()
        .findFirst()
        .ifPresent(m -> command = getPositionByName(m.group(1)));
  }

  private static void warnAboutInput(String input, Exception e) {
    System.err.print(PROCESSING_FAILED.getMessage(input, e.getMessage()));
  }
}
