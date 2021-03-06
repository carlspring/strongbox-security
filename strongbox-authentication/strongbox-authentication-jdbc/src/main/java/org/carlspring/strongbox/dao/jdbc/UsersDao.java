package org.carlspring.strongbox.dao.jdbc;

import org.carlspring.strongbox.security.jaas.Role;
import org.carlspring.strongbox.security.jaas.User;
import org.carlspring.strongbox.security.jaas.authentication.UserResolutionException;
import org.carlspring.strongbox.security.jaas.authentication.UserResolver;
import org.carlspring.strongbox.security.jaas.authentication.UserStorage;

import java.util.Set;

/**
 * @author mtodorov
 */
public interface UsersDao extends BaseDBDao, UserResolver, UserStorage
{

    Set<Role> getRoles(User user)
            throws UserResolutionException;

    boolean hasRole(String username, String roleName)
            throws UserResolutionException;

}
