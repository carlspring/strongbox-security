package org.carlspring.strongbox.dao.rdbms;

import org.carlspring.strongbox.jaas.Role;
import org.carlspring.strongbox.jaas.User;
import org.carlspring.strongbox.jaas.authentication.UserResolver;

import java.sql.SQLException;
import java.util.Set;

/**
 * @author mtodorov
 */
public interface UsersDao extends BaseDBDao, UserResolver
{

    void createUser(User user)
            throws SQLException;

    void updateUser(User user)
            throws SQLException;

    void removeUser(User user)
            throws SQLException;

    void removeUserById(long userId)
            throws SQLException;

    void assignRole(User user, Role role)
            throws SQLException;

    void assignRole(User user, String roleName)
            throws SQLException;

    Set<Role> getRoles(User user)
            throws SQLException;

    void removeRole(User user, Role role)
            throws SQLException;

    public boolean hasRole(User user, String roleName)
            throws SQLException;

}
