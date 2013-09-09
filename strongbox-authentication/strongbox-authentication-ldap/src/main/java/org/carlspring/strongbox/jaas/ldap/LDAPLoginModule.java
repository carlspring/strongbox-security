package org.carlspring.strongbox.jaas.ldap;

import org.carlspring.strongbox.jaas.*;
import org.carlspring.strongbox.jaas.authentication.UserAuthenticator;
import org.carlspring.strongbox.jaas.authentication.UserResolver;
import org.carlspring.strongbox.jaas.principal.BasePrincipal;
import org.carlspring.strongbox.jaas.principal.RolePrincipal;
import org.carlspring.strongbox.jaas.principal.UserPrincipal;
import org.carlspring.strongbox.util.encryption.EncryptionUtils;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> This LoginModule authenticates users with a password against a database.
 * <p/>
 * <p> If user is successfully authenticated,
 * a <code>UserPrincipal</code> with the user's user name
 * is added to the Subject.
 * <p/>
 * <p> This LoginModule recognizes the debug option.
 * If set to true in the login Configuration,
 * debug messages will be output to the output stream, System.out.
 */
public class LDAPLoginModule
        implements LoginModule
{


    private static Logger logger = LoggerFactory.getLogger(LDAPLoginModule.class);

    // initial state
    private Subject subject;

    private CallbackHandler callbackHandler;

    private Map sharedState;

    private Map options;

    // configurable option
    // private boolean debug = false;

    // the authentication status
    private boolean succeeded = false;

    private boolean commitSucceeded = false;

    private BasePrincipal principal = new BasePrincipal();

    private Credentials credentials = new Credentials();

    private User user;

    // TODO: This should actually be injected somehow (perhaps by a IoC).
    private UserAuthenticator userAuthenticator = new UserAuthenticator();

    private UserResolver userResolver = new LDAPUserRealm();


    /**
     * Initialize this <code>LoginModule</code>.
     * <p/>
     * <p/>
     *
     * @param subject         the <code>Subject</code> to be authenticated. <p>
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *                        with the end user (prompting for user names and
     *                        passwords, for example). <p>
     * @param sharedState     shared <code>LoginModule</code> state. <p>
     * @param options         options specified in the login
     *                        <code>Configuration</code> for this particular
     *                        <code>LoginModule</code>.
     */
    @Override
    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<String, ?> sharedState,
                           Map<String, ?> options)
    {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // initialize any configured options
        // debug = "true".equalsIgnoreCase((String) options.get("debug"));

        logger.debug("LDAPLoginModule initialized!");
    }

    /**
     * Authenticate the user by prompting for a user name and password.
     * <p/>
     * <p/>
     *
     * @return true in all cases since this <code>LoginModule</code>
     * should not be ignored.
     * @throws FailedLoginException if the authentication fails. <p>
     * @throws LoginException       if this <code>LoginModule</code>
     *                              is unable to perform the authentication.
     */
    @Override
    public boolean login()
            throws LoginException
    {
        // prompt for a user name and password
        if (callbackHandler == null)
        {
            throw new LoginException("Error: No CallbackHandler available to garner" +
                                     " authentication information from the user!");
        }

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Username: ");
        callbacks[1] = new PasswordCallback("Password: ", false);

        try
        {
            callbackHandler.handle(callbacks);

            final NameCallback nameCallback = (NameCallback) callbacks[0];
            final PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];

            logger.debug("Handling login for " + nameCallback.getName() + "...");

            principal.setName(nameCallback.getName());
            credentials.setPassword(String.valueOf(passwordCallback.getPassword()));

            passwordCallback.clearPassword();
        }
        catch (IOException ioe)
        {
            throw new LoginException(ioe.toString());
        }
        catch (UnsupportedCallbackException uce)
        {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                                     " not available to garner authentication information " +
                                     "from the user");
        }

        checkUserCredentials(principal.getName(), EncryptionUtils.encryptWithMD5(credentials.getPassword()));

        if (user != null)
        {
            logger.debug("Authentication succeeded.");

            succeeded = true;
            return true;
        }
        else
        {
            // authentication failed -- clean out state
            logger.debug("Authentication failed.");

            succeeded = false;

            clearCredentials();

            throw new FailedLoginException("Incorrect username or password!");
        }
    }

    private void checkUserCredentials(String username, String encryptedPassword)
            throws LoginException
    {
        try
        {
            logger.debug("Checking authentication for: " + principal.getName() + " / " + encryptedPassword);

            user = userAuthenticator.authenticate(username, encryptedPassword, userResolver);
        }
        /*
        catch (SQLException e)
        {
            throw new LoginException("Failed to authenticate against the database with error message: " +
                                     e.getMessage());
        }
        */
        catch (Exception e)
        {
            throw new LoginException(e.getMessage());
        }
    }

    /**
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     * <p/>
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates a
     * <code>SamplePrincipal</code>
     * with the <code>Subject</code> located in the
     * <code>LoginModule</code>.  If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     * <p/>
     * <p/>
     *
     * @return true if this LoginModule's own login and commit
     * attempts succeeded, or false otherwise.
     * @throws LoginException if the commit fails.
     */
    @Override
    public boolean commit()
            throws LoginException
    {
        if (!succeeded)
        {
            logger.debug("Committing failure!");

            return false;
        }
        else
        {
            logger.debug("Committing success!");

            addPrincipals();

            // in any case, clean out state
            clearCredentials();

            commitSucceeded = true;
            return true;
        }
    }

    private void addPrincipals()
    {
        // Add a Principal (authenticated identity) to the Subject
        // Add a principal with the username
        principal = new UserPrincipal(principal.getName());
        if (!subject.getPrincipals().contains(principal))
        {
            subject.getPrincipals().add(principal);

            if (logger.isDebugEnabled())
            {
                logger.debug("Added UserPrincipal [" + principal.toString() + "] to subject.");
            }
        }

        // Add all the roles as principals:
        for (Role role : user.getRoles())
        {
            Principal rolePrincipal = new RolePrincipal(role.getName());

            subject.getPrincipals().add(rolePrincipal);

            if (logger.isDebugEnabled())
            {
                logger.debug("Added RolePrincipal [" + rolePrincipal.toString() + "] to subject.");
            }

        }
    }

    /*
    @Override
    public UserInfo getUserInfo(String username)
            throws Exception
    {
        return null;
    }
    */

    /**
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     * <p/>
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> and <code>commit</code> methods),
     * then this method cleans up any state that was originally saved.
     * <p/>
     * <p/>
     *
     * @return false if this LoginModule's own login and/or commit attempts
     * failed, and true otherwise.
     * @throws LoginException if the abort fails.
     */
    @Override
    public boolean abort()
            throws LoginException
    {
        logger.debug("Aborting!");

        if (!succeeded)
        {
            return false;
        }
        else if (succeeded && !commitSucceeded)
        {
            // login succeeded but overall authentication failed
            succeeded = false;

            clearCredentials();

            principal = null;
        }
        else
        {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    private void clearCredentials()
    {
        try
        {
            credentials.destroy();
        }
        catch (DestroyFailedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Logout the user.
     * <p/>
     * <p> This method removes the <code>UserPrincipal</code>
     * that was added by the <code>commit</code> method.
     * <p/>
     * <p/>
     *
     * @return true in all cases since this <code>LoginModule</code>
     * should not be ignored.
     * @throws LoginException if the logout fails.
     */
    @Override
    public boolean logout()
            throws LoginException
    {
        logger.debug("Logging out!");

        subject.getPrincipals().remove(principal);
        succeeded = false;
        succeeded = commitSucceeded;

        clearCredentials();

        principal = null;
        return true;
    }

}
