/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.web.rest.security;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

@Priority(Integer.MIN_VALUE)
public class AuthenticationInterceptor implements ContainerResponseFilter {

  private static final String AGATE_SESSION_ID_COOKIE_NAME = "agatesid";

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {
    // Set the cookie if the user is still authenticated
    if(isUserAuthenticated()) {
      Session session = SecurityUtils.getSubject().getSession();
      session.touch();
      int timeout = (int) (session.getTimeout() / 1000);
      responseContext.getHeaders().add(HttpHeaders.SET_COOKIE,
          new NewCookie(AGATE_SESSION_ID_COOKIE_NAME, session.getId().toString(), "/", null, null, timeout, false));
    } else {
      if(responseContext.getHeaders().get(HttpHeaders.SET_COOKIE) == null) {
        responseContext.getHeaders().putSingle(HttpHeaders.SET_COOKIE,
            new NewCookie(AGATE_SESSION_ID_COOKIE_NAME, null, "/", null, "Agate session deleted", 0, false));
      }
    }
  }

  private boolean isUserAuthenticated() {
    return SecurityUtils.getSubject().isAuthenticated();
  }

}
