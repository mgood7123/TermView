package com.example.TermView

import io.realm.RealmObject
import io.realm.annotations.Required

@Suppress("unused")
open class ConsoleRealmObject : RealmObject() {
    @Required
    var stdin: String = ""
    @Required
    var stdout: String = ""
    @Required
    var stderr: String = ""
}