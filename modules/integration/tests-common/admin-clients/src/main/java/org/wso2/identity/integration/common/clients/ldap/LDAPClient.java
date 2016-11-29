package org.wso2.identity.integration.common.clients.ldap;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.DeleteRequest;
import org.apache.directory.api.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.api.ldap.model.message.DeleteResponse;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.IOException;
import java.util.Iterator;


public class LDAPClient {
    private final String host;
    private final int port;
    private final String userName;
    private final String password;

    public LDAPClient(String host, int port, String userName, String password) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public void addEntry(LdapConnection connection, Entry entry) throws IOException, LdapException {

        if (entry == null) {
            String msg="Cannot add an empty entry";
//            log
            throw new IllegalArgumentException(msg);
        }

//        Entry entry = new DefaultEntry("cn=testadd, ou=system", "ObjectClass: top", "ObjectClass: person", "cn: testadd_cn", "sn: testadd_sn");
        AddRequest addRequest = new AddRequestImpl();
        addRequest.setEntry(entry);
        addRequest.setEntryDn(entry.getDn());

        AddResponse response = connection.add(addRequest);
    }

    public void deleteEntry(LdapConnection connection, Dn dn) throws IOException, LdapException{

//        session.exists(dn);
//        assertTrue( session.exists( "cn=child1,cn=parent,ou=system" ) );
        DeleteRequest deleteRequest = new DeleteRequestImpl();
//        Dn dn = new Dn("cn=child1,cn=parent,ou=system");
        deleteRequest.setName(dn);
        DeleteResponse response = connection.delete(deleteRequest);

    }

    public LdapConnection getConnection() throws LdapException, IOException {
        LdapConnection connection = new LdapNetworkConnection(host, port);
        connection.bind("uid=" + userName + ",ou=system", password);
        return connection;
    }

    public void closeConnection(LdapConnection connection) throws LdapException, IOException {
        connection.unBind();
        connection.close();
    }

    public Iterator search(LdapConnection connection, String baseDn, String attributeName, String attributeValue) throws LdapException {
        String filter = "(" + attributeName + "=" + attributeValue + ")";
        SearchScope searchScope = SearchScope.SUBTREE;
        EntryCursor cursor = connection.search(baseDn, filter, searchScope);
        return cursor.iterator();
    }

    public static void main(String[] args) throws Exception {
//        if (args.length == 0) {
//            throw new IllegalArgumentException("Specify the title of the book to be searched.");
//        }
        LDAPClient client = new LDAPClient("localhost", 10389, "admin", "admin");
        LdapConnection connection = client.getConnection();

//        ou=Users,dc=wso2,dc=org
//        Entry ouEntry = new DefaultEntry("ou=ouEntry,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: top", "ObjectClass: organizationalUnit");
//        client.addEntry(connection, ouEntry);
//        Entry entry = new DefaultEntry("uid=testaddUser3,ou=ouEntry,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: inetOrgPerson", "ObjectClass: top", "ObjectClass: person", "cn: testaddUser_cn", "sn: testaddUser_sn", "userPassword: 12345");
//        client.addEntry(connection, entry);


//        client.deleteEntry(connection, new Dn("uid=testaddUser3,ou=ouEntry,ou=Users,dc=WSO2,dc=ORG"));

//        Entry entry3 = new DefaultEntry("cn=testaddUser2, ou=ouEntry, ou=users, ou=system", "ObjectClass: top", "ObjectClass: person", "cn: testaddUser_cn", "sn: testaddUser_sn", "userPassword: 12345");
//        client.addEntry(connection, entry3);

//        Iterator searchResults = client.search(connection, "dc=example,dc=com", "subject", args[0]);
//        if (searchResults.hasNext()) {
//            printBookDetails((Entry) searchResults.next());
//        } else {
//            System.out.println("No book found with title: " + args[0]);
//        }

//        Entry entry = new DefaultEntry("uid=testaddUser3,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: inetOrgPerson", "ObjectClass: top", "ObjectClass: person", "cn: testaddUser_cn", "sn: testaddUser_sn", "userPassword: 12345");
//        client.addEntry(connection, entry);
//        client.closeConnection(connection);




//        Entry entry = new DefaultEntry("uid=testaddUser7,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: inetOrgPerson", "ObjectClass: top", "ObjectClass: person", "cn: testaddUser_cn", "sn: testaddUser_sn", "userPassword: 12345");
//        client.addEntry(connection, entry);
        client.deleteEntry(connection, new Dn("uid=testaddUser7,ou=Users,dc=WSO2,dc=ORG"));


        Entry ouEntry = new DefaultEntry("ou=ouEntry,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: top", "ObjectClass: organizationalUnit");
        client.addEntry(connection, ouEntry);
        Entry entry = new DefaultEntry("uid=testaddUser7,ou=ouEntry,ou=Users,dc=WSO2,dc=ORG", "ObjectClass: inetOrgPerson", "ObjectClass: top", "ObjectClass: person", "cn: testaddUser_cn", "sn: testaddUser_sn", "userPassword: 12345");
        client.addEntry(connection, entry);
//        client.closeConnection(connection);
    }

    private static void printBookDetails(Entry book) {
        System.out.println("Title: " + book.get("subject").get().toString());
        System.out.println("Author: " + book.get("author").get().toString());
        Attribute synopsis = book.get("synopsis");
        if (synopsis != null) {
            System.out.println("Synopsis: " + synopsis.get().toString());
        }
    }
}
