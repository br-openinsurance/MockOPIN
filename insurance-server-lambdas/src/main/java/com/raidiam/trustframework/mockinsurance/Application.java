package com.raidiam.trustframework.mockinsurance;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import org.postgresql.Driver;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@TypeHint({
  Driver.class,
})
public class Application {

  @Singleton
  static class ObjectMapperBeanEventListener implements BeanCreatedEventListener<ObjectMapper> {

    @Override
    public ObjectMapper onCreated(BeanCreatedEvent<ObjectMapper> event) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
      SimpleModule timeModule = new SimpleModule();
      timeModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
        @Override
        public void serialize(OffsetDateTime offsetDateTime, JsonGenerator gen, SerializerProvider serializers) throws IOException {
          gen.writeString(formatter.format(offsetDateTime));
        }
      });
      return event.getBean()
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
              .setBase64Variant(Base64Variants.MODIFIED_FOR_URL)
              .registerModule(timeModule);
    }
  }

  public static void main(String[] args) {
    Micronaut.run(Application.class);
  }
}
