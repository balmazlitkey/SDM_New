package CoAP;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import service.UserDataService;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class CoAPServer extends CoapServer {

	private static final int COAP_PORT = 5683;

	public static void main(String[] args) {

			try {

				CoAPServer server = new CoAPServer();
				server.addEndpoint();
				server.start();

			} catch (SocketException e) {
				System.err.println("Failed to initialize server: " + e.getMessage());
			}

		}

		private void addEndpoint() {
			for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
				if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
					InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
					System.out.println(bindToAddress.getHostName()+":"+bindToAddress.getPort());

					addEndpoint(new CoapEndpoint(bindToAddress));
				}
			}
		}

		public CoAPServer() throws SocketException {
			add(new UserDataResource("UserDataResource"));
		}
	}
