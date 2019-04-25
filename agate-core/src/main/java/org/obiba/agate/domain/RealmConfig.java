package org.obiba.agate.domain;

import com.google.common.collect.Sets;
import org.obiba.mongodb.domain.AbstractAuditableDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Document
public class RealmConfig extends AbstractAuditableDocument {

  @NotNull
  @Indexed(unique = true)
  private String name;

  private String title;

  private String description;

  private RealmStatus status = RealmStatus.INACTIVE;

  private Set<String> groups = Sets.newHashSet();

  private AgateRealm type = AgateRealm.AGATE_USER_REALM;

  private boolean defaultRealm = false;

  private boolean forSignup = false;

  // security concerns: might need to be a hashed string? password field would need to be hashed?
  // might have to use configurationService.encrypt
  private String content;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNameAsId() {
    setId(getName().replaceAll("\\s+", "+").toLowerCase());
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RealmStatus getStatus() {
    return status;
  }

  public void setStatus(RealmStatus status) {
    this.status = status;
  }

  public Set<String> getGroups() {
    return Collections.unmodifiableSet(groups);
  }

  public void setGroups(Collection<String> groups) {
    setGroups(Sets.newHashSet(groups));
  }

  public void setGroups(Set<String> groups) {
    this.groups = groups == null ? Sets.newHashSet() : groups;
  }

  public AgateRealm getType() {
    return type;
  }

  public void setType(AgateRealm type) {
    this.type = type;
  }

  public boolean isDefaultRealm() {
    return defaultRealm;
  }

  public void setDefaultRealm(boolean defaultRealm) {
    this.defaultRealm = defaultRealm;
  }

  public boolean isForSignup() {
    return forSignup;
  }

  public void setForSignup(boolean forSignup) {
    this.forSignup = forSignup;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private RealmConfig config;

    private Builder() {
      config = new RealmConfig();
    }

    public Builder id(String value) {
      config.setId(value);
      return this;
    }

    public Builder name(String value) {
      config.setName(value);
      return this;
    }

    public Builder title(String value) {
      config.setTitle(value);
      return this;
    }

    public Builder description(String value) {
      config.setDescription(value);
      return this;
    }

    public Builder type(AgateRealm value) {
      config.setType(value);
      return this;
    }

    public Builder defaultRealm(boolean value) {
      config.setDefaultRealm(value);
      return this;
    }

    public Builder forSignup(boolean value) {
      config.setForSignup(value);
      return this;
    }

    public Builder status(RealmStatus value) {
      config.setStatus(value);
      return this;
    }

    public Builder groups(Collection<String> groups) {
      config.setGroups(groups);
      return this;
    }

    public Builder content(String value) {
      config.setContent(value);
      return this;
    }

    public RealmConfig build() {
      return config;
    }
  }
}
