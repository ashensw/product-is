/*
*  Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.identity.integration.test.user.mgt;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.identity.integration.common.clients.ldap.LDAPClient;
import org.wso2.identity.integration.common.utils.ISIntegrationTest;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class CARBON16078LDAPDnCacheTestCase extends ISIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON16078LDAPDnCacheTestCase.class);
    private LoginLogoutClient loginLogoutClient;
    private UserManagementClient userManagementClient;
    private ServerConfigurationManager serverConfigurationManager;
    private AuthenticatorClient authenticatorClient;


    @BeforeClass(alwaysRun = true)
    public void testInit() throws Exception {



//        super.init(TestUserMode.SUPER_TENANT_ADMIN);
//        String pathToCarbonXML = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "IS" + File.separator +
//                                 "userMgt" + File.separator + "carbon15051" + File.separator + "carbon.xml";
//        String targetCarbonXML = CarbonUtils.getCarbonHome() + "repository" + File.separator + "conf" + File.separator + "carbon.xml";
//        serverConfigurationManager = new ServerConfigurationManager(isServer);
//        serverConfigurationManager.applyConfiguration(new File(pathToCarbonXML), new File(targetCarbonXML));
//
//
//        String pathToUserMgtXML = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "IS" +
//                File.separator +
//                "userMgt" + File.separator + "carbon15051" + File.separator + "user-mgt.xml";
//        String targetUserMgtXML = CarbonUtils.getCarbonHome() + "repository" + File.separator + "conf" + File.separator + "user-mgt.xml";
//        serverConfigurationManager.applyConfiguration(new File(pathToUserMgtXML), new File(targetUserMgtXML));

        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        authenticatorClient = new AuthenticatorClient(backendURL);
        loginLogoutClient = new LoginLogoutClient(isServer);

        userManagementClient = new UserManagementClient(backendURL, getSessionCookie());
        userManagementClient.addUser("testUser14", "passWord1", new String[]{"admin"}, null);
    }

    @AfterClass(alwaysRun = true)
    public void endTest() throws Exception {

        serverConfigurationManager.restoreToLastConfiguration();
    }

    @Test(groups = "wso2.is", description = "Trying to log in with email as the username")
    public void testLogin() throws Exception {
//        this.loginLogoutClient.login();
//        String backendURL = isServer.getContextUrls().getBackEndUrl();
//        login("testUser10", "passWord1", backendURL);
        String sessionCookie = authenticatorClient.login("testUser14", "passWord1", isServer.getInstance().getHosts().get("default"));
        Assert.assertTrue(sessionCookie.contains("JSESSIONID"), "Session Cookie not found. Login failed");
        authenticatorClient.logOut();

        // moveusr()

        LDAPClient client = new LDAPClient("localhost", 10799, "admin", "admin");
        LdapConnection connection = client.getConnection();
        
//        Entry entry = new DefaultEntry("uid=testaddUser7,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: inetOrgPerson", "ObjectClass: top", "ObjectClass: person", "cn: testaddUser_cn", "sn: testaddUser_sn", "userPassword: 12345");
//        client.addEntry(connection, entry);
        client.deleteEntry(connection, new Dn("uid=testUser14,ou=Users,dc=WSO2,dc=ORG"));


        Entry ouEntry = new DefaultEntry("ou=ouEntry,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: top", "ObjectClass: organizationalUnit");
        client.addEntry(connection, ouEntry);
        
        Entry entry = new DefaultEntry("uid=testUser14,ou=ouEntry,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: inetOrgPerson", "ObjectClass: top", "ObjectClass: person", "cn: cn", "sn: sn", "userPassword: passWord1");
        client.addEntry(connection, entry);


//        login("testUser10", "passWord1", backendURL);
//        this.loginLogoutClient.logout();
        String sessionCookie2 = authenticatorClient.login("testUser14", "passWord1", isServer.getInstance().getHosts().get("default"));
        Assert.assertTrue(sessionCookie2.contains("JSESSIONID"), "Session Cookie not found. Login failed");
        authenticatorClient.logOut();

        client.closeConnection(connection);
    }

    /**
     * Log in to a Carbon server
     *
     * @return The session cookie on successful login
     */
    private String login(String username, String password, String backendUrl) throws LoginAuthenticationExceptionException, IOException, XMLStreamException,
                                                                                     URISyntaxException, SAXException, XPathExpressionException {
        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendUrl);
        return authenticatorClient.login(username, password, new URL(backendUrl).getHost());
    }
}