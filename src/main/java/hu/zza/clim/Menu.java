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
import static hu.zza.clim.menu.Message.PROCESSING_FAILED;

import hu.zza.clim.input.ProcessedInput;
import hu.zza.clim.menu.MenuEntry.Leaf;
import hu.zza.clim.menu.MenuEntry.Node;
import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.Message;
import hu.zza.clim.menu.NodePosition;
import hu.zza.clim.menu.Position;
import hu.zza.clim.menu.component.UserInterfaceService;
import hu.zza.clim.parameter.ParameterMatcher;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private final Map<Class<? extends ClimOption>, ClimOption> climOptions;
  private UserInterface inputType = UserInterface.NOMINAL;
  private final UserInterfaceService userInterfaceService;
  private final ParameterMatcher parameterMatcher;
  private final Deque<NodePosition> positionHistory = new ArrayDeque<>();
  private NodePosition position;
  private Position command;
  private Position[] options;

  Menu(MenuStructure menuStructure, ParameterMatcher parameterMatcher, ClimOption... climOptions) {

    this.menuStructure = menuStructure;
    this.parameterMatcher = parameterMatcher;
    this.climOptions = ClimOption.getClimOptionMap(climOptions);

    position = menuStructure.get(null).select(ProcessedInput.NULL);
    userInterfaceService =
        UserInterfaceService.of((UserInterface) this.climOptions.get(UserInterface.class));
    positionHistory.offer(position);
    refreshOptions();
  }

  private void refreshOptions() {
    options = menuStructure.get(position).getLinks();
  }

  /** Prints a short licence information about clim. */
  public void printShortLicence() {
    userInterfaceService.printShortLicence();
  }

  /** Prints the available options from the current position of the {@link Menu}. */
  public void listOptions() {
    refreshOptions();
    userInterfaceService.printHeader(position.getName());
    if (options.length != 0) {
      userInterfaceService.printOptionList(getDisplayableOptions());
    } else {
      System.out.println(Message.NO_OPTIONS.getMessage());
    }
  }


  private List<String> getDisplayableOptions() {
    return Arrays.stream(options).map(Position::getName).collect(Collectors.toList());
  }

//  private void printOrdinalMenu() {
//    boolean trailingZero = inputType == UserInterface.ORDINAL_TRAILING_ZERO;
//
//    for (int i = trailingZero ? 1 : 0; i < options.length; i++) {
//      printMenuEntry(menuStructure.get(options[i]), i);
//    }
//
//    if (trailingZero) {
//      printMenuEntry(menuStructure.get(options[0]), 0);
//    }
//  }
//
//  private void printMenuEntry(MenuEntry menuEntry, Integer ordinal) {
//    if (menuEntry != null) {
//      if (ordinal != null) {
//        System.out.print(MENU_ORDINAL_OPTION_DECORATOR.getMessage(ordinal, menuEntry.getName()));
//      } else {
//        System.out.print(MENU_OPTION_DECORATOR.getMessage(menuEntry.getName()));
//      }
//    }
//  }


//    if ("<".equals(input)) {
//      position = 0 < positionHistory.size() ? positionHistory.pollFirst() : position;
//    } else

//  switch (inputType) {
//    case NOMINAL:
//      Position nominal = getPositionByName(input);
//      setMenuPosition(getValidatedPositionOrThrow(nominal), ProcessedInput.NULL);
//      break;
//
//    case ORDINAL:
//    case ORDINAL_TRAILING_ZERO:
//      int ordinal = Integer.parseInt(input);
//      setMenuPosition(getValidatedPositionOrThrow(ordinal), ProcessedInput.NULL);
//      break;
//
//    case PARAMETRIC:
//      extractAndUpdateCommandField(input);
//      getValidatedPositionOrThrow(command);
//      parameterMatcher.setText(input);
//      setMenuPosition(command, parameterMatcher.processText(command));
//      break;
//
//    default:
//      break;
//  }

  /**
   * Choose an option from the {@link Menu#listOptions available ones}. If it is a {@link Node
   * node}, {@link Menu} navigates itself to this position. If it is a {@link Leaf leaf}, {@link
   * Menu} call its function, and according to the result, it navigates itself toward.
   *
   * <p>The desired format of {@code input} depends on the {@link UserInterface} of the {@link Menu}
   * object:<br>
   * {@link UserInterface#NOMINAL} requires the exact name of the choosen option.<br>
   * {@link UserInterface#ORDINAL} and {@link UserInterface#ORDINAL_TRAILING_ZERO} require a numeric
   * string which is parsable with {@link Integer#parseInt(String)}.<br>
   * {@link UserInterface#PARAMETRIC} is more flexible (depends on the configuration of the {@link
   * ParameterMatcher}).
   *
   * @param input the choice of the user, its desired format (single integer, alphanumeric text,
   *     etc.) depends on {@link UserInterface}
   */
  public void chooseOption(String input) {
    if (input == null || input.isBlank()) {
      return;
    } else if (licenseCommands.contains(input.toLowerCase())) {
      System.out.println(GNU_GPL.getMessage());
    } else {
      refreshOptions();
      positionHistory.offerFirst(position);
      tryToChooseAnOption(input);
    }
    refreshOptions();
  }

  private void tryToChooseAnOption(String input) {
    try {
      Position choosenPosition = userInterfaceService.chooseOption(input, options);
      setMenuPosition(choosenPosition, null); // todo implement parameterMatcher service
    } catch (Exception e) {
      positionHistory.pollFirst();
      warnAboutInput(input, e);
    }
  }

  private void setMenuPosition(Position key, ProcessedInput parameterMap) {
    position = menuStructure.get(key).select(parameterMap);
  }

//  private Position getPositionByName(String name) {
//    if (Position.existsBy(name)) {
//      return Position.getBy(name);
//    } else {
//      throw new IllegalArgumentException(UNKNOWN_COMMAND.getMessage(name));
//    }
//  }
//
//
//  private Position getValidatedPositionOrThrow(Object choosenOption) {
//    String notValid = "";
//
//    switch (inputType) {
//      case ORDINAL:
//      case ORDINAL_TRAILING_ZERO:
//        int ordinal = (Integer) choosenOption;
//
//        if (0 <= ordinal || ordinal < options.length) {
//          return options[ordinal];
//        } else {
//          notValid = String.valueOf(ordinal);
//        }
//        break;
//
//      case NOMINAL:
//      case PARAMETRIC:
//        Position nominal = (Position) choosenOption;
//        if (Arrays.asList(options).contains(nominal)) {
//          return nominal;
//        } else {
//          notValid = String.valueOf(nominal);
//        }
//        break;
//    }
//
//    throw new IllegalArgumentException(Message.INVALID_POSITION.getMessage(notValid));
//  }
//
//  private void extractAndUpdateCommandField(String commandString) {
//    parameterMatcher
//        .getCommandRegex()
//        .matcher(commandString)
//        .results()
//        .findFirst()
//        .ifPresent(m -> command = getPositionByName(m.group(1)));
//  }

  private static void warnAboutInput(String input, Exception e) {
    System.err.print(PROCESSING_FAILED.getMessage(input, e.getMessage()));
  }
}
