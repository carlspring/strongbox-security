package org.carlspring.strongbox.jaas;

import org.carlspring.strongbox.jaas.authentication.AuthenticationCallbackHandler;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.junit.Test;
import static junit.framework.Assert.fail;

/**
 * @author mtodorov
 */
public class LDAPLoginModuleACNTest
{

    private static final String ADMIN_USERNAME_VALID = "admin";

    private static final String ADMIN_PASSWORD_VALID = "secret";

    private static final String USER_USERNAME_VALID = "carlspring";

    private static final String USER_PASSWORD_VALID = "password";

    private static final String USERNAME_INVALID = "admin1";

    private static final String PASSWORD_INVALID = "password123";


    @Test
    public void testValidLogin()
            throws LoginException
    {
        System.out.println("Testing login with valid credentials...");

        LoginContext lc = new LoginContext("strongbox",
                                           new AuthenticationCallbackHandler(USER_USERNAME_VALID, USER_PASSWORD_VALID));

        // attempt authentication
        lc.login();

        // if we return with no exception, authentication succeeded
    }

//    @Test
//    public void testCaching()
//            throws LoginException
//    {
//        /*
//        System.out.println("Testing login with cached credentials...");
//
//        LoginContext lc = new LoginContext("strongbox", new AuthenticationCallbackHandler(USERNAME_VALID, PASSWORD_VALID));
//        lc.login();
//
//        lc = new LoginContext("strongbox", new AuthenticationCallbackHandler("testuser", PASSWORD_VALID));
//        lc.login();
//
//        // Try again
//        lc = new LoginContext("strongbox", new AuthenticationCallbackHandler(USERNAME_VALID, PASSWORD_VALID));
//        lc.login();
//
//        lc = new LoginContext("strongbox", new AuthenticationCallbackHandler("testuser", PASSWORD_VALID));
//        lc.login();
//
//        // if we return with no exception, authentication succeeded
//        */
//    }
//
//    @Test
//    public void testInvalidLogin()
//            throws LoginException
//    {
//        /*
//        System.out.println("Testing login with invalid credentials...");
//
//        LoginContext lc = new LoginContext("strongbox", new AuthenticationCallbackHandler(USERNAME_INVALID, PASSWORD_INVALID));
//
//        // attempt authentication
//        try
//        {
//            lc.login();
//
//            // if we return with no exception, authentication succeeded
//
//            fail("The test should have failed when using invalid credentials!");
//        }
//        catch (LoginException e)
//        {
//            // This is expected to happen.
//        }
//        */
//    }

}


