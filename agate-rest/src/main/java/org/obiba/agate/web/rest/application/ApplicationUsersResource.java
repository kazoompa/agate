/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.web.rest.application;

import com.google.common.base.Strings;
import org.obiba.agate.domain.User;
import org.obiba.agate.service.UserService;
import org.obiba.agate.web.model.Dtos;
import org.obiba.shiro.realm.ObibaRealm;
import org.obiba.web.model.AuthDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/application/{id}/users")
public class ApplicationUsersResource extends ApplicationAwareResource {
  private static final Logger log = LoggerFactory.getLogger(ApplicationUsersResource.class);

  @Inject
  private Dtos dtos;

  @Inject
  protected UserService userService;

  @GET
  public List<AuthDtos.SubjectDto> get(@PathParam("id") String id,
                                       @HeaderParam(ObibaRealm.APPLICATION_AUTH_HEADER) String authHeader, @QueryParam("group") String group) {
    validateApplication(authHeader);
    if (!getApplicationName().equals(id)) throw new BadRequestException("Application can only query its own users");
    List<User> users = Strings.isNullOrEmpty(group)
      ? userService.findActiveUsersByApplication(getApplicationName())
      : userService.findActiveUsersByApplicationAndGroup(getApplicationName(), group);

    return users.stream().map(u -> dtos.asDto(u, true)).collect(Collectors.toList());
  }
}
