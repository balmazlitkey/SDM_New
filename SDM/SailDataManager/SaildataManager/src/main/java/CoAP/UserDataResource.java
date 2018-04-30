package CoAP;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.UserData;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import service.UserDataService;

import java.util.ArrayList;

public class UserDataResource extends CoapResource {
		private ArrayList<UserData> data;
		private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		private static UserDataService userDataService;


		public UserDataResource(String name) {
			super(name);

			userDataService = new UserDataService();
			userDataService.initDB();

			data = (ArrayList<UserData>) userDataService.getAllUserDatas();
		}

		@Override
		public void handleGET(CoapExchange exchange){
			exchange.respond(CoAP.ResponseCode.VALID,gson.toJson(data));
		}


		@Override
		public void handlePOST(CoapExchange exchange){
			exchange.accept();
			int format = exchange.getRequestOptions().getContentFormat();

			if(format == MediaTypeRegistry.APPLICATION_JSON){

				String json = exchange.getRequestText();
				String responseText = "Received Json '"+ json+"'";
				String userDataJSON = exchange.advanced().getCurrentRequest().getPayloadString();

				UserData userData = gson.fromJson(userDataJSON, UserData.class);
				System.out.println(userData);

				data.add(userData);
				userDataService.saveUserData(userData);

				exchange.respond(CoAP.ResponseCode.CREATED, responseText);
			}
		}

}
