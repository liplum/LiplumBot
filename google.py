url_head = "https://www.google.com.hk/search?q="


async def google(bot, keyword: str):
    url = url_head + keyword
    await bot.send(url)
