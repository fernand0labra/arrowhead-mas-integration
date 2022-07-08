// Auto generated
package Server;

import eu.generator.resources.RESTResources;
import java.lang.String;
import java.net.SocketException;
import org.eclipse.californium.core.CoapServer;

class ServerApplication extends CoapServer {
  public ServerApplication() throws SocketException {
    add(new RESTResources());
  }

  public static void main(String[] args) {
    try {
      ServerApplication server = new ServerApplication();
      server.start();
    } catch(SocketException e) {
      System.err.println("Failed to initialize server: " + e.getMessage());
    }
  }
}