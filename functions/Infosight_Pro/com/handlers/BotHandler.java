//$Id$
package com.handlers;


import static APIcallBacks.FileDownloader.closeCsvFile;
import static APIcallBacks.FileDownloader.downloadedCSVs;
import static APIcallBacks.FileDownloader.fileDictionaryFDJ;
import static APIcallBacks.FileDownloader.openCsvFile;
import static APIcallBacks.FileDownloader.sendFileToBot;

import com.zc.cliq.enums.ACTION_TYPE;
import com.zc.cliq.enums.BUTTON_TYPE;
import com.zc.cliq.enums.CHANNEL_OPERATION;
import com.zc.cliq.enums.SLIDE_TYPE;
import com.zc.cliq.objects.Action;
import com.zc.cliq.objects.ActionData;
import com.zc.cliq.objects.ButtonObject;
import com.zc.cliq.objects.CardDetails;
import com.zc.cliq.objects.Message;
import com.zc.cliq.objects.Slide;
import com.zc.cliq.requests.BotContextHandlerRequest;
import com.zc.cliq.requests.BotMentionHandlerRequest;
import com.zc.cliq.requests.BotMenuActionHandlerRequest;
import com.zc.cliq.requests.BotMessageHandlerRequest;
import com.zc.cliq.requests.BotParticipationHandlerRequest;
import com.zc.cliq.requests.BotWebhookHandlerRequest;
import com.zc.cliq.requests.BotWelcomeHandlerRequest;
import com.zc.cliq.util.ZCCliqUtil;
import com.zc.component.cache.ZCCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import APIcallBacks.FileDownloader;


public class BotHandler implements com.zc.cliq.interfaces.BotHandler {


	public static Map<String, Object> createWelcomeMessageMap(String uName) {
		Map<String, Object> welcomeMessageMap = new HashMap<>();

		// Action Object for the commands button
		Map<String, Object> actionObject = new HashMap<>();
		Map<String, Object> actionData = new HashMap<>();
		actionData.put("name", "Commands");
		actionObject.put("type", "invoke.function");
		actionObject.put("data", actionData);

		// Buttons List
		List<Map<String, Object>> buttonsList = new ArrayList<>();
		Map<String, Object> buttonObject = new HashMap<>();
		buttonObject.put("label", "Commands");
		buttonObject.put("hint", "");
		buttonObject.put("action", actionObject);
		buttonObject.put("key", "");
		buttonsList.add(buttonObject);

		// Card Object
		Map<String, String> cardObject = new HashMap<>();
		cardObject.put("theme", "modern-inline");

		// Populate the welcome message map
		welcomeMessageMap.put("text", "\uD83D\uDC4B Welcome, " + uName + "! I'm Infosight Pro, your dedicated CSV companion! \uD83E\uDD16\u2728\n\nI'm here to simplify your life by effortlessly handling CSV files. Whether you need to organize, analyze, or manipulate your data, I've got you covered! \uD83D\uDCD2\uD83D\uDCC4\n\nJust let me know what you need, and I'll perform operations like sorting, filtering, and much more on your CSV files. Need insights from your data? I can do that too! \uD83D\uDCA1\n\nReady to make your CSV adventures smooth and enjoyable? Let's get started! \uD83D\uDE80\n\nFeel free to ask me anything about your data. \uD83D\uDE0A\n\nClick the button below or type Commands to view all commands.");
		welcomeMessageMap.put("bot", Collections.singletonMap("name", "Infosight Pro"));
		welcomeMessageMap.put("card", cardObject);
		welcomeMessageMap.put("buttons", buttonsList);

		return welcomeMessageMap;
	}



	Logger LOGGER = Logger.getLogger(BotHandler.class.getName());
	private FunctionHandler functionHandler = new FunctionHandler();
	private Map<String, String> previousFileDictionary = new HashMap<>();

	public String filesD(){

		String dFiles = downloadedCSVs;
		return dFiles;
	}
	public String handleGetCsvFiles() {
		// Populate the current file dictionary
		FileDownloader.populateFileDictionary();

		// Display only the changes (additions and deletions)
		StringBuilder changesBuilder = new StringBuilder();

		// Check for added files and mark them in the changesBuilder
		for (String filename : fileDictionaryFDJ.keySet()) {
			if (!previousFileDictionary.containsKey(filename)) {
				changesBuilder.append("Added: ").append(filename).append("\n");
			}
		}

		// Check for deleted files and mark them in the changesBuilder
		for (String filename : previousFileDictionary.keySet()) {
			if (!fileDictionaryFDJ.containsKey(filename)) {
				changesBuilder.append("Deleted: ").append(filename).append("\n");
			}
		}

		// Update the previousFileDictionary for the next iteration
		previousFileDictionary = new HashMap<>(fileDictionaryFDJ);

		// Display the changes
		String changesText = changesBuilder.toString();
		if (!changesText.isEmpty()) {
			System.out.println("Changes in fileDictionaryFDJ:\n" + changesText);
		} else {
			System.out.println("No changes in fileDictionaryFDJ.");
		}

		// Display filenames with numbers
		StringBuilder listBuilder = new StringBuilder();
		int counter = 1;
		for (String filename : fileDictionaryFDJ.keySet()) {
			listBuilder.append(counter++).append(". ").append(filename).append("\n");
		}
		String text = listBuilder.toString();
		System.out.println(text);
		return text;// You can print or use the 'text' as needed
	}


	@Override
	public Map<String, Object> welcomeHandler(BotWelcomeHandlerRequest req) {
		String uName = req.getUser().getFirstName();
		return createWelcomeMessageMap(uName);
	}

	public static String createCommandsMessageMap() {
		String text = ("Sure! Here are the available commands for CSV manipulation:\n\n" +
				"1.   openCsvFile\n" +
				"2.   closeCsvFile\n" +
				"3.   insertRecord\n" +
				"4.   insertColumn\n" +
				"5.   updateRecord\n" +
				"6.   updateColumn\n" +
				"7.   deleteRecord\n" +
				"8.   deleteColumns\n" +
				"9.   sort\n" +
				"10.  undo\n" +
				"11.  displayCsvFile\n" +
				"12.  downloadCsvFile\n\n" +
				"Note: Commands are not case-sensitive.\n"+
				"Feel free to use these commands to interact with me!");
		return text;

	}

	// Function to build the file upload form





	private static final String STATE_OPEN_CSV_FILE = "openCsvFile";
	private static final String STATE_FILE_SELECTION = "fileSelection";
	private static final String STATE_FILE_ClOSE = "fileClose";


	public static Map<String, Object> createFormMap() {

		Map<String, Object> formMap = new HashMap<>();

		// Action Object
		Map<String, String> actionObject = new HashMap<>();
		actionObject.put("type", "invoke.function");
		actionObject.put("name", "function");

		// CSV File Input Object
		Map<String, Object> csvFileInput = new HashMap<>();
		csvFileInput.put("name", "csvFile");
		csvFileInput.put("label", "CSV File");
		csvFileInput.put("placeholder", "Select CSV file to upload");
		csvFileInput.put("mandatory", false);
		csvFileInput.put("type", "file");

		// Inputs List
		List<Map<String, Object>> inputsList = new ArrayList<>();
		inputsList.add(csvFileInput);

		// Form Object
		formMap.put("type", "form");
		formMap.put("title", "Upload CSV File");
		formMap.put("name", "uploadCsvForm");
		formMap.put("hint", "Please upload a CSV file");
		formMap.put("button_label", "Submit");
		formMap.put("inputs", inputsList);
		formMap.put("action", actionObject);

		return formMap;
	}

	@Override
	public Map<String, Object> messageHandler(BotMessageHandlerRequest req) throws JSONException, IOException, InterruptedException {
		try {
			String message = req.getMessage();
			Map<String, Object> resp = new HashMap<>();

			String text = null;
			String state = getState(req.getUser().getId());
			String uName = req.getUser().getFirstName();
			String opDict = handleGetCsvFiles();




			if (message == null) {
				text = "Please enable 'Message' in bot settings";
			}  else if (message.equals("downloadCsvFile") || message.equalsIgnoreCase("downloadCsvFile")) {
				if (!filesD().equals("No open file")){
					String fileSendFlag = sendFileToBot(filesD());
					if (fileSendFlag == "success"){
						text = "File "+ filesD() +" has successfully been sent to project bots";
						resp.put("text",text);
						return resp;
					}
					else if(fileSendFlag == "failure"){
						text = "Failed to send file "+filesD();
						resp.put("text",text);
						return resp;
					}
				}
				else if (filesD().equals("No open file")){
					text = "No files are currently opened!";
					resp.put("text",text);
					return resp;
				}

			} else if (!state.equals(STATE_OPEN_CSV_FILE) && !state.equals(STATE_FILE_SELECTION) && message.equalsIgnoreCase("openCsvFile")) {
				// Allow "openCsvFile" command only when not in file open selection or file selection stage
				if(downloadedCSVs == "No open file"){
					text = "Please select an option to open a CSV file:\n\n" +
							"1. Open CSV file within Zoho Cliq\n" +
							"2. Access CSV file from system\n\n" +
							"Type <Exit> to exit iteration";
					setState(req.getUser().getId(), STATE_OPEN_CSV_FILE);
					resp.put("text", text);
					return resp;
				}
				else{
					text = "File " + downloadedCSVs + " was already opened\n\nDo you want to close it (Y/N)";
					setState(req.getUser().getId(), STATE_FILE_ClOSE);
					resp.put("text", text);
					return resp;

				}

			} else if (state.equals(STATE_FILE_ClOSE)) {
				if (message.equals("Y") || message.equalsIgnoreCase("Y")){
					text = "Success! Closed file '" + downloadedCSVs + "'.";
					// Call the function to delete the specific downloaded file
					closeCsvFile();
					clearState(req.getUser().getId());
				}
				else if(message.equals("N") || message.equalsIgnoreCase("N")){
					text = "Ok make sure to close the file once you are done";
					clearState(req.getUser().getId());
				}
				else{
					text = "Please choose either Y or N";
					resp.put("text", text);
					return resp;
				}
			} else if (state.equals(STATE_OPEN_CSV_FILE)) {
				if (message.equals("1") || message.equalsIgnoreCase("1")) {
					text = "Awesome! Here are the available CSV files.\nType the file name you want to open! \n\n" + opDict + "\nType <Exit> to exit iteration";


					setState(req.getUser().getId(), STATE_FILE_SELECTION);
					// Set the state for file selection
					resp.put("text", text);
					return resp;
				} else if (message.equals("2") || message.equalsIgnoreCase("2")) {
					clearState(req.getUser().getId());
					return createFormMap();

				} else if (message.equals("Exit") || message.equalsIgnoreCase("Exit")) {
					clearState(req.getUser().getId());
					text = "Feel free to reach me out if you need any further assistance!";

				} else {
					text = "Please choose a valid option";
					// Set the message for an invalid option
					resp.put("text", text);
					return resp; // Prompt again for a valid option
				}

				// Reset state after processing the message
			} else if (state.equals(STATE_FILE_SELECTION)) {
				if (fileDictionaryFDJ.containsKey(message)) {
					text = "Success! Opened file '" + message + "'.";
					openCsvFile(message);


					clearState(req.getUser().getId());
					resp.put("text", text);
					return resp;
				} else if(message.equalsIgnoreCase("Exit")){
					text = "Feel free to reach me out if you need any further assistance!";
					clearState(req.getUser().getId());
					resp.put("text", text);
					return resp;
				}
				else {
					text = "'" + message + "' is not available. Please enter a valid file name.";
					resp.put("text", text);
					setState(req.getUser().getId(), STATE_FILE_SELECTION); // Maintain the state for file selection
					return resp;
				}


			} else if (message != null && message.equalsIgnoreCase("Commands")) {
				String cmds = createCommandsMessageMap();
				resp.put("text", cmds);
				return resp;
			}
			else if (message != null && message.equalsIgnoreCase("downloaded csv")) {
				String cmds = filesD();
				resp.put("text", cmds);
				return resp;
			}
			else if (message != null && message.equalsIgnoreCase("closeCsvFile")) {
				// Check if an opened file name is available before closing
				if (downloadedCSVs != "No open file") {
					text = "Success! Closed file '" + downloadedCSVs + "'.";
					// Call the function to delete the specific downloaded file
					closeCsvFile();

				} else {
					text = "No file is currently opened.";
				}
			}

			else {
				text = "Sorry, I'm not programmed yet to do this :sad:";
			}

			resp.put("text", text);
			return resp;

		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Exception in message handler. ", ex);
			throw ex;
		}
	}

	// Helper methods for managing state
	private String getState(String userId) {
		// Replace this with your actual storage mechanism (database, cache, etc.)
		// For simplicity, using a map in this example
		return stateMap.getOrDefault(userId, "");
	}

	private void setState(String userId, String state) {
		// Replace this with your actual storage mechanism (database, cache, etc.)
		// For simplicity, using a map in this example
		stateMap.put(userId, state);
	}

	private void clearState(String userId) {
		// Replace this with your actual storage mechanism (database, cache, etc.)
		// For simplicity, using a map in this example
		stateMap.remove(userId);
	}

	private static Map<String, String> stateMap = new HashMap<>();

	@Override
	public Map<String, Object> contextHandler(BotContextHandlerRequest req) {
		Map<String, Object> resp = new HashMap<String, Object>();
		if (req.getContextId().equals("personal_details")) {
			Map<String, String> answers = req.getAnswers();
			StringBuilder str = new StringBuilder();
			str.append("*Name*: ").append(answers.get("name")).append("\n");
			str.append("*Department*: ").append(answers.get("dept")).append("\n");

			if(answers.get("cache").equals("YES")) {
				try {
					ZCCache cache = ZCCache.getInstance();
					cache.putCacheValue("Name", answers.get("name"), 1L);
					cache.putCacheValue("Department", answers.get("dept"), 1L);
					str.append("This data is now available in Catalyst Cache's default segment.");
				} catch(Exception ex) {
					System.out.print("Error putting the value to cache: " + ex.toString());
				}
			}

			resp.put("text", "Nice ! I have collected your info: \n" + str.toString());
		}
		return resp;
	}

	@Override
	public Map<String, Object> mentionHandler(BotMentionHandlerRequest req) {
		String text = "Hey *" + req.getUser().getFirstName() + "*, thanks for mentioning me here. I'm from Catalyst city";
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("text", text);
		return resp;
	}

	@Override
	public Map<String, Object> menuActionHandler(BotMenuActionHandlerRequest req) {
		Map<String, Object> resp = new HashMap<String, Object>();
		String text;
		if (req.getActionName().equals("Say Hi")) {
			text = "Hi";
		} else if (req.getActionName().equals("Look Angry")) {
			text = ":angry:";
		} else {
			text = "Menu action triggered :fist:";
		}
		resp.put("text", text);
		return resp;
	}

	@Override
	public Map<String, Object> webhookHandler(BotWebhookHandlerRequest req) throws Exception {
		// Sample handler class for incoming mails in ZohoMail
		// Please configure the bot in ZohoMail's outgoing webhooks
		JSONObject reqBody = req.getBody();
		String summary;
		String bodyStr = new StringBuilder("*From*: ").append(reqBody.getString("fromAddress")).append("\n*Subject*: ").append(reqBody.getString("subject")).append("\n*Content*: ").append((summary = reqBody.getString("summary")).length() > 100 ? summary.substring(0, 100) : summary).toString();
		Message msg = Message.getInstance(bodyStr);
		msg.setBot("PostPerson", "https://www.zoho.com/sites/default/files/catalyst/functions-images/icon-robot.jpg");
		CardDetails card = CardDetails.getInstance();
		card.setTitle("New Mail");
		card.setThumbnail("https://www.zoho.com/sites/default/files/catalyst/functions-images/mail.svg");
		msg.setCard(card);

		ButtonObject button = new ButtonObject();
		button.setLabel("Open mail");
		button.setType(BUTTON_TYPE.GREEN_OUTLINE);
		button.setHint("Click to open the mail in a new tab");
		Action action = new Action();
		action.setType(ACTION_TYPE.OPEN_URL);
		ActionData actionData = new ActionData();
		actionData.setWeb("https://mail.zoho.com/zm/#mail/folder/inbox/p/" + reqBody.getLong("messageId"));
		action.setData(actionData);
		button.setAction(action);

		msg.addButton(button);

		Slide gifSlide = Slide.getInstance();
		gifSlide.setType(SLIDE_TYPE.IMAGES);
		gifSlide.setTitle("");
		List<String> obj = new ArrayList<String>() {
			{
				add("https://media.giphy.com/media/efyEShk2FJ9X2Kpd7V/giphy.gif");
			}
		};
		gifSlide.setData(obj);

		msg.addSlide(gifSlide);

		return ZCCliqUtil.toMap(msg);
	}

	@Override
	public Map<String, Object> participationHandler(BotParticipationHandlerRequest req) throws Exception {
		String text;
		if (req.getOperation().equals(CHANNEL_OPERATION.ADDED)) {
			text = "Hi. Thanks for adding me to the channel :smile:";
		} else if (req.getOperation().equals(CHANNEL_OPERATION.REMOVED)) {
			text = "Bye-Bye :bye-bye:";
		} else {
			text = "I'm too a participant of this chat :wink:";
		}
		Message msg = Message.getInstance(text);
		return ZCCliqUtil.toMap(msg);
	}
}