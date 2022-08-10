package net.liplum.util

import dev.kord.core.entity.Message

val Message.isSentBySelf
    get() = data.author.id == kord.selfId