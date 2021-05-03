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

public class ClimPositionTest {
  public static void main(String[] args) {
    var node = NodeEnumTest.NODE;
    var leaf = LeafEnumTest.LEAF;

    for (var a : node.getClass().getAnnotations()) {
      System.out.println(a);
    }
    for (var a : leaf.getClass().getAnnotations()) {
      System.out.println(a);
    }    System.out.println();

    System.out.println(node);
    for (var e : node.getClass().getInterfaces()) {
      for (var a : e.getAnnotations()) {
        System.out.println(a);
      }
    }
    System.out.println();

    System.out.println(leaf);
    for (var e : leaf.getClass().getInterfaces()) {
      for (var a : e.getAnnotations()) {
          System.out.println(a);
      }
    }

  }
}
