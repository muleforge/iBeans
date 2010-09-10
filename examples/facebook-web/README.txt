This is an example webapp using the Facebook iBean.  It puts your friends on a google map with their pictures.

Setup:
* Run 'mvn clean install' on the Facebook iBean
** Make sure you modify the facebook-ibeans.properties and add your Facebook connect API key and secret
* Replace the value of key for this line in src/main/webapp/index.html:
    <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAAMXiPTfxgqabxarHGTeKxRRgLlDSxjh82zpQ8zvGPGF-jU71mxRXHxXKzIF-Fmed0Iz1WUuSMgyung" type="text/javascript"></script>    
* Replace the value of facebook.api.key in src/main/webapp/META-INF/context.xml with your Facebook API key 
 
Running:
 * Run 'mvn clean tomcat:run' from the facebook example directory
 * Go to http://localhost:8888 to see the running application
 * The first time you run it, you will see a button to connect with facebook
 * After you log into facebook you will see a page with an empty map
 * Click on the 'Map Friends Hometowns' button to see your friends on the map  

Known issues:
* Does not appear to work on safari
* There may be some sync issues and you may not get all of your friends mapped every time
* If you have multiple friends in the same location, only one will show up on the map