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

import hu.zza.clim.parameter.ParameterMatcher;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class MenuBuilder {

  private final Map<String, JSONObject> declarations = new HashMap<>();
  private JSONObject menuStructure;
  private ControlType controlType;
  private ParameterMatcher parameterMatcher;
  private String initialPosition;

  public Menu build() {
    //return Menu.of(menuStructure, controlType, parameterMatcher, initialPosition);
    return null;
  }

  public  MenuBuilder clear() {
    return this;
  }

  public MenuBuilder setStructure(JSONObject menuStructure) {
    return this;
  }

  public MenuBuilder setDeclaration(JSONObject... declarations) {
    return this;
  }

  public MenuBuilder setControlType(ControlType controlType) {
    return this;
  }

  public MenuBuilder setParameterMatcher(ParameterMatcher parameterMatcher) {
    return this;
  }

  public MenuBuilder setInitialPosition(String initialPosition) {
    return this;
  }
}
