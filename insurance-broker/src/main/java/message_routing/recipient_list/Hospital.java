package main.java.message_routing.recipient_list;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

public class Hospital extends HospitalConnection {
    private Evaluator evaluator; // for evaluation of bank rules
    private String rule;

    public Hospital(String hospitalName, String rule, String queueName) {
        super(queueName);
        evaluator = new Evaluator(); // for evaluation of bank rules
        this.rule = rule;
        this.hospitalName = hospitalName;
    }

    @Override
    public boolean canHandleHospitalCostsRequest(String treatmentCode, int age) {
        boolean ruleMatched = false;
        int codeValue = Rule.isRightTreatmentCode(treatmentCode);
        System.out.println("Treatment Code: " + codeValue);
        try {
            evaluator.putVariable("age", Integer.toString(age));
            evaluator.putVariable("codeValue", Integer.toString(codeValue));
            String result = evaluator.evaluate(rule); // evaluate rule
            ruleMatched = result.equals("1.0"); // 1.0 means TRUE, otherwise it is FALSE
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
        return ruleMatched;
    }
}
