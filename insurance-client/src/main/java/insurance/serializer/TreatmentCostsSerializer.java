package main.java.insurance.serializer;

import com.owlike.genson.Genson;
import main.java.insurance.model.TreatmentCostsReply;
import main.java.insurance.model.TreatmentCostsRequest;

public class TreatmentCostsSerializer {
    private Genson genson;

    public TreatmentCostsSerializer(){
        genson = new Genson();
    }

    public String requestToString(TreatmentCostsRequest request){
        return genson.serialize(request);
    }

    public TreatmentCostsRequest requestFromString(String str){
        return  genson.deserialize(str, TreatmentCostsRequest.class);
    }

    public String replyToString(TreatmentCostsReply reply){
        return genson.serialize(reply);
    }

    public TreatmentCostsReply replyFromString(String str){
        return genson.deserialize(str, TreatmentCostsReply.class);
    }
}
