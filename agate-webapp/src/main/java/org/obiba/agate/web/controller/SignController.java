/*
 * Copyright (c) 2020 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.obiba.agate.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignController {

  @GetMapping("/signin")
  public ModelAndView signin() {
    Subject subject = SecurityUtils.getSubject();
    if (subject.isAuthenticated())
      return new ModelAndView("redirect:/");

    ModelAndView mv = new ModelAndView("signin");
    return mv;
  }

}
