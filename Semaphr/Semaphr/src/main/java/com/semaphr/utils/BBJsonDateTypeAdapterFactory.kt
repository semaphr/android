package com.semaphr.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.bind.DateTypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

inline fun <T> tryOptional(expression: () -> T): T? {
    return try {
        expression()
    } catch (ex: Throwable) {
        null
    }
}

class BBJsonDateTypeAdapterFactory : TypeAdapterFactory {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'", Locale.US)
    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSSSSS'Z'", Locale.US)
    val dateFormat3 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val dateFormat4 = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {

        if (type.rawType != Date::class.java){
            return null
        }

        return object: TypeAdapter<Date>() {

            @Throws(IOException::class)
            override  fun write(out: JsonWriter, value: Date?) {
                if (value == null)
                    out.nullValue()
                else {
                    out.value(dateFormat.format(value))
                }
            }

            @Throws(IOException::class)
            override  fun read(input: JsonReader?): Date? {
                return when {
                    input ==  null -> null
                    input.peek() === JsonToken.NULL -> { input.nextNull();  return null }
                    input.peek() == JsonToken.STRING -> {
                        var date: Date? = null
                        var string = input.nextString()
                        if (date == null) {
                            tryOptional {
                                date = dateFormat.parse(string)
                            }
                        }
                        if (date == null) {
                            tryOptional {
                                date = dateFormat2.parse(string)
                            }
                        }
                        if (date == null) {
                            tryOptional {
                                date = dateFormat3.parse(string)
                            }
                        }
                        if (date == null) {
                            date = dateFormat4.parse(string)
                        }
                        return date
                    }
                    input.peek() == JsonToken.NUMBER -> Date(input.nextLong())
                    else -> null
                }


            }

        } as TypeAdapter<T>
    }
}