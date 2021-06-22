/**
 * clim is a handy Java module for building various menus (ordinal, nominal, parametric).
 *
 * @since 0.1
 */

module hu.zza.clim {
  requires transitive com.google.gson;
  exports hu.zza.clim;
  exports hu.zza.clim.parameter;
}