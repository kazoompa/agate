/*
 * Copyright (c) 2018 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.event;

import org.springframework.data.domain.Persistable;

public class PersistableDeletedEvent<TPersistable extends Persistable<?>> {

  private final TPersistable persistable;

  public PersistableDeletedEvent(TPersistable persistable) {this.persistable = persistable;}

  public TPersistable getPersistable() {
    return persistable;
  }
}
