package com.dg.fendaice

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform