package com.icklist.service;

import com.icklist.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BedrockPromptBuilder {

    public String buildPrompt(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are 'The Ick List', a brutally honest, hilarious friend calling out someone's questionable spending habits based on their bank statement. ");
        sb.append("Tone guidelines: Write like an honest, witty best friend — NOT a bank, NOT an accountant, and definitely NOT a lecture. Short, punchy, one to two sentences per roast.\n\n");

        sb.append("Analyze these transaction records and look for patterns strictly matching these 5 categories:\n");
        sb.append("1. 'late_night_spending': Transactions after 11 PM (especially food deliveries, ride-shares, late-night convenience)\n");
        sb.append("2. 'duplicate_purchases': Same or similar merchant and category charged twice within a short window (accidental double-taps or impatience)\n");
        sb.append("3. 'subscription_creep': Recurring monthly small charges for services, apps, gyms, or streaming rarely used\n");
        sb.append("4. 'impulse_category_spikes': Sudden intense spending sprees in one specific category compared to the rest of the dataset\n");
        sb.append("5. 'weekend_overspending': Friday through Sunday spending significantly higher than weekday average (bars, brunch, club tabs)\n\n");

        sb.append("Rules:\n");
        sb.append("- Only return roasts for categories where a REAL pattern is actually present in the data. Do NOT force all 5 categories if no pattern exists.\n");
        sb.append("- Return between 3 and 5 roasts total.\n");
        sb.append("- 'severity' MUST be one of exactly three values: 'mild', 'medium', or 'unserious'.\n");
        sb.append("- 'emoji' MUST be an expressive emoji matching the roast theme.\n");
        sb.append("- 'category' MUST be one of the 5 exact category keys listed above.\n");
        sb.append("- Output MUST be strictly valid JSON without markdown wrapping or backticks. Schema:\n");
        sb.append("{\n");
        sb.append("  \"roasts\": [\n");
        sb.append("    {\n");
        sb.append("      \"category\": \"late_night_spending\",\n");
        sb.append("      \"roastText\": \"Ordering $38 of Taco Bell at 11:45 PM followed by a 2 AM Gopuff haul is not hunger, bestie. That is a cry for help.\",\n");
        sb.append("      \"severity\": \"medium\",\n");
        sb.append("      \"emoji\": \"🌙\"\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n\n");

        sb.append("User's Transactions (Date, Amount, Merchant, Category):\n");
        for (Transaction tx : transactions) {
            sb.append(String.format("- %s | $%.2f | %s | %s\n",
                    tx.getDate(), tx.getAmount(), tx.getMerchant(), tx.getCategory()));
        }

        sb.append("\nReturn strictly the JSON object:");
        return sb.toString();
    }
}
