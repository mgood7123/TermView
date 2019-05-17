package com.example.TermView

import io.realm.Realm

fun consoleRealmObjectInstance(realm: Realm): ConsoleRealmObject {
    var consoleRealmObject: ConsoleRealmObject? = realm.where(ConsoleRealmObject::class.java).findFirst()
    if (consoleRealmObject == null) {
        // The object doesn't exist.
        realm.beginTransaction()
        consoleRealmObject = realm.createObject(ConsoleRealmObject::class.java)
        consoleRealmObject!!.stdout = ""
        realm.commitTransaction()
    }
    return consoleRealmObject
}

