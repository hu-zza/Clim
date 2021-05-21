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

package hu.zza.clim.menu.component.ui;

import static hu.zza.clim.menu.Message.MENU_OPTION_DECORATOR;
import static hu.zza.clim.menu.Message.MENU_OPTION_SPACER;

import hu.zza.clim.UserInterface;
import hu.zza.clim.menu.component.NotImplementedException;
import java.util.List;

public abstract class UserInterfaceService {
  private HeaderService headerService;

  public static UserInterfaceService of(UserInterface userInterface) {
    switch (userInterface) {
      case NOMINAL:
      case PARAMETRIC:
        return new StandardUserInterface();
      case ORDINAL:
        return new OrdinalUserInterface();
      case ORDINAL_TRAILING_ZERO:
        return new OrdinalTrailingZeroUserInterface();
      default:
        throw new NotImplementedException();
    }
  }

  public void setHeaderService(HeaderService headerService) {
    this.headerService = headerService;
  }

  public void printHeaderForCurrentPositionAndHistory(
      String currentPosition, String[] positionHistory) {
    headerService.printHeaderForCurrentPositionAndHistory(currentPosition, positionHistory);
  }

  // Dummy...
  public void printFooter() {
    System.out.println();
  }

  public void printOptionList(List<String> options) {
    options.forEach(
        e -> System.out.print(MENU_OPTION_SPACER.getMessage(MENU_OPTION_DECORATOR.getMessage(e))));
  }
}
