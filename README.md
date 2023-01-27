# OAuth2.0 Java Microsoft sending emails Deamon app
How to get accesstokens from the Microsoft authorizationserver and use it to send an email on behalf of a user in your organization. This is done with so-called "two-legged OAuth", the application authorizes on behalf of the user. The end user does not have to give permission. 

For sending emails on behalf of personal accounts tree-legged OAuth has to be used where the end user does have to give permission. Because a personal account can not register an app. (If this is what you are looking for this is not the right example.)

The app that has to be registerd can only be done by an admin of a an organization since the admin has to grant permission for the application to have certain  permissions. 

Before you can use this code you have to register an app on the Microsoft Azure Active Directory! An Admin is need to provide sending-email permissions. 

How to register a daemon app in Microsoft Azure Active Directory:
https://learn.microsoft.com/en-us/azure/active-directory/develop/scenario-daemon-app-registration 

You can also read the .PDF file to understand how to register an daemon app.

(There is also an Google library to do this.)
