package org.obiba.agate.domain;


import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Map;

public interface GitPersistable extends Serializable, Persistable<String>, GitIdentifier {

    Map<String, Serializable> parts();
}
