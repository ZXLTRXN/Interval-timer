package com.zxltrxn.intervaltimer

import java.lang.RuntimeException

class WrongCommandException(
    message: String
) : RuntimeException(message)