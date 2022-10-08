# DiscordBot/Notification bot

Installation
Java JDK required
Versions for various dependences can be found in pom.xml

Run->App.java

On Discord -> use /get-ques

The bot responsible for sending a message with daily questions easy and hard from the list in public_data folder as well as hourly reminders for next 24 hours.

Roadmap/Improvements to this bot

1. Automate it to send the message at UTC 00:00
2. Add a live timer to the message?




.env.example file comments
TOKEN - bot token from diiscord developers page
PATHTOQUETIONSCSV- path of the file containing questions in CSV format
LEETCODEURL - as name
DELAY - can be set to adjust no of reminders sent throughout the day
