import os
import discord
from dotenv import load_dotenv

load_dotenv()
token = os.getenv("token")
client = discord.Client()


@client.event
async def on_ready():
    print(f'{client.user} has connected to Discord!')


@client.event
async def on_message(message):
    if message.author == client.user:
        return
    if message.content == 'hello':
        await message.channel.send("Hi there!")


client.run(token)
