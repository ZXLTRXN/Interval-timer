package com.zxltrxn.intervaltimer

class WrongCommandException(
    message: String
) : RuntimeException(message)

class WrongInputTimeException(
    message: String
) : RuntimeException(message)