// genai_chat.js

const { GoogleGenerativeAI } = require("@google/generative-ai");

const apiKey = "";
const genAI = new GoogleGenerativeAI(apiKey);
const model = genAI.getGenerativeModel({
  model: "gemini-1.5-flash",
  systemInstruction: "Your name is Shinchan and you are a coding expert and a friendly guy who helps those in need.",
});

const generationConfig = {
  temperature: 1,
  topP: 0.95,
  topK: 64,
  maxOutputTokens: 8192,
  responseMimeType: "text/plain",
};

// Get the input passed from the Java program
const userInput = process.argv[2];  // Command-line argument

if (!userInput) {
  console.error("No input provided.");
  process.exit(1);
}

(async () => {
  try {
    const chatSession = model.startChat({
      generationConfig,
      history: [{ role: "user", parts: [{ text: userInput }] }],
    });

    const result = await chatSession.sendMessage(userInput);
    console.log(result.response.text());
  } catch (error) {
    console.error("Error sending message:", error);
    process.exit(1);
  }
})();
