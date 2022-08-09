import os
import discord
from dotenv import load_dotenv
import command as cmd

load_dotenv()
token = os.getenv("token")
client = discord.Client()


@client.event
async def on_ready():
    print(f'{client.user} has connected to Discord!')


@client.event
async def on_message(message: discord.Message):
    if message.author == client.user:
        return
    content: str = message.content
    if content.startswith("!"):
        full_cmd = content[1:]
        args = full_cmd.split(" ")
        command = cmd.match(args[0])
        if command is not None:
            await command.execute(message.channel, args[1:])


client.run(token)
