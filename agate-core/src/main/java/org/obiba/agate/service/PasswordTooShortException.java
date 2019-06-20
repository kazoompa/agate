/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.service;

public class PasswordTooShortException extends RuntimeException {
  private final int minSize;

  public PasswordTooShortException(int minSize) {
    super(String.format("Password is shorter than the required %d characters", minSize));
    this.minSize = minSize;
  }

  public int getMinSize() {
    return minSize;
  }
}



