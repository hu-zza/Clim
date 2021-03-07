/*
 *     clim   // Command Line Interface Menu
 *
 *     Copyright (C) 2020-2021 Szabó László András <hu@zza.hu>
 *
 *     This file is part of clim.
 *
 *     clim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     clim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.zza.clim;

import java.util.HashMap;


public final class MenuStructure {
    private final HashMap<Position, MenuEntry> menu = new HashMap<>();
    private       boolean                      finalized;
    
    
    boolean isEmpty() {
        return menu.isEmpty();
    }
    
    
    public void setFinalized() {
        finalized = true;
    }
    
    
    public MenuEntry put(MenuEntry menuEntry) {
        return finalized ? null : menu.put(menuEntry.getPosition(), menuEntry);
    }
    
    
    boolean containsKey(Position position) {
        return menu.containsKey(position);
    }
    
    
    MenuEntry get(Position position) {
        return menu.get(position);
    }
}
