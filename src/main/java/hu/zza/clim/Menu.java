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
import static hu.zza.clim.menu.Message.INVALID_POSITION;
import static hu.zza.clim.menu.Message.PROCESSING_FAILED;
import static hu.zza.clim.menu.Message.SHORT_LICENSE;

import hu.zza.clim.menu.MenuEntry.Leaf;
import hu.zza.clim.menu.MenuEntry.Node;
import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.NodePosition;
import hu.zza.clim.menu.Position;
import hu.zza.clim.menu.ProcessedInput;
import hu.zza.clim.menu.Util;
import hu.zza.clim.menu.component.in.InputProcessorService;
import hu.zza.clim.menu.component.in.ParametricInputProcessor;
import hu.zza.clim.menu.component.ui.HeaderService;
import hu.zza.clim.menu.component.ui.UserInterfaceService;
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
          "licence",
          "warranty",
          "liability",
          "about license",
          "about licence",
          "about warranty",
          "about liability",
          "show license",
          "show licence",
          "show warranty",
          "show liability");

  private final MenuStructure menuStructure;
  private final UserInterfaceService userInterfaceService;
  private final InputProcessorService inputProcessorService;
  private final Deque<NodePosition> positionHistory = new ArrayDeque<>();
  private NodePosition position;
  private Position[] options;

  Menu(MenuStructure menuStructure, ParameterMatcher parameterMatcher, ClimOption... climOptions) {

    this.menuStructure = menuStructure;
    Map<Class<? extends ClimOption>, ClimOption> optionsMap =
        ClimOption.getClimOptionMap(climOptions);

    UserInterface ui = (UserInterface) optionsMap.get(UserInterface.class);
    userInterfaceService = UserInterfaceService.of(ui);

    if (ui == UserInterface.PARAMETRIC) {
      Util.assertNonNull("parameterMatcher", parameterMatcher);
      inputProcessorService = new ParametricInputProcessor(parameterMatcher);
    } else {
      inputProcessorService = InputProcessorService.of(ui);
    }

    userInterfaceService.setHeaderService(
        HeaderService.of((HeaderStyle) optionsMap.get(HeaderStyle.class)));

    position = menuStructure.get(null).select(ProcessedInput.NULL);
    refreshOptions();
  }

  private void refreshOptions() {
    options = menuStructure.get(position).getLinks();
  }

  /** Prints the available options from the current position of the {@link Menu}. */
  public void listOptions() {
    refreshOptions();
    userInterfaceService.printHeaderForCurrentPositionAndHistory(
        position.getName(), getPositionHistoryAsStringArray());
    userInterfaceService.printOptionList(getDisplayableOptions());
    userInterfaceService.printFooter();
  }

  private String[] getPositionHistoryAsStringArray() {
    return positionHistory.stream().map(Position::getName).toArray(String[]::new);
  }

  private List<String> getDisplayableOptions() {
    return Arrays.stream(options).map(Position::getName).collect(Collectors.toList());
  }

  /**
   * Choose an option from the {@link Menu#listOptions available ones}. If it is a {@link Node
   * node}, {@link Menu} navigates itself to this position. If it is a {@link Leaf leaf}, {@link
   * Menu} call its function, and according to the result, it navigates itself toward.
   *
   * <p>The desired format of {@code consoleInput} depends on the {@link UserInterface} of the
   * {@link Menu} object:<br>
   * {@link UserInterface#NOMINAL} requires the exact name of the choosen option.<br>
   * {@link UserInterface#ORDINAL} and {@link UserInterface#ORDINAL_TRAILING_ZERO} require a numeric
   * string which is parsable with {@link Integer#parseInt(String)}.<br>
   * {@link UserInterface#PARAMETRIC} is more flexible (depends on the configuration of the {@link
   * ParameterMatcher}).
   *
   * @param consoleInput the choice of the user, its desired format (single integer, alphanumeric
   *     text, etc.) depends on {@link UserInterface}
   */
  public void chooseOption(String consoleInput) {
    if (consoleInput == null || consoleInput.isBlank()) {
      System.out.println();
    } else if (licenseCommands.contains(consoleInput.toLowerCase())) {
      printLicense();
    } else {
      positionHistory.offerFirst(position);
      tryToChooseAnOption(consoleInput);
    }
    refreshOptions();
  }

  /** Prints the full license information about clim. */
  public void printLicense() {
    System.out.println(GNU_GPL.getMessage());
  }

  private void tryToChooseAnOption(String input) {
    try {
      chooseOptionByProcessedInput(
          inputProcessorService.processInputRelatedToOptions(input, options));
    } catch (Exception e) {
      positionHistory.pollFirst();
      throw new ClimException(PROCESSING_FAILED.getMessage(input), e);
    }
  }

  private void chooseOptionByProcessedInput(ProcessedInput processedInput) {
    Position selected = returnValidatedPositionOrThrow(processedInput.getPosition());
    position = menuStructure.get(selected).select(processedInput);
  }


  private Position returnValidatedPositionOrThrow(Position position) {
    refreshOptions();
    if (Arrays.asList(options).contains(position)) {
      return position;
    }
    throw new IllegalArgumentException(INVALID_POSITION.getMessage(position.getName()));
  }

  /** Prints short license information about clim. */
  public void printShortLicense() {
    System.out.println(SHORT_LICENSE.getMessage());
  }
}
