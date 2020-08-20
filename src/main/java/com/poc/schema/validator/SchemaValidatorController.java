package com.poc.schema.validator;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class SchemaValidatorController {

    @PostMapping("/api/create")
    public String createCustomer(@RequestBody  CustomerRequest request) throws Exception{

/*        try {
            JSONObject jsonSchema = new JSONObject(
                    new JSONTokener(this.getClass().getResourceAsStream("/CustomerSchema.json")));
            JSONObject jsonSubject = new JSONObject(request);

            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(jsonSubject);
        } catch (ValidationException ve) {
            return ve.getMessage();
        }*/

        /*JsonNode fstabSchema = JsonLoader.fromResource("/CustomerSchema.json");
        JsonNode good = JsonLoader.fromString(new ObjectMapper().writeValueAsString(request));

        LoadingConfiguration cfg = LoadingConfiguration.newBuilder().dereferencing(Dereferencing.INLINE).freeze();
        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
        JsonSchema schema = factory.getJsonSchema(fstabSchema);
        ProcessingReport report = schema.validate(good);*/


        ObjectMapper mapper = new ObjectMapper();
        JsonNode node =  mapper.readTree(this.getClass().getResourceAsStream("/CustomerSchema.json"));
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = factory.getSchema(node);

        JsonNode requestNode = mapper.readTree(mapper.writeValueAsString(request));

        Set<ValidationMessage> errors = schema.validate(requestNode);



        if (CollectionUtils.isEmpty(errors)) {
            return request.toString();
        } else {
/*           return errors.stream().map(v -> {
                String error = v.getMessage();
                return error;
            }).collect(Collectors.joining());*/

           return processValidationErrors(errors);

        }





    }

    private String processValidationErrors(Set<ValidationMessage> errors) {

        return errors.stream().map(ve -> {
            String errorType = ve.getType();
            String errorMessage = "";

            switch(errorType) {

                case "type" :
                    errorMessage = ve.getPath().substring(2).toUpperCase()+ " is Required";
                    break;
                case "maximum" :
                    errorMessage = ve.getMessage();
                    break;
                default:


            }
            return errorMessage;

        }).collect(Collectors.joining());
    }
}
