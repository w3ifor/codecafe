package net.sourcedestination.codecafe.structure.goals;

import net.sourcedestination.codecafe.execution.JShellExerciseTool;
import net.sourcedestination.funcles.tuple.Tuple2;

import java.util.Arrays;

import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

public class MethodDefinitionParameters {

    private final String methodName;
    private final String[] params;

    public static String[] parseSignatureParameters(String signature) {
        return signature.split("\\)")[0].trim().substring(1).split(",");
    }

    public MethodDefinitionParameters(String methodName, String signature) {
        this.methodName = methodName;
        this.params = parseSignatureParameters(signature);
    }

    public String getType() { return "method-definition"; }

    public String getDescription() {
        return "A method with the name '"+methodName+"' must be defined with " +
                params.length + " parameters with types: "+ Arrays.toString(params)+".";
    }

    public Tuple2<Double,String> completionPercentage(JShellExerciseTool tool) {
        var js = tool.getShell();

        // check method name exists
        if(!js.methods().anyMatch(m -> m.name().equals(methodName)))
            return makeTuple(0.0, "Method name is not correct");
        else {
            var actualParams = parseSignatureParameters(
                    js.methods().filter(m -> m.name().equals(methodName)) // find method definition
                            .findFirst().get()        // exactly one should exist
                            .signature());  // retrieve and parse its signature
            // check method has correct parameters
            var correct = 0;
            var mistakes = "";
            for(int i=0; i<params.length; i++) {
                if(i >= actualParams.length) {
                    mistakes += "Parameter #" + (i + 1) + " was not defined. ";
                } else if(!actualParams[i].equals(params[i])){
                    mistakes += "Parameter #" + (i + 1) + " should have been " + params[i] +
                            " but was " + actualParams[i];
                } else {
                    correct++;
                }
            }

            if(mistakes.equals("") && actualParams.length == params.length)
                return makeTuple(1.0, "All tests passed!");

            return makeTuple(0.25 +  // 25% for getting the name right
                            (actualParams.length == params.length ? 0.25 : 0.0) + // 25% for right # of params
                            ((double)correct / actualParams.length)*0.5,          // 50% for correct param types
                    mistakes);
        }
    }
}
