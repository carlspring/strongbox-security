package org.carlspring.strongbox.dao.rdbms.impl;

import org.carlspring.strongbox.dao.rdbms.RolesDao;
import org.carlspring.strongbox.jaas.Role;

import java.sql.SQLException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author mtodorov
 */
public class RolesDaoImplTest
{

    public static final String ROLE_NAME = "Read";


    @Test
    public void testCreateRole()
            throws Exception
    {
        Role role = new Role();
        role.setName(ROLE_NAME);
        role.setDescription("An observation role");

        RolesDao rolesDao = new RolesDaoImpl();

        final long countOld = rolesDao.count();

        rolesDao.createRole(role);

        final long countNew = rolesDao.count();

        assertTrue("Failed to create role '" + role.getName() + "'!", countOld < countNew);

        Role createdRole = rolesDao.findRole(ROLE_NAME);
        System.out.println("roleid: " + createdRole.getRoleId());
    }

    @Test
    public void testGetRole()
            throws SQLException
    {
        RolesDao rolesDao = new RolesDaoImpl();
        final Role role = rolesDao.findRole(ROLE_NAME);

        assertNotNull("Failed to lookup role!", role);
        assertTrue("Failed to lookup role!", role.getRoleId() > 0);
    }

    @Test
    public void testUpdateRole()
            throws SQLException
    {
        RolesDao rolesDao = new RolesDaoImpl();

        final Role role = rolesDao.findRole(ROLE_NAME);
        final String description = "Permission to read objects";

        role.setDescription(description);

        rolesDao.updateRole(role);

        final Role updatedRole = rolesDao.findRole(ROLE_NAME);

        assertEquals("Failed to update the role!", description, updatedRole.getDescription());
    }

    @Test
    public void testDeleteRole()
            throws SQLException
    {
        RolesDao rolesDao = new RolesDaoImpl();
        final Role role = rolesDao.findRole(ROLE_NAME);
        rolesDao.removeRoleById(role.getRoleId());
    }

    @Test
    public void testCount()
            throws Exception
    {
        RolesDao rolesDao = new RolesDaoImpl();
        final long count = rolesDao.count();

        assertTrue("Failed to get the number of available roles!", count > 0);

        System.out.println("Number of roles in database: " + count);
    }

}