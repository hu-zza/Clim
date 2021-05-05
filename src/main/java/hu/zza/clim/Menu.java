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
import static hu.zza.clim.menu.Message.INITIALIZATION_FAILED;
import static hu.zza.clim.menu.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.menu.Message.INVALID_NONNULL_ARGUMENT;
import static hu.zza.clim.menu.Message.MISSING_MENU_ENTRY;
import static hu.zza.clim.menu.Message.PROCESSING_FAILED;
import static hu.zza.clim.menu.Message.UNKNOWN_COMMAND;

import hu.zza.clim.menu.LeafPosition;
import hu.zza.clim.menu.MenuEntry;
import hu.zza.clim.menu.MenuEntry.Leaf;
import hu.zza.clim.menu.MenuEntry.Node;
import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.Message;
import hu.zza.clim.menu.NodePosition;
import hu.zza.clim.menu.Position;
import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterMatcher;
import hu.zza.clim.parameter.ParameterName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private final ControlType controlType;
  private final Map<String, NodePosition> nodeNameMap;
  private final Map<String, LeafPosition> leafNameMap;
  private final ParameterMatcher parameterMatcher;
  private NodePosition position;
  private Position command;
  private Position[] options;

  private Menu(
      MenuStructure menuStructure,
      ControlType controlType,
      Class<? extends NodePosition> nodeEnum,
      Class<? extends LeafPosition> leafEnum,
      NodePosition initialPosition,
      ParameterMatcher parameterMatcher) {

    if (menuStructure == null || menuStructure.isEmpty()) {
      throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("menuStructure"));
    }

    if (controlType == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("controlType"));
    }

    if (nodeEnum == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("nodeEnum"));
    }

    if (leafEnum == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("leafEnum"));
    }

    if (initialPosition == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("initialPosition"));
    }

    if (controlType == ControlType.PARAMETRIC && parameterMatcher == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("parameterMatcher"));
    }

    this.menuStructure = menuStructure;
    this.controlType = controlType;
    this.position = initialPosition;
    this.parameterMatcher = parameterMatcher;

    Map<String, NodePosition> tmpNodeMap = new HashMap<>();
    for (var node : nodeEnum.getEnumConstants()) {
      if (!menuStructure.containsKey(node)) {
        throw new IllegalStateException(
            MISSING_MENU_ENTRY.getMessage("MenuStructure", node.name()));
      }

      tmpNodeMap.put(node.name(), node);
    }
    this.nodeNameMap = Map.copyOf(tmpNodeMap);

    Map<String, LeafPosition> tmpLeafMap = new HashMap<>();
    for (var leaf : leafEnum.getEnumConstants()) {
      if (!menuStructure.containsKey(leaf)) {
        throw new IllegalStateException(
            MISSING_MENU_ENTRY.getMessage("MenuStructure", leaf.name()));
      }

      if (!parameterMatcher.containsKeyInPatternMap(leaf)) {
        throw new IllegalArgumentException(
            MISSING_MENU_ENTRY.getMessage("PatternMap", leaf.name()));
      }

      tmpLeafMap.put(leaf.name(), leaf);
    }
    this.leafNameMap = Map.copyOf(tmpLeafMap);

    refreshOptions();
  }

  private void refreshOptions() {
    options = menuStructure.get(position).getLinks();
  }

  /**
   * Creates a simple, {@link ControlType#ORDINAL ordinal} menu and returns with it. The initial
   * position is the first object from the {@code nodeEnum}.
   *
   * @param menuStructure {@link MenuStructure} object which represents the connection between
   *     {@link NodePosition nodes} and {@link LeafPosition leaves}
   * @param nodeEnum user defined node enum which implements {@link NodePosition} marker interface
   * @param leafEnum user defined leaf enum which implements {@link LeafPosition} marker interface
   * @return the {@link ControlType#ORDINAL ordinal} {@link Menu} object.
   */
  public static Menu of(
      MenuStructure menuStructure,
      Class<? extends NodePosition> nodeEnum,
      Class<? extends LeafPosition> leafEnum) {
    return of(menuStructure, ControlType.ORDINAL, nodeEnum, leafEnum);
  }

  /**
   * Creates a menu with the given parameters and returns with it. The initial position is the first
   * object from the {@code nodeEnum}. This factory method do not set the {@link ParameterMatcher}
   * of the {@link Menu}, and throws an exception if {@link ControlType} needs a {@link
   * ParameterMatcher} set.
   *
   * @param menuStructure {@link MenuStructure} object which represents the connection between
   *     {@link NodePosition nodes} and {@link LeafPosition leaves}
   * @param controlType sets the main behavior of the menu
   * @param nodeEnum user defined node enum which implements {@link NodePosition} marker interface
   * @param leafEnum user defined leaf enum which implements {@link LeafPosition} marker interface
   * @return the {@link Menu} object.
   */
  public static Menu of(
      MenuStructure menuStructure,
      ControlType controlType,
      Class<? extends NodePosition> nodeEnum,
      Class<? extends LeafPosition> leafEnum) {
    return of(menuStructure, controlType, nodeEnum, leafEnum, null);
  }

  /**
   * Creates a menu with the given parameters and returns with it. The initial position is the first
   * object from the {@code nodeEnum}.
   *
   * @param menuStructure {@link MenuStructure} object which represents the connection between
   *     {@link NodePosition nodes} and {@link LeafPosition leaves}
   * @param controlType sets the main behavior of the menu
   * @param nodeEnum user defined node enum which implements {@link NodePosition} marker interface
   * @param leafEnum user defined leaf enum which implements {@link LeafPosition} marker interface
   * @param parameterMatcher sets the parameter matching strategy, patterns, etc. with a {@link
   *     ParameterMatcher}
   * @return the {@link Menu} object.
   */
  public static Menu of(
      MenuStructure menuStructure,
      ControlType controlType,
      Class<? extends NodePosition> nodeEnum,
      Class<? extends LeafPosition> leafEnum,
      ParameterMatcher parameterMatcher) {
    return of(menuStructure, controlType, nodeEnum, leafEnum, null, parameterMatcher);
  }

  /**
   * Creates a menu with the given parameters and returns with it.
   *
   * @param menuStructure {@link MenuStructure} object which represents the connection between
   *     {@link NodePosition nodes} and {@link LeafPosition leaves}
   * @param controlType sets the main behavior of the menu
   * @param nodeEnum user defined node enum which implements {@link NodePosition} marker interface
   * @param leafEnum user defined leaf enum which implements {@link LeafPosition} marker interface
   * @param initialPosition starting point of the menu (choosen from the {@code nodeEnum})
   * @param parameterMatcher sets the parameter matching strategy, patterns, etc. with a {@link
   *     ParameterMatcher}
   * @return the {@link Menu} object.
   */
  public static Menu of(
      MenuStructure menuStructure,
      ControlType controlType,
      Class<? extends NodePosition> nodeEnum,
      Class<? extends LeafPosition> leafEnum,
      NodePosition initialPosition,
      ParameterMatcher parameterMatcher) {
    try {
      if (initialPosition == null) {
        initialPosition = nodeEnum.getEnumConstants()[0];
      }
      return new Menu(
          menuStructure, controlType, nodeEnum, leafEnum, initialPosition, parameterMatcher);
    } catch (Exception e) {
      throw new IllegalArgumentException(INITIALIZATION_FAILED.getMessage(e.getMessage()));
    }
  }

  /**
   * Prints the available options (with license footer) from the current position of the {@link
   * Menu}.
   */
  public void listOptions() {
    listOptions(true);
  }

  /**
   * Prints the available options from the current position of the {@link Menu}.
   *
   * @param showLicense switch license footer on or off for the current printing
   */
  public void listOptions(boolean showLicense) {
    refreshOptions();
    if (options.length != 0) {
      switch (controlType) {
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
    }

    if (showLicense) {
      System.out.println(
          "\n\n"
              + "    clim   // Command Line Interface Menu\n\n"
              + "    Copyright (C) 2020-2021 Szabó László András <hu@zza.hu>\n\n"
              + "    This program comes with ABSOLUTELY NO WARRANTY; for details type \"license\".\n"
              + "    This is free software, and you are welcome to redistribute it\n"
              + "    under certain conditions; type \"license\" for details.\n");
    }
  }

  private void printOrdinalMenu() {
    boolean trailingZero = controlType == ControlType.ORDINAL_TRAILING_ZERO;

    int i = trailingZero ? 1 : 0;

    for (; i < options.length; i++) {
      printMenuEntry(menuStructure.get(options[i]), i);
    }

    if (trailingZero) {
      printMenuEntry(menuStructure.get(options[0]), 0);
    }
  }

  private void printMenuEntry(MenuEntry menuEntry, Integer ordinal) {
    if (menuEntry != null) {
      if (ordinal != null) {
        System.out.printf("%d. %s%n", ordinal, menuEntry.getName());
      } else {
        System.out.printf("%s%n", menuEntry.getName());
      }
    }
  }

  /**
   * Choose an option from the {@link Menu#listOptions available ones}. If it is a {@link Node
   * node}, {@link Menu} navigates itself to this position. If it is a {@link Leaf leaf}, {@link
   * Menu} call its function, and according to the result, it navigates itself toward.
   *
   * <p>The desired format of {@code input} depends on the {@link ControlType} of the {@link Menu}
   * object:<br>
   * {@link ControlType#NOMINAL} requires the exact name of the choosen option.<br>
   * {@link ControlType#ORDINAL} and {@link ControlType#ORDINAL_TRAILING_ZERO} require a numeric
   * string which is parsable with {@link Integer#parseInt(String)}.<br>
   * {@link ControlType#PARAMETRIC} is more flexible (depends on the configuration of the {@link
   * ParameterMatcher}).
   *
   * @param input the choice of the user, its desired format (single integer, alphanumeric text,
   *     etc.) depends on {@link ControlType}
   */
  public void chooseOption(String input) {
    if (input == null || input.isBlank()) {
      return;
    }

    if (licenseCommands.contains(input.toLowerCase())) {
      System.out.println(GNU_GPL.getMessage());
    } else {
      refreshOptions();
      try {
        switch (controlType) {
          case NOMINAL:
            Position nominal = getPositionByName(input);
            setMenuPosition(getValidatedPositionOrThrow(nominal), Map.of());
            break;

          case ORDINAL:
          case ORDINAL_TRAILING_ZERO:
            int ordinal = Integer.parseInt(input);
            setMenuPosition(getValidatedPositionOrThrow(ordinal), Map.of());
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
        refreshOptions();
      } catch (Exception e) {
        warnAboutInput(input, e);
      }
    }
  }

  private Position getPositionByName(String name) {
    String upperCaseName = name.toUpperCase();

    if (nodeNameMap.containsKey(upperCaseName)) {
      return nodeNameMap.get(upperCaseName);
    } else if (leafNameMap.containsKey(upperCaseName)) {
      return leafNameMap.get(upperCaseName);
    } else {
      throw new IllegalArgumentException(UNKNOWN_COMMAND.getMessage(name));
    }
  }

  private void setMenuPosition(Position key, Map<ParameterName, Parameter> parameterMap) {
    position = menuStructure.get(key).select(parameterMap);
  }

  private Position getValidatedPositionOrThrow(Object choosenOption) {
    String notValid = "";

    switch (controlType) {
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
