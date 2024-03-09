package com.handlers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.zc.cliq.objects.CommandSuggestion;
import com.zc.cliq.requests.CommandHandlerRequest;

public class CommandHandler implements com.zc.cliq.interfaces.CommandHandler {

	public static void main(String[] args) {
		// Create an instance of CommandHandler
		CommandHandler commandHandler = new CommandHandler();

		// Simulate user input (replace this with the actual input from the bot)
		Scanner scanner = new Scanner(System.in);

		// Use a loop to continuously prompt the user for commands
		while (true) {
			System.out.print("Enter command (or 'exit' to quit): ");
			String userCommand = scanner.nextLine();

			// Check if the user wants to exit
			if ("exit".equals(userCommand)) {
				break; // Exit the loop
			}

			// Check if the user command is "getcsvfile"
			if ("getcsvfile".equals(userCommand)) {
				// Call the handleGetCsvFiles method

			} else {
				// Handle other commands or provide a message for unrecognized commands
				System.out.println("Unrecognized command: " + userCommand);
			}
		}

		// Close the scanner to avoid resource leaks
		scanner.close();
	}

	private Map<String, String> previousFileDictionary = new HashMap<>();





	@Override
	public Map<String, Object> executionHandler(CommandHandlerRequest req) throws Exception {
		Map<String, Object> resp = new HashMap<String, Object>();
		String text = null;

		String commandName = req.getName();
		if (commandName.equals("catalystresource")) {
			List<CommandSuggestion> suggestions = req.getSelections();
			if (suggestions == null || suggestions.isEmpty()) {
				text = "Please select a suggestion from the command";
			} else {
				String prefix = "Take a look at our ";
				if (suggestions.get(0).getTitle().equals("API doc")) {
					text = prefix + "[API Documentation](https://www.zoho.com/catalyst/help/api/introduction/overview.html)";
				} else if (suggestions.get(0).getTitle().equals("CLI doc")) {
					text = prefix + "[CLI Documentation](https://www.zoho.com/catalyst/help/cli-command-reference.html)";
				} else {
					text = prefix + "[help documentation](https://www.zoho.com/catalyst/help/)";
				}
			}
		} else if (commandName.equals("getcsvfiles")) {


		}
		else {
			text = "Command executed successfully!";
		}

		resp.put("text", text);
		return resp;
	}

	@Override
	public List<CommandSuggestion> suggestionHandler(CommandHandlerRequest req) {
		List<CommandSuggestion> suggestionList = new ArrayList<CommandSuggestion>();
		if (req.getName().equals("catalystresource")) {
			CommandSuggestion sugg1 = CommandSuggestion.getInstance("API doc", "Catalyst API documentation", "https://www.zohowebstatic.com/sites/default/files/styles/product-home-page/public/catalyst-icon.png");
			CommandSuggestion sugg2 = CommandSuggestion.getInstance("CLI doc", "Catalyst CLI documentation", "https://www.zohowebstatic.com/sites/default/files/styles/product-home-page/public/catalyst-icon.png");
			CommandSuggestion sugg3 = CommandSuggestion.getInstance("Help doc", "Catalyst Help documentation", "https://www.zohowebstatic.com/sites/default/files/styles/product-home-page/public/catalyst-icon.png");
			suggestionList.add(sugg1);
			suggestionList.add(sugg2);
			suggestionList.add(sugg3);
		}
		return suggestionList;
	}


}