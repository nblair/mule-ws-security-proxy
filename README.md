# mule-ws-security-proxy #

This project is an example of how create a Mule application to proxy a remote SOAP Web Service that requires Encryption and Signing via WS-Security.
The endpoint exposed on the internal network has none of the WS-Security requirements. Mule acts in essence as a SSL/WS-Security terminator and performs
all required Encryption and Signing on the outbound request to the remote service and all required Decryption and Signature Validation on
the response from the remote service. The client on your internal network need not 

WARNING: since the endpoint on the internal network removes the security requirements, it is your responsibility to secure it appropriately via other means.
The original intent is that the internal endpoint sit on an internal only network, not accessible outside of your border.

The Mule Documentation is sparse in regards to this topic, this project will demonstrate successful configuration.

## Mule Flow ##

The example flow can be found at src/main/app/ws-security-gateway.xml. This flow delegates most all of it's configuration to 3 property files (defaults 
bundled in src/main/resources):

### environment.properties ###

This file contains environment specific options like the hostname of the remote endpoint to contact, the address and port to expose the internal endpoint on, the aliases of the certificates to use for encryption and signing, 
and where to find the 'encryption.properties' and 'signature.properties' that need to be fed into Merlin, the cryptography implementation.

The default configuration points to the encryption.properties and signature.properties located in src/main/resources. Deployments in other environments can modify this file to point to filesystem locations (see examples within the file).

### encryption.properties ###

This file accepts properties defined http://ws.apache.org/wss4j/config.html in the 'Crypto properties' section. This file specifically includes the configuration options for encryption operations.
The ''org.apache.ws.security.crypto.merlin.keystore.alias'' property needs to point to the alias in the keystore that references the remote endpoint's public key.

### signature.properties ##

This file accepts properties defined http://ws.apache.org/wss4j/config.html in the 'Crypto properties' section. This file specifically includes the configuration options for signature operations.
The ''org.apache.ws.security.crypto.merlin.keystore.alias'' property needs to point to the alias in the keystore that references our client keypair.


KeyStore Management
==================

1. Acquire the certificate public key required by the remote endpoint. In the next step, ''endpoint.cer'' represents that file.
2. keytool -import -alias remote-endpoint-public-key -keystore mykeystore.jks -file endpoint.cer
    - When prompted, hit enter or type "yes" and hit enter to trust the certificate and import the public key into the keystore.
3. You'll now have a new file named "mykeystore.jks". To see the contents, perform 'keytool -list -keystore mykeystore.jks' (add -v if you want really verbose output).
4. keytool -genkeypair -alias my-client-certificate -validity 1095 -keyalg RSA -keystore mykeystore.jks
    - When prompted for the password, enter the password you set previously when creating mykeystore.jks
    - When prompted **What is your first and last name?** this is the CN of the certificate. Enter something appropriate to identify the client, like *My Web Services Client - environment...*
    - When prompted **What is the name of your organizational unit?** this is the OU of the certificate, enter something like the name of the division of the company you work for.
    - When prompted **What is the name of your organization?** this is the O of the certificate, enter something like the name of the company you represent.
    - When prompted **What is the name of your City or Locality?** this is the L of the certificate, enter something like *Madison*
    - When prompted **What is the name of your State or Province?** this is the ST of the certificate. Don't use abbreviations here, use full state name, like *Wisconsin*
    - When prompted **What is the two-letter country code for this unit?** this is the C of the certificate, enter the appropriate country code, like *US*
    - When prompted for correctness, review and type yes to accept, no to re-enter.
5. keytool -exportcert -keystore mykeystore.jks -alias my-client-certificate -file my-client-certificate.cer
6. The previous step exports the public key from your client certificate. Contact the maintainers of the remote web service endpoint and share with them the my-client-certificate.cer file.

