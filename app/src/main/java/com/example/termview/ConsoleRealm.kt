@file:Suppress("unused", "UNUSED_VARIABLE", "UNUSED_CHANGED_VALUE")

package com.example.TermView

import io.realm.*

const val ConsoleRealmObjectVersion: Long = 2

// Example migration adding a new class
class MigrateConsole : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion

        // DynamicRealm exposes an editable schema
        val schema = realm.schema

        // version 1:
        //   text

        // Migrate to version 2:
        // + stdin
        //   text -> stdout
        // + stderr
        if (oldVersion == 1L) {
            schema.get("ConsoleRealmObject")!!
                .addField("stdin", String::class.java, FieldAttribute.REQUIRED)
                .renameField("text", "stdout")
                .addField("stderr", String::class.java, FieldAttribute.REQUIRED)
            oldVersion++
        }

        // Migrate to version 2: Add a primary key + object references
        // Example:
        // public Person extends RealmObject {
        //     private String name;
        //     @PrimaryKey
        //     private int age;
        //     private Dog favoriteDog;
        //     private RealmList<Dog> dogs;
        //     // getters and setters left out for brevity
        // }
//        if (oldVersion == 1L) {
//            schema.get("Person")!!
//                .addField("id", Long::class.javaPrimitiveType!!, FieldAttribute.PRIMARY_KEY)
//                .addRealmObjectField("favoriteDog", schema.get("Dog")!!)
//                .addRealmListField("dogs", schema.get("Dog")!!)
//            oldVersion++
//        }
    }
}
