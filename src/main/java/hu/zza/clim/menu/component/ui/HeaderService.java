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

import static hu.zza.clim.menu.Message.MENU_POSITION_DECORATOR;
import static hu.zza.clim.menu.Message.MENU_POSITION_SPACER;

import hu.zza.clim.HeaderStyle;
import hu.zza.clim.menu.component.NotImplementedException;

public interface HeaderService {
  static HeaderService of(HeaderStyle headerStyle) {
    switch (headerStyle) {
      case HIDDEN:
        return new HiddenHeader();
      case STANDARD:
        return new StandardHeader();
      case HISTORY:
        return new HistoryHeader();
      default:
        throw new NotImplementedException();
    }
  }

  default void printHeaderForCurrentPositionAndHistory(
      String currentPosition, String[] positionHistory) {
    System.out.print(
        MENU_POSITION_SPACER.getMessage(MENU_POSITION_DECORATOR.getMessage(currentPosition)));
  }
}
