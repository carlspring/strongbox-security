package org.carlspring.strongbox.dao.jdbc.impl;

import org.carlspring.strongbox.dao.jdbc.RolesDao;
import org.carlspring.strongbox.security.jaas.Role;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * @author mtodorov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/strongbox-*-context.xml", "classpath*:/META-INF/spring/strongbox-*-context.xml"})
public class RolesDaoImplTest
{

    public static final String ROLE_NAME = "Read";

    @Autowired
    private RolesDao rolesDaoJdbc;


    @Test
    public void testCreateRole()
            throws Exception
    {
        Role role = new Role();
        role.setName(ROLE_NAME);
        role.setDescription("An observation role");

        final long countOld = rolesDaoJdbc.count();

        rolesDaoJdbc.createRole(role);

        final long countNew = rolesDaoJdbc.count();

        assertTrue("Failed to create role '" + role.getName() + "'!", countOld < countNew);

        Role createdRole = rolesDaoJdbc.findRole(ROLE_NAME);

        assertNotNull("Failed to lookup role!", createdRole);
        assertNotNull("Failed to lookup role!", createdRole.getName());

        System.out.println("role_name: " + createdRole.getName());

        // Update
        role = rolesDaoJdbc.findRole(ROLE_NAME);
        final String description = "Permission to read objects";

        role.setDescription(description);

        rolesDaoJdbc.updateRole(role);

        final Role updatedRole = rolesDaoJdbc.findRole(ROLE_NAME);

        assertEquals("Failed to update the role!", description, updatedRole.getDescription());

        // Count
        final long count = rolesDaoJdbc.count();

        assertTrue("Failed to get the number of available roles!", count > 0);

        System.out.println("Number of roles in database: " + count);

        // Delete
        rolesDaoJdbc.removeRole(role.getName());

        // TODO: Re-enable this at some point
        // assertEquals("Failed to delete the role!", 6, rolesDao.count());
    }

}
