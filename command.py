from typing import Optional, List
import google

all_commands = {}


class Command:
    def __init__(self, key: str):
        self.keyword = key
        all_commands[key] = self

    async def execute(self, bot, args: List[str]):
        pass


def match(keyword: str) -> Optional[Command]:
    return all_commands[keyword]


class GoogleCommand(Command):
    def __init__(self):
        super().__init__("google")

    async def execute(self, bot, args: List[str]):
        await google.google(bot, args[0])


cmd_google = GoogleCommand()
