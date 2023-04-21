/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.finto.fineract.sdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.GsonFireBuilder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class JSON {

    private final Gson gson;
    private final DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();
    private final SqlDateTypeAdapter sqlDateTypeAdapter = new SqlDateTypeAdapter();
    private final OffsetDateTimeTypeAdapter offsetDateTimeTypeAdapter = new OffsetDateTimeTypeAdapter();
    private final LocalDateTypeAdapter localDateTypeAdapter = new LocalDateTypeAdapter();

    public JSON() {
        gson = new GsonFireBuilder().createGsonBuilder().registerTypeAdapter(Date.class, dateTypeAdapter)
                .registerTypeAdapter(java.sql.Date.class, sqlDateTypeAdapter)
                .registerTypeAdapter(OffsetDateTime.class, offsetDateTimeTypeAdapter)
                .registerTypeAdapter(LocalDate.class, localDateTypeAdapter).create();
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * GSON TypeAdapter for JSR-310 OffsetDateTime type.
     */
    public static class OffsetDateTimeTypeAdapter extends TypeAdapter<OffsetDateTime> {

        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        @Override
        public void write(JsonWriter out, OffsetDateTime date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(date));
            }
        }

        @Override
        public OffsetDateTime read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    String date = in.nextString();
                    if (date.endsWith("+0000")) {
                        date = date.substring(0, date.length() - 5) + "Z";
                    }
                    return OffsetDateTime.parse(date, formatter);
            }
        }
    }

    /**
     * GSON TypeAdapter for JSR-310 LocalDate type, which Fineract API's RETURNS as JSON array in the
     * <tt>[2009,1,1]</tt> format, but EXPECTS as String not Array and with a locale and dateFormat. Weird, but so it is
     * (see FINERACT-1220 & FINERACT-1233).
     */
    public static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

        // NB this format is referenced from io.finto.fineract.sdk.util.FineractClient.DATE_FORMAT
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(date));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                case STRING:
                    String dateString = in.nextString();
                    if (dateString != null && dateString.length() != 0) {
                        return LocalDate.parse(dateString);
                    }
                    return null;
                case BEGIN_ARRAY:
                    in.beginArray();
                    int year = in.nextInt();
                    int month = in.nextInt();
                    int day = in.nextInt();
                    in.endArray();
                    return LocalDate.of(year, month, day);
                default:
                    throw new JsonParseException(
                            "Fineract's API normally always sends LocalDate as e.g. [2009,1,1].. or does it not?! (FINERACT-1220)");
            }
        }
    }

    /**
     * GSON TypeAdapter for java.sql.Date type. If the dateFormat is null, a simple "yyyy-MM-dd" format will be used
     * (more efficient than SimpleDateFormat).
     */
    public static class SqlDateTypeAdapter extends TypeAdapter<java.sql.Date> {

        private final DateFormat dateFormat = null; // TODO FINERACT-1220

        @Override
        public void write(JsonWriter out, java.sql.Date date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                String value;
                if (dateFormat != null) {
                    value = dateFormat.format(date);
                } else {
                    value = date.toString();
                }
                out.value(value);
            }
        }

        @Override
        public java.sql.Date read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    String date = in.nextString();
                    try {
                        if (dateFormat != null) {
                            return new java.sql.Date(dateFormat.parse(date).getTime());
                        }
                        return new java.sql.Date(ISO8601Utils.parse(date, new ParsePosition(0)).getTime());
                    } catch (ParseException e) {
                        throw new JsonParseException(e);
                    }
            }
        }
    }

    /**
     * GSON TypeAdapter for java.util.Date type. If the dateFormat is null, ISO8601Utils will be used.
     */
    public static class DateTypeAdapter extends TypeAdapter<Date> {

        private final DateFormat dateFormat = null; // TODO FINERACT-1220

        @Override
        public void write(JsonWriter out, Date date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                String value;
                if (dateFormat != null) {
                    value = dateFormat.format(date);
                } else {
                    value = ISO8601Utils.format(date, true);
                }
                out.value(value);
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            try {
                switch (in.peek()) {
                    case NULL:
                        in.nextNull();
                        return null;
                    default:
                        String date = in.nextString();
                        try {
                            if (dateFormat != null) {
                                return dateFormat.parse(date);
                            }
                            return ISO8601Utils.parse(date, new ParsePosition(0));
                        } catch (ParseException e) {
                            throw new JsonParseException(e);
                        }
                }
            } catch (IllegalArgumentException e) {
                throw new JsonParseException(e);
            }
        }
    }

    // The following is not from the generated JSON class, but from the original generated ApiClient.
    // The original class ApiClass, just like the JSON class, is deleted during the build (see FINERACT-1231).

    /**
     * This wrapper is to take care of this case: when the deserialization fails due to JsonParseException and the
     * expected type is String, then just return the body string.
     */
    public static class GsonResponseBodyConverterToString<T> implements Converter<ResponseBody, T> {

        private final Gson gson;
        private final Type type;

        GsonResponseBodyConverterToString(Gson gson, Type type) {
            this.gson = gson;
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T convert(ResponseBody value) throws IOException {
            String returned = value.string();
            try {
                return gson.fromJson(returned, type);
            } catch (JsonParseException e) {
                return (T) returned;
            }
        }
    }

    public static final class GsonCustomConverterFactory extends Converter.Factory {

        private final Gson gson;
        private final GsonConverterFactory gsonConverterFactory;

        public static GsonCustomConverterFactory create(Gson gson) {
            return new GsonCustomConverterFactory(gson);
        }

        private GsonCustomConverterFactory(Gson gson) {
            if (gson == null) {
                throw new NullPointerException("gson == null");
            }
            this.gson = gson;
            this.gsonConverterFactory = GsonConverterFactory.create(gson);
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (type.equals(String.class)) {
                return new GsonResponseBodyConverterToString<>(gson, type);
            }
            return gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
                Retrofit retrofit) {
            return gsonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        }
    }
}
